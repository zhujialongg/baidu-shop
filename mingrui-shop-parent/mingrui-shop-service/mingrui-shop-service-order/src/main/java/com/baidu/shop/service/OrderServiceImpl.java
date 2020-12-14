package com.baidu.shop.service;



import com.baidu.base.BaseApiService;
import com.baidu.base.Result;
import com.baidu.business.OrderService;
import com.baidu.shop.config.JwtConfig;
import com.baidu.constant.ShopConstant;
import com.baidu.dto.AddrDTO;
import com.baidu.dto.Car;
import com.baidu.dto.OrderDTO;
import com.baidu.dto.OrderInfo;
import com.baidu.entity.AddrEntity;
import com.baidu.entity.OrderDetailEntity;
import com.baidu.entity.OrderEntity;
import com.baidu.entity.OrderStatusEntity;
import com.baidu.shop.dto.UserInfo;
import com.baidu.shop.mapper.AddrMapper;
import com.baidu.shop.mapper.OrderDetailMapper;
import com.baidu.shop.mapper.OrderMapper;
import com.baidu.shop.mapper.OrderStatusMapper;
import com.baidu.shop.repository.RedisRepository;
import com.baidu.shop.utils.JwtUtils;
import com.baidu.status.HTTPStatus;
import com.baidu.utils.BaiduBeanUtil;
import com.baidu.utils.IdWorker;
import com.baidu.utils.StringUtil;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName
 * @Description //TODO
 * @Author zhujialong
 * @Date
 * @Version V1.0
 **/
@RestController
public class OrderServiceImpl extends BaseApiService implements OrderService {


    @Resource
    private OrderMapper orderMapper;

    @Resource
    private OrderDetailMapper orderDetailMapper;

    @Resource
    private OrderStatusMapper orderStatusMapper;

    @Autowired
    private JwtConfig jwtConfig;

    @Autowired
    private IdWorker idWorker;

    @Resource
    private RedisRepository redisRepository;


    @Resource
    private AddrMapper addrMapper;


    @Override
    public Result<JSONObject> removeAddrInfoById(String id) {

        if(StringUtil.isEmpty(id)) this.setResultError("id不存在");
        addrMapper.deleteByPrimaryKey(Integer.parseInt(id));

        return this.setResultSuccess();
    }

    @Override
    public Result<List<AddrEntity>> getAddrInfoById(String id) {

        AddrEntity addrEntity = addrMapper.selectByPrimaryKey(Integer.parseInt(id));
        return this.setResultSuccess(addrEntity);
    }

    @Override
    public Result<List<AddrEntity>> searchAddr(String token) {
        List<AddrEntity> addrEntities = null;
        try {
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());

            Example example = new Example(AddrEntity.class);
            example.createCriteria().andEqualTo("userId",userInfo.getId());
            addrEntities = addrMapper.selectByExample(example);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.setResultSuccess(addrEntities);
    }

    @Transactional
    @Override
    public Result<JSONObject> saveAddr(AddrDTO addrDTO,String token) {

        try {
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());

            AddrEntity addrEntity = new AddrEntity();

            if(addrDTO.getId() != null && addrDTO.getId() != 0){
                addrEntity.setId(addrDTO.getId());
                AddrEntity   addrEntity1 = BaiduBeanUtil.copyProperties(addrDTO, AddrEntity.class);
                addrEntity1.setUserId(Integer.toString(userInfo.getId()));
                addrMapper.updateByPrimaryKeySelective(addrEntity1);
            } else {
                AddrEntity addrEntity1 = BaiduBeanUtil.copyProperties(addrDTO, AddrEntity.class);
                addrEntity1.setUserId(Integer.toString(userInfo.getId()));
                int i = addrMapper.insertSelective(addrEntity1);
                if(i < 1) this.setResultError("新增失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return this.setResultSuccess();
    }

    @Override
    public Result<OrderInfo> getOrderInfoByOrderId(Long orderId) {

        //根据订单id查询 订单信息
        OrderEntity orderEntity = orderMapper.selectByPrimaryKey(orderId);
        OrderInfo orderInfo = BaiduBeanUtil.copyProperties(orderEntity, OrderInfo.class);

        Example example = new Example(OrderDetailEntity.class);
        example.createCriteria().andEqualTo("orderId");

        List<OrderDetailEntity> orderDetailEntityList = orderDetailMapper.selectByExample(example);
        orderInfo.setOrderDetailList(orderDetailEntityList);

        OrderStatusEntity orderStatusEntity = orderStatusMapper.selectByPrimaryKey(orderInfo.getOrderId());

        orderInfo.setOrderStatusEntity(orderStatusEntity);
        return this.setResultSuccess(orderInfo);
    }

    @Transactional
    @Override
    public Result<String> createOrder(OrderDTO orderDTO, String token) {

        long orderId = idWorker.nextId();
        Date date = new Date();
        try {
            //用户信息
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());
            //1、生成订单
            OrderEntity orderEntity = new OrderEntity();

            orderEntity.setOrderId(orderId);
            orderEntity.setUserId(userInfo.getId() + "");
            orderEntity.setPaymentType(orderDTO.getPayType()); //支付类型
            orderEntity.setBuyerMessage("很好");//买家留言
            orderEntity.setBuyerNick(userInfo.getUsername());//买家昵称
            orderEntity.setInvoiceType(1); //发票类型
            orderEntity.setSourceType(1); //订单来源
            orderEntity.setBuyerRate(ShopConstant.NOT_BUYER_RATE); //用户是否评论 1：没有 应该用常量或 枚举来写
            orderEntity.setCreateTime(date);

            //2、订单详情
            String skuIds = orderDTO.getSkuIds();

            //定义个总价
            List<Long> totalPrice = Arrays.asList(0L);

            List<OrderDetailEntity> orderDetailEntityList = Arrays.asList(skuIds.split(",")).stream().map(skuId -> {

                //获得car值
                Car car = redisRepository.getHash(ShopConstant.GOODS_CAR_PRE + userInfo.getId(), skuId, Car.class);

                OrderDetailEntity orderDetailEntity = new OrderDetailEntity();
                if(car == null){
                    throw new RuntimeException("数据异常");
                }
                orderDetailEntity.setOrderId(orderId);
                orderDetailEntity.setImage(car.getImage());
                orderDetailEntity.setTitle(car.getTitle());
                orderDetailEntity.setNum(car.getNum());
                orderDetailEntity.setPrice(car.getPrice());
                orderDetailEntity.setSkuId(Long.valueOf(skuId));
                orderDetailEntity.setOwnSpec(car.getOwnSpec());
                totalPrice.set(0, car.getPrice() * car.getNum() + totalPrice.get(0));
                return orderDetailEntity;
            }).collect(Collectors.toList());

            orderEntity.setTotalPay(totalPrice.get(0)); //总金额
            orderEntity.setActualPay(totalPrice.get(0)); //实际 上要减去 或活动优惠价

            //3、订单状态
            OrderStatusEntity orderStatusEntity = new OrderStatusEntity();
            orderStatusEntity.setCreateTime(date);
            orderStatusEntity.setOrderId(orderId);
            orderStatusEntity.setStatus(1); //未支付

            //入库
            orderMapper.insertSelective(orderEntity);
            orderDetailMapper.insertList(orderDetailEntityList);
            orderStatusMapper.insertSelective(orderStatusEntity);

            //删除redis中数据
            Arrays.asList(skuIds.split(",")).stream().forEach(skuId ->{
                redisRepository.delHash(ShopConstant.GOODS_CAR_PRE +userInfo.getId() , skuId);
            });
            //mysql 与 redis 双写一致性

        } catch (Exception e) {
            e.printStackTrace();
        }

        return this.setResult(HTTPStatus.OK,"",orderId + "");
    }
}
