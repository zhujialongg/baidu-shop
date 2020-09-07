package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.BrandDTO;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.entity.CategoryBrandEntity;
import com.baidu.shop.mapper.BrandMapper;
import com.baidu.shop.mapper.CategoryBrandMapper;
import com.baidu.shop.service.BrandService;
import com.baidu.shop.utils.BaiduBeanUtil;
import com.baidu.shop.utils.ObjectUtil;
import com.baidu.shop.utils.PinyinUtil;
import com.baidu.shop.utils.StringUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.JsonObject;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
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
public class BrandServiceImpl extends BaseApiService implements BrandService{

    @Resource
    private BrandMapper brandMapper;

    @Resource
    private CategoryBrandMapper categoryBrandMapper;

    @Override
    public Result<PageInfo<BrandEntity>> getBrandInfo(BrandDTO brandDTO) {

        //分页
        if(ObjectUtil.isNotNull(brandDTO.getPage())
                && ObjectUtil.isNotNull(brandDTO.getRows()))
            PageHelper.startPage(brandDTO.getPage(),brandDTO.getRows());

        //条件查询  排序
        Example example = new Example(BrandEntity.class);

        if(StringUtil.isNotEmpty(brandDTO.getSort()))  example.setOrderByClause(brandDTO.getOrderByClause());

        //为 spu中根据id查询 品牌添加 根据id条件查询
        Example.Criteria criteria = example.createCriteria();
        if(ObjectUtil.isNotNull(brandDTO.getId())){
            criteria.andEqualTo("id",brandDTO.getId());
        }

        //若涉及到多个查询 的话  需要单拎出来 example.createCriteria()  只会执行最后一个查询
        if(StringUtil.isNotEmpty(brandDTO.getName())){
            criteria.andLike("name","%"+brandDTO.getName()+"%");
        }

        //查询
        List<BrandEntity> list = brandMapper.selectByExample(example);
        //返回页面信息
        PageInfo<BrandEntity> pageInfo = new PageInfo<>(list);

        return this.setResultSuccess(pageInfo);
    }

    @Transactional
    @Override
    public Result<JSONObject> save(BrandDTO brandDTO) {

        //java中一个方法的大小
        BrandEntity brandEntity = BaiduBeanUtil.copyProperties(brandDTO, BrandEntity.class);

        //高级代码
        //转换首字母大写操作
        //获取到品牌名称
        //获取到品牌名称的第一个字符
        //将第一个字符转换为Pinyin  拼音
        //获取拼音的首字母
        //统一转换为大写
        brandEntity.setLetter(PinyinUtil.getUpperCase(
                String.valueOf(brandEntity.getName().charAt(0)),
                PinyinUtil.TO_FIRST_CHAR_PINYIN).charAt(0));
        //新增返回主键
        brandMapper.insertSelective(brandEntity);

            //高级代码  省去了  map发挥一个数组  arrayList.add(categoryBrandEntity);
            //通过split分割字符串的Array
            //Arrays.asList将Array转换为List
            //使用jdk1.8 的stream  可以提高效率
            //使用map函数返回一个新的数据
            //collect 转换集合类型Stream<T>
            //Collectors.toList())将集合转换为List类型
            //调用用优化代码
            this.insertCategoryInfo(brandDTO,brandEntity);


        return this.setResultSuccess();
    }

    @Transactional
    @Override
    public Result<JsonObject> edit(BrandDTO brandDTO) {
        BrandEntity brandEntity = BaiduBeanUtil.copyProperties(brandDTO, BrandEntity.class);
        brandEntity.setLetter(PinyinUtil.getUpperCase(
                String.valueOf(brandEntity.getName().charAt(0)),
                PinyinUtil.TO_FIRST_CHAR_PINYIN).charAt(0));

        //执行修改操作
        brandMapper.updateByPrimaryKeySelective(brandEntity);

        //用过brandId删除 中间表数据
        this.deleteCategoryAndBrand(brandEntity.getId());

        //新增新的数据
        //调用用优化代码
        this.insertCategoryInfo(brandDTO,brandEntity);



        return this.setResultSuccess();

    }

    //优化 保存 关系
    public void insertCategoryInfo(BrandDTO brandDTO,BrandEntity brandEntity){

        if(brandDTO.getCategories().contains(",")){
            List<CategoryBrandEntity> list = Arrays.asList(brandDTO.getCategories().split(","))
                    .stream().map(cid -> {
                        CategoryBrandEntity categoryBrandEntity = new CategoryBrandEntity();
                        categoryBrandEntity.setBrandId(brandEntity.getId());
                        categoryBrandEntity.setCategoryId(StringUtil.toInteger(cid));

                        return categoryBrandEntity;
                    }).collect(Collectors.toList());
            //批量新增
            categoryBrandMapper.insertList(list);
        }else{
            CategoryBrandEntity categoryBrandEntity = new CategoryBrandEntity();
            categoryBrandEntity.setBrandId(brandEntity.getId());
            categoryBrandEntity.setCategoryId(StringUtil.toInteger(brandDTO.getCategories()));

            categoryBrandMapper.insertSelective(categoryBrandEntity);
        }

    }

    @Transactional
    @Override
    public Result<JSONObject> remove(Integer id) {

        //删除品牌
        brandMapper.deleteByPrimaryKey(id);


        //删除关系
        this.deleteCategoryAndBrand(id);

        return this.setResultSuccess();
    }

    //优化删除关系
    public void deleteCategoryAndBrand(Integer id){
        Example example = new Example(CategoryBrandEntity.class);
        example.createCriteria().andEqualTo("brandId",id);
        categoryBrandMapper.deleteByExample(example);

    }

}
