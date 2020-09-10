package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.BrandDTO;
import com.baidu.shop.dto.SkuDTO;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.entity.*;
import com.baidu.shop.mapper.*;
import com.baidu.shop.service.BrandService;
import com.baidu.shop.service.GoodsService;
import com.baidu.shop.status.HTTPStatus;
import com.baidu.shop.utils.BaiduBeanUtil;
import com.baidu.shop.utils.ObjectUtil;
import com.baidu.shop.utils.StringUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @ClassName
 * @Description //TODO
 * @Author zhujialong
 * @Date
 * @Version V1.0
 **/
@RestController
public class GoodsServiceImpl extends BaseApiService implements GoodsService {

    @Resource
    private SpuMapper spuMapper;

    @Resource
    private BrandService brandService;

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private StockMapper stockMapper;

    @Resource
    private SpuDetailMapper spuDetailMapper;

    @Resource
    private SkuMapper skuMapper;


    @Transactional
    @Override
    public Result<JSONObject> editSaleable(Integer id) {


        if (ObjectUtil.isNull(id)) return this.setResultError("id不存在");

        Example example = new Example(SpuEntity.class);
        example.createCriteria().andEqualTo("id", id);
        List<SpuEntity> spuEntities = spuMapper.selectByExample(example);

        if (spuEntities.size() == 1) {
            if (spuEntities.get(0).getSaleable() == 1) {
                spuMapper.updateByspuId(spuEntities.get(0).getId());
            } else {
                spuMapper.update2ByspuId(spuEntities.get(0).getId());
            }
        }
        return this.setResultSuccess();
    }

    @Transactional
    @Override
    public Result<JSONObject> reoveGoods(Integer spuId) {

        // 判断 spuId
        if(ObjectUtil.isNotNull(spuId)){
            //删除spu
            spuMapper.deleteByPrimaryKey(spuId);
            //删除spuDetail
            spuDetailMapper.deleteByPrimaryKey(spuId);
            //删除 sku
            List<Long> skuIdArr = this.getSkuIdArr(spuId);

            if(skuIdArr.size()>0){
                //删除 skus
                skuMapper.deleteByIdList(skuIdArr);
                //删除 stock
                stockMapper.deleteByIdList(skuIdArr);
            }
        }
        return this.setResultSuccess();
    }

    //删除和修改
    public  List<Long>  getSkuIdArr(Integer id){

        Example example = new Example(SkuEntity.class);
        example.createCriteria().andEqualTo("spuId",id);
        List<SkuEntity> skuEntities = skuMapper.selectByExample(example);
        List<Long> skuIdArr = skuEntities.stream().map(sku -> sku.getId()).collect(Collectors.toList());

        return skuIdArr;
    }

    @Transactional
    @Override
    public Result<JSONObject> editGoods(SpuDTO spuDTO) {

        //修改spu
        Date date = new Date();
        SpuEntity spuEntity = BaiduBeanUtil.copyProperties(spuDTO, SpuEntity.class);
        spuEntity.setLastUpdateTime(date);
        spuMapper.updateByPrimaryKeySelective(spuEntity);

        //修改 spuDetail 用的是 spuId
        spuDetailMapper.updateByPrimaryKeySelective(BaiduBeanUtil.copyProperties(spuDTO.getSpuDetail(),SpuDetailEntity.class));
        //修改  sku
        //先删除 后新增 直接修改  没有 依据的条件
        List<Long> skuIdArr = this.getSkuIdArr(spuDTO.getId());

        if(skuIdArr.size()>0){
            skuMapper.deleteByIdList(skuIdArr);
           stockMapper.deleteByIdList(skuIdArr);
        //新增
        List<SkuDTO> skus = spuDTO.getSkus();
        Integer spuId = spuDTO.getId();
        this.saveSkuAndStock(skus,spuId,date);
        }
        return this.setResultSuccess();

    }

    //保存 sku 和 stock
    public void saveSkuAndStock(List<SkuDTO> skus,Integer spuId,Date date){
        skus.stream().forEach((skuDTO)->{
            SkuEntity skuEntity = BaiduBeanUtil.copyProperties(skuDTO, SkuEntity.class);
            skuEntity.setSpuId(spuId);
            skuEntity.setCreateTime(date);
            skuEntity.setLastUpdateTime(date);
            skuMapper.insertSelective(skuEntity);

            StockEntity stockEntity = new StockEntity();
            stockEntity.setSkuId(skuEntity.getId());
            stockEntity.setStock(skuDTO.getStock());
            stockMapper.insertSelective(stockEntity);
        });
    }

    @Override
    public Result<List<SkuDTO>> getSkuBySpuId(Integer spuId) {

        List<SkuDTO> list = skuMapper.selectSkuAndStockBySpuId(spuId);

        return this.setResultSuccess(list);
    }

    @Override
    public Result<SpuDetailEntity> getSpuDetailBydSpu(Integer spuId) {

        SpuDetailEntity spuDetailEntity = spuDetailMapper.selectByPrimaryKey(spuId);

        return this.setResultSuccess(spuDetailEntity);
    }

    @Transactional
    @Override
    public Result<JSONObject> saveGoods(SpuDTO spuDTO) {

        System.out.println(spuDTO);
        //新增spu
        SpuEntity spuEntity = BaiduBeanUtil.copyProperties(spuDTO, SpuEntity.class);
        spuEntity.setSaleable(1);
        spuEntity.setValid(1);
        final Date date = new Date();
        spuEntity.setCreateTime(date);
        spuEntity.setLastUpdateTime(date);
        spuMapper.insertSelective(spuEntity);

        //新增 spuDetail
        SpuDetailEntity spuDetailEntity = BaiduBeanUtil.copyProperties(spuDTO.getSpuDetail(), SpuDetailEntity.class);
        //这是spu新增返回 主键
        spuDetailEntity.setSpuId(spuEntity.getId());
        spuDetailMapper.insertSelective(spuDetailEntity);

        //新增sku
        this.saveSkuAndStock(spuDTO.getSkus(),spuEntity.getId(),date);

        return this.setResultSuccess();
    }

    @Override
    public Result<List<SpuDTO>> getSpuInfo(SpuDTO spuDTO) {

        //分页
        if (ObjectUtil.isNotNull(spuDTO.getPage())
                && ObjectUtil.isNotNull(spuDTO.getRows())) {
            PageHelper.startPage(spuDTO.getPage(), spuDTO.getRows());
        }
        //构建条件查询
        Example example = new Example(SpuEntity.class);
        this.exampleFun(example,spuDTO);
        //排序
        if (ObjectUtil.isNotNull(spuDTO.getSort())) {
            example.setOrderByClause(spuDTO.getOrderByClause());
        }
        List<SpuEntity> list = spuMapper.selectByExample(example);

        //查询 品牌和分类名称
        List<SpuDTO> dtoList = this.getBrandNameAndCateName(list);
        PageInfo<SpuEntity> pageInfo = new PageInfo<>(list);
        HashMap<String, Object> map = new HashMap<>();

        long total = pageInfo.getTotal();

        return this.setResult(HTTPStatus.OK,total+"",dtoList);
    }
    //封装 查询
    public void exampleFun(Example example,SpuDTO spuDTO){

        Example.Criteria criteria = example.createCriteria();
        if (StringUtil.isNotEmpty(spuDTO.getTitle()))
            criteria.andLike("title", "%" + spuDTO.getTitle() + "%");

        if (ObjectUtil.isNotNull(spuDTO.getSaleable())
                && spuDTO.getSaleable() != 2) {
            criteria.andEqualTo("saleable", spuDTO.getSaleable());
        }
    }

    //封装 查询
    public  List<SpuDTO>  getBrandNameAndCateName(List<SpuEntity> list){

        List<SpuDTO> dtoList = list.stream().map(spuEntity -> {
            SpuDTO spuDTO1 = BaiduBeanUtil.copyProperties(spuEntity, SpuDTO.class);
            //品牌名称
            BrandDTO brandDTO = new BrandDTO();
            brandDTO.setId(spuEntity.getBrandId());
            Result<PageInfo<BrandEntity>> brandInfo = brandService.getBrandInfo(brandDTO);

            if (ObjectUtil.isNotNull(brandInfo)) {
                PageInfo<BrandEntity> data = brandInfo.getData();
                List<BrandEntity> list1 = data.getList();
                if (!list1.isEmpty() && list1.size() == 1) {
                    spuDTO1.setBrandName(list1.get(0).getName());
                }
            }
            //分类名称
            String  categoryName = categoryMapper.getByCateId(spuDTO1.getCid1(), spuDTO1.getCid2(), spuDTO1.getCid3());
            spuDTO1.setCategoryName(categoryName);
            return spuDTO1;
        }).collect(Collectors.toList());

        return dtoList;
    }



}
