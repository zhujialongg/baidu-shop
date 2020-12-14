package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.dto.BrandDTO;
import com.baidu.entity.BrandEntity;
import com.baidu.entity.CategoryBrandEntity;
import com.baidu.entity.SpuEntity;
import com.baidu.shop.mapper.CategoryBrandMapper;
import com.baidu.shop.mapper.SpuMapper;
import com.baidu.base.BaseApiService;
import com.baidu.base.Result;
import com.baidu.shop.mapper.BrandMapper;
import com.baidu.service.BrandService;
import com.baidu.utils.ObjectUtil;
import com.baidu.utils.BaiduBeanUtil;
import com.baidu.utils.PinyinUtil;
import com.baidu.utils.StringUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.JsonObject;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
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
public class BrandServiceImpl extends BaseApiService implements BrandService {

    @Resource
    private BrandMapper brandMapper;

    @Resource
    private CategoryBrandMapper categoryBrandMapper;

    @Resource
    private SpuMapper spuMapper;


    @Override
    public Result<List<BrandEntity>> getBrandByIds(String brandIds) {
        String[] idArr = brandIds.split(",");

        List<Integer> brandIdList = Arrays.asList(idArr).stream().map(idStr -> {
            return Integer.parseInt(idStr);   //字符串转Integer
        }).collect(Collectors.toList());
        //通过id 的List集合查询 泛型为Integer
        List<BrandEntity> list = brandMapper.selectByIdList(brandIdList);
        return this.setResultSuccess(list);
    }

    @Override
    public Result<List<BrandEntity>> getBrandByCategory(Integer cid) {

        if (ObjectUtil.isNotNull(cid)) {
            List<BrandEntity> list = brandMapper.getBrandByCategory(cid);
            return this.setResultSuccess(list);
        }
        return null;
    }

    @Override
    public Result<PageInfo<BrandEntity>> getBrandInfo(BrandDTO brandDTO) {

        //分页
        if (ObjectUtil.isNotNull(brandDTO.getPage())
                && ObjectUtil.isNotNull(brandDTO.getRows()))
            PageHelper.startPage(brandDTO.getPage(), brandDTO.getRows());

        //条件查询  排序
        Example example = new Example(BrandEntity.class);

        if (StringUtil.isNotEmpty(brandDTO.getSort())) example.setOrderByClause(brandDTO.getOrderByClause());

        //为 spu中根据id查询 品牌添加 根据id条件查询
        Example.Criteria criteria = example.createCriteria();
        if (ObjectUtil.isNotNull(brandDTO.getId())) {
            criteria.andEqualTo("id", brandDTO.getId());
        }

        //若涉及到多个查询 的话  需要单拎出来 example.createCriteria()  只会执行最后一个查询
        if (StringUtil.isNotEmpty(brandDTO.getName())) {
            criteria.andLike("name", "%" + brandDTO.getName() + "%");
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

        BrandEntity brandEntity = BaiduBeanUtil.copyProperties(brandDTO, BrandEntity.class);

        brandEntity.setLetter(PinyinUtil.getUpperCase(
                String.valueOf(brandEntity.getName().charAt(0)),
                PinyinUtil.TO_FIRST_CHAR_PINYIN).charAt(0));
        //新增返回主键
        brandMapper.insertSelective(brandEntity);

        this.insertCategoryInfo(brandDTO, brandEntity);


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

        //新增新的数据 调用用优化代码
        this.insertCategoryInfo(brandDTO, brandEntity);

        return this.setResultSuccess();

    }

    //优化 保存 关系
    public void insertCategoryInfo(BrandDTO brandDTO, BrandEntity brandEntity) {

        if (brandDTO.getCategories().contains(",")) {
            List<CategoryBrandEntity> list = Arrays.asList(brandDTO.getCategories().split(","))
                    .stream().map(cid -> {
                        CategoryBrandEntity categoryBrandEntity = new CategoryBrandEntity();
                        categoryBrandEntity.setBrandId(brandEntity.getId());
                        categoryBrandEntity.setCategoryId(StringUtil.toInteger(cid));

                        return categoryBrandEntity;
                    }).collect(Collectors.toList());
            //批量新增
            categoryBrandMapper.insertList(list);
        } else {
            CategoryBrandEntity categoryBrandEntity = new CategoryBrandEntity();
            categoryBrandEntity.setBrandId(brandEntity.getId());
            categoryBrandEntity.setCategoryId(StringUtil.toInteger(brandDTO.getCategories()));

            categoryBrandMapper.insertSelective(categoryBrandEntity);
        }

    }

    @Transactional
    @Override
    public Result<JSONObject> remove(Integer id) {

        //判断是否绑定 sku
        Example example = new Example(SpuEntity.class);
        example.createCriteria().andEqualTo("brandId", id);
        List<SpuEntity> spuEntities = spuMapper.selectByExample(example);
        if (spuEntities.size() > 0) {
            return this.setResultError("该品牌绑定spu,不能被删除!");
        }
        //删除品牌
        brandMapper.deleteByPrimaryKey(id);
        //删除关系
        this.deleteCategoryAndBrand(id);

        return this.setResultSuccess();
    }

    //优化删除关系
    public void deleteCategoryAndBrand(Integer id) {
        Example example = new Example(CategoryBrandEntity.class);
        example.createCriteria().andEqualTo("brandId", id);
        categoryBrandMapper.deleteByExample(example);
    }


}
