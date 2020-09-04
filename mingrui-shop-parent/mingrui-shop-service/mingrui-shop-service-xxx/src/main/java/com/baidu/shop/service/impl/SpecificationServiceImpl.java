package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SpecGroupDTO;
import com.baidu.shop.dto.SpecParamDTO;
import com.baidu.shop.entity.SpecGroupEntity;
import com.baidu.shop.entity.SpecParamEntity;
import com.baidu.shop.mapper.SpecGroupMapper;
import com.baidu.shop.mapper.SpecParamMapper;
import com.baidu.shop.service.SpecificationService;
import com.baidu.shop.utils.BaiduBeanUtil;
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
public class SpecificationServiceImpl extends BaseApiService implements SpecificationService {


    @Resource
    private SpecGroupMapper specGroupMapper;

    @Resource
    private SpecParamMapper specParamMapper;

    @Override
    public Result<List<SpecGroupEntity>> getSpecGroupInfo(SpecGroupDTO specGroupDTO) {

        if(ObjectUtil.isNull(specGroupDTO)) return this.setResultError("规格组DTO不能为空");
        if(ObjectUtil.isNull(specGroupDTO.getCid())) return this.setResultError("分类id不能为空");

        Example example = new Example(SpecGroupEntity.class);
        example.createCriteria().andEqualTo("cid",specGroupDTO.getCid());
        List<SpecGroupEntity> list = specGroupMapper.selectByExample(example);

        return this.setResultSuccess(list);
    }

    @Transactional
    @Override
    public Result<JSONObject> saveSpecGroup(SpecGroupDTO specGroupDTO) {

        specGroupMapper.insertSelective(BaiduBeanUtil.copyProperties(specGroupDTO,SpecGroupEntity.class));

        return this.setResultSuccess();
    }


    @Transactional
    @Override
    public Result<JSONObject> editSpecGroup(SpecGroupDTO specGroupDTO) {

        specGroupMapper.updateByPrimaryKeySelective(BaiduBeanUtil.copyProperties(specGroupDTO,SpecGroupEntity.class));

        return this.setResultSuccess();
    }
    @Transactional
    @Override
    public Result<JSONObject> removeSpecGroup(Integer id) {

        //思路：1.判断当前id是否为空
        if(ObjectUtil.isNull(id)){
            return this.setResultError("id为空！！！");
        }
        //2.通过 id查询 中间表中是否存在关联数据（也就是规格组下是否存在规格参数）
         //List<SpecParamEntity> paramList = specParamMapper.selectByGroupId(id);

        Example example = new Example(SpecParamEntity.class);
        example.createCriteria().andEqualTo("groupId",id);
        List<SpecParamEntity> paramList = specParamMapper.selectByExample(example);

        if(paramList.size() > 0 ){//存在: 返回错误信息
            return this.setResultError("该规格组下存在关联规格参数,不能被删除");
        }
        //不存在：进行删除操作
        specGroupMapper.deleteByPrimaryKey(id);

        return  this.setResultSuccess();
    }

    //查询规格参数
    @Override
    public Result<SpecParamEntity> getSpecParamInfo(SpecParamDTO specParamDTO) {

        if (ObjectUtil.isNull(specParamDTO)) return this.setResultError("规格参数DTO为空");

        if (ObjectUtil.isNull(specParamDTO.getGroupId())) return this.setResultError("规格组id为空");

            Example example = new Example(SpecParamEntity.class);
            example.createCriteria().andEqualTo("groupId", specParamDTO.getGroupId());
            List<SpecParamEntity> list = specParamMapper.selectByExample(example);

        return this.setResultSuccess(list);
    }

    @Transactional
    @Override
    public Result<SpecParamEntity> saveSpecParam(SpecParamDTO specParamDTO) {

        specParamMapper.insertSelective(BaiduBeanUtil.copyProperties(specParamDTO,SpecParamEntity.class));

        return this.setResultSuccess();
    }

    @Override
    public Result<SpecParamEntity> editSpecParam(SpecParamDTO specParamDTO) {

        specParamMapper.updateByPrimaryKeySelective(BaiduBeanUtil.copyProperties(specParamDTO,SpecParamEntity.class));

        return this.setResultSuccess();
    }

    @Override
    public Result<SpecParamEntity> removeSpecParam(Integer id) {

        if(ObjectUtil.isNull(id)){
            return this.setResultError("id为空");
        }
        specParamMapper.deleteByPrimaryKey(id);

        return this.setResultSuccess();
    }


}
