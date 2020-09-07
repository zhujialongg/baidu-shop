package com.baidu.shop.service.impl;

import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.BrandDTO;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.entity.CategoryEntity;
import com.baidu.shop.entity.SpuEntity;
import com.baidu.shop.mapper.CategoryMapper;
import com.baidu.shop.mapper.SpuMapper;
import com.baidu.shop.service.BrandService;
import com.baidu.shop.service.GoodsService;
import com.baidu.shop.status.HTTPStatus;
import com.baidu.shop.utils.BaiduBeanUtil;
import com.baidu.shop.utils.ObjectUtil;
import com.baidu.shop.utils.StringUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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

    @Override
    public Result<List<SpuDTO>> getSpuInfo(SpuDTO spuDTO) {

        //分页
        if (ObjectUtil.isNotNull(spuDTO.getPage())
                && ObjectUtil.isNotNull(spuDTO.getRows())) {
            PageHelper.startPage(spuDTO.getPage(), spuDTO.getRows());
        }
        //构建条件查询
        Example example = new Example(SpuEntity.class);

        Example.Criteria criteria = example.createCriteria();

        if (StringUtil.isNotEmpty(spuDTO.getTitle()))
            criteria.andLike("title", "%" + spuDTO.getTitle() + "%");

        if (ObjectUtil.isNotNull(spuDTO.getSaleable())
                && spuDTO.getSaleable() != 2) {
            criteria.andEqualTo("saleable", spuDTO.getSaleable());
        }
        //排序
        if (ObjectUtil.isNotNull(spuDTO.getSort())) {
            example.setOrderByClause(spuDTO.getOrderByClause());
        }


        List<SpuEntity> list = spuMapper.selectByExample(example);

        //查询 品牌和分类名称
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
            List<CategoryEntity> categoryEntityList = categoryMapper.selectByIdList(Arrays.asList(spuDTO1.getCid1(), spuDTO1.getCid2(), spuDTO1.getCid3()));
            String categoryName = categoryEntityList.stream().map(category -> {
                return category.getName();
            }).collect(Collectors.joining("/"));
            spuDTO1.setCategoryName(categoryName);

            //返回
            return spuDTO1;
        }).collect(Collectors.toList());


        PageInfo<SpuEntity> pageInfo = new PageInfo<>(list);

        //需要返回 总条数 但是pageInfo 中没有总条数
        HashMap<String, Object> map = new HashMap<>();
        //1.可以用 map集合的方式 但 在fegin中会出问题
//        PageInfo<SpuEntity> pageInfo = new PageInfo<>(list);
//        map.put("list", dtoList);
//        map.put("total", pageInfo.getTotal());
        //2.也可以借用 message 没错！！！
        long total = pageInfo.getTotal();


        return this.setResult(HTTPStatus.OK,total+"",dtoList);

    }


}
