package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.entity.*;
import com.baidu.shop.mapper.*;
import com.baidu.shop.service.CategoryService;
import com.baidu.shop.utils.ObjectUtil;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName
 * @Description //TODO
 * @Author zhujialong
 * @Date
 * @Version V1.0
 **/
@RestController
public class CategoryServiceImpl extends BaseApiService implements CategoryService {

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private SpecGroupMapper specGroupMapper;

    @Resource
    private CategoryBrandMapper categoryBrandMapper;

    @Resource
    private SpuMapper spuMapper;

    @Override
    public Result<List<CategoryEntity>> getCategoryByPid(Integer pid) {

        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setParentId(pid);
        List<CategoryEntity> list = categoryMapper.select(categoryEntity);


        //return new Result<List<CategoryEntity>>(HTTPStatus.OK,"",list);
        return this.setResultSuccess(list);
    }

    @Transactional
    @Override
    public Result<JSONObject> saveCategory(CategoryEntity entity) {

        //通过页面传来的parentId查询parentId的对应的数据是否为父节点  也就是isParent是否为1
        //若!=1 则需要修改为1

        //这里我们为了效率  可以  跳过查询 直接修改
        CategoryEntity categoryEntity = new CategoryEntity();

        categoryEntity.setId(entity.getParentId());
        categoryEntity.setIsParent(1);
        categoryMapper.updateByPrimaryKeySelective(categoryEntity);

        categoryMapper.insertSelective(entity);

        return this.setResultSuccess();
    }

    @Transactional
    @Override
    public Result<JSONObject> editCategory(CategoryEntity entity) {

            categoryMapper.updateByPrimaryKeySelective(entity);

        return this.setResultSuccess();
    }

    @Transactional
    @Override
    public Result<JSONObject> removeCategory(Integer id) {

        //判断分类id是否存在关联品牌
        Example example2 = new Example(CategoryBrandEntity.class);
        example2.createCriteria().andEqualTo("categoryId",id);
        List<CategoryBrandEntity> list2 = categoryBrandMapper.selectByExample(example2);
        if(list2.size()>0){
            return this.setResultError("该分类存在绑定品牌,不能被删除");
        }

        //判断分类id是否存在关联规格组
        Example example1 = new Example(SpecGroupEntity.class);
        example1.createCriteria().andEqualTo("cid",id);
        List<SpecGroupEntity> list1 = specGroupMapper.selectByExample(example1);

        if (list1.size()>0) {
            return this.setResultError("该分类存在绑定规格组,不能被删除");
        }

        //通过工具类  判断  id是否为空
        if(ObjectUtil.isNull(id)){
                return this.setResultError("该id不存在");
        }
        //验证传入的id是否有效,categoryEntity对下面的程序有用
        CategoryEntity categoryEntity = categoryMapper.selectByPrimaryKey(id);

         //判断 是否 绑定spu
        Example example3 = new Example(SpuEntity.class);
        example3.createCriteria().andEqualTo("cid3",id);
        List<SpuEntity> spuEntities = spuMapper.selectByExample(example3);
        if(spuEntities.size()>0){
            return this.setResultError("该分类被spu绑定不可删除");
        }

        //判断 此id是否为父节点
        if(categoryEntity.getIsParent() == 1){
            return this.setResultError("当前节点为父节点,不可删除");
        }

        //条件查询,通过 该节点parentId查询数据
        Example example = new Example(CategoryEntity.class);
        example.createCriteria().andEqualTo("parentId",categoryEntity.getParentId());
        List<CategoryEntity> list = categoryMapper.selectByExample(example);

        //如果查询出的数据只有一条 则将次节点的状态修改为子节点
        if(!list.isEmpty() && list.size() == 1){  //只有一条
        CategoryEntity entity = new CategoryEntity();
        entity.setId(categoryEntity.getParentId());
        entity.setIsParent(0);
        categoryMapper.updateByPrimaryKeySelective(entity);
        }

        //删除操作
        categoryMapper.deleteByPrimaryKey(id);

        return this.setResultSuccess();
    }

    //回显操作
    @Override
    public Result<List<CategoryEntity>> getByBrand(Integer brandId) {

         List<CategoryEntity> listByBrand = categoryMapper.getByBrand(brandId);

        return this.setResultSuccess(listByBrand);
    }
}
