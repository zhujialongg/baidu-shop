package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.dto.SpecParamDTO;
import com.baidu.entity.SpecParamEntity;
import com.baidu.exception.MingruiException;
import com.baidu.exception.errorenum.ErrorCodeEnum;
import com.baidu.shop.mapper.SpecParamMapper;
import com.baidu.base.BaseApiService;
import com.baidu.base.Result;
import com.baidu.dto.SpecGroupDTO;
import com.baidu.entity.SpecGroupEntity;
import com.baidu.shop.mapper.SpecGroupMapper;
import com.baidu.service.SpecificationService;
import com.baidu.utils.BaiduBeanUtil;
import com.baidu.utils.ObjectUtil;
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
        if(ObjectUtil.isNull(id)) throw new MingruiException(ErrorCodeEnum.ID_NOT_NULL);

        //2.通过 id查询 中间表中是否存在关联数据（也就是规格组下是否存在规格参数）
        Example example = new Example(SpecParamEntity.class);
        example.createCriteria().andEqualTo("groupId",id);
        List<SpecParamEntity> paramList = specParamMapper.selectByExample(example);

        if(paramList.size() > 0 ) return this.setResultError("该规格组下存在关联规格参数,不能被删除");
        specGroupMapper.deleteByPrimaryKey(id);

        return  this.setResultSuccess();
    }

    //查询规格参数
    @Override
    public Result <List<SpecParamEntity>> getSpecParamInfo(SpecParamDTO specParamDTO) {


        if (ObjectUtil.isNull(specParamDTO)) return this.setResultError("规格参数DTO为空");

        Example example = new Example(SpecParamEntity.class);
        Example.Criteria criteria = example.createCriteria();

        if(ObjectUtil.isNotNull(specParamDTO.getGroupId())){
            criteria.andEqualTo("groupId", specParamDTO.getGroupId());
        }
        if(ObjectUtil.isNotNull(specParamDTO.getCid())){
            criteria.andEqualTo("cid",specParamDTO.getCid());
        }
        if(ObjectUtil.isNotNull(specParamDTO.getSearching())){
            criteria.andEqualTo("searching",specParamDTO.getSearching());
        }
        if(ObjectUtil.isNotNull(specParamDTO.getId())){
            criteria.andEqualTo("id",specParamDTO.getId());
        }
        if(ObjectUtil.isNotNull(specParamDTO.getGeneric())){
            criteria.andEqualTo("generic",specParamDTO.getGeneric());
        }

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

        if(ObjectUtil.isNull(id)) return this.setResultError("id为空");

        specParamMapper.deleteByPrimaryKey(id);

        return this.setResultSuccess();
    }


}
