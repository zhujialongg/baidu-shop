package com.baidu.shop.service.impl;

import com.baidu.base.BaseApiService;
import com.baidu.base.Result;

import com.baidu.constant.ShopConstant;
import com.baidu.dto.Car;

import com.baidu.entity.SkuEntity;

import com.baidu.shop.comment.LogAnnotation;
import com.baidu.shop.config.JwtConfig;
import com.baidu.shop.dto.UserInfo;
import com.baidu.shop.fegin.GoodsFegin;
import com.baidu.shop.redis.repository.RedisRepository;
import com.baidu.service.CarService;
import com.baidu.shop.utils.JwtUtils;
import com.baidu.utils.JSONUtil;
import com.baidu.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName
 * @Description //TODO
 * @Author zhujialong
 * @Date
 * @Version V1.0
 **/
@RestController(value = "car")
@Slf4j
public class CarServiceImpl extends BaseApiService implements CarService {


    @Autowired
    private RedisRepository redisRepository;


    @Autowired
    private GoodsFegin goodsFegin;

    @Autowired
    private JwtConfig jwtConfig;



    @Override
    public Result<JSONObject> carNumUpdate(Long skuId, Integer type, String token) {

        try {
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());
            Car car = redisRepository.getHash(ShopConstant.GOODS_CAR_PRE + userInfo.getId(), skuId + "", Car.class);

            if(car != null){
                if(type == 1){
                    car.setNum(car.getNum() + 1);
                }else {
                    car.setNum(car.getNum() - 1);
                }
                //保存到redis中
                redisRepository.setHash(ShopConstant.GOODS_CAR_PRE +userInfo.getId(), car.getSkuId()+"", JSONUtil.toJsonString(car));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.setResultSuccess();
    }

    @Override
    public Result<List<Car>> getUserGoodsCar(String token) {

       List<Object> carList = new ArrayList<>();

        try {
            //获取当前登录用户信息
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());
            //通过用户id获取redis中购物车信息  redis中时以 key value 存储的 所以返回map
            Map<String, String> map = redisRepository.getHash(ShopConstant.GOODS_CAR_PRE + userInfo.getId());

            map.forEach((key,value)->{
                //value为 json类型    //将value 转化为Car.class类型
                carList.add(JSONUtil.toBean(value,Car.class));
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.setResultSuccess(carList);
    }

    @Override
    public Result<JSONObject> mergeCar(String clientCarList, String token) {

        //因为Result<JSONObject> 中JSONObject已经导包
        com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(clientCarList);
        log.debug(jsonObject + "");
        List<Car> carList  = com.alibaba.fastjson.JSONObject.parseArray(jsonObject.get("clientCarList").toString(), Car.class);
        carList.stream().forEach(car->{
            this.addCar(car,token); //调用addCar方法传参 合并到 redis中
        });
        return this.setResultSuccess();
    }

    @Override
    @LogAnnotation(operationModel = "购物车模块",operation = "新增商品到购物车",operationType = "新增")
    public Result<JSONObject> addCar(Car car, String token) {

        try {
            //用户信息
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());
            // 根据两个key 获取信息值 Car.class :表示 要转换的类型
            Car redisCar = redisRepository.getHash(ShopConstant.GOODS_CAR_PRE+userInfo.getId(), car.getSkuId() + "", Car.class);

            Car saveCar = null;
            if(redisCar == null){ //购物车为空的情况 就是确定redis不存在当前加入购物车的商品
                Result<SkuEntity> skuResult= goodsFegin.getSkuBySkuId(car.getSkuId());
                if(skuResult.getCode() ==200){
                    SkuEntity skuEntity = skuResult.getData();
                    car.setUserId(userInfo.getId());
                    car.setTitle(skuEntity.getTitle());
                    car.setImage(StringUtil.isEmpty(skuEntity.getImages())?"":skuEntity.getImages().split(",")[0]);
                    car.setPrice(Long.valueOf(skuEntity.getPrice()));
                    //需要处理
                    //key为id
                    //value 为规格参数值
                    //遍历map
                    //重新组装map
                    //将map转换为json字符串
                    car.setOwnSpec(skuEntity.getOwnSpec());
                }
                saveCar  = car;
            }else{
                //购物车不为空的情况下 需要将原来的数量加上
                redisCar.setNum(redisCar.getNum() + car.getNum());
                saveCar = redisCar;
                log.debug("当前用户购物车中有将要新增的商品，重新设置num : {}" , redisCar.getNum());
            }
            //存到 redis中  redis 数据结构  ：Map<String,Map<String,String>>
            redisRepository.setHash(ShopConstant.GOODS_CAR_PRE + userInfo.getId() , car.getSkuId()+"",JSONUtil.toJsonString(saveCar));
            log.debug("新增到redis数据成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.setResultSuccess();
    }





}
