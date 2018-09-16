package com.learning.service.impl;

import com.learning.common.RequestHolder;
import com.learning.dao.SysDeptMapper;
import com.learning.exception.ParamException;
import com.learning.model.SysDept;
import com.learning.param.DeptParam;
import com.learning.service.SysDeptService;
import com.learning.util.BeanValidator;
import com.learning.util.IpUtil;
import com.learning.util.LevelUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Service
@Slf4j
public class SysDeptServiceImpl implements SysDeptService{

    @Resource
    private SysDeptMapper sysDeptMapper;

    public void save(DeptParam param) {
        BeanValidator.check(param);
        if(checkExist(param.getParentId(), param.getName(), param.getId())){
            throw new ParamException("同一层级下存在相同名称的部门");
        }
        SysDept sysDept = SysDept.builder().name(param.getName()).parentId(param.getParentId()).seq(param.getSeq())
                .remark(param.getRemark()).build();
        sysDept.setLevel(LevelUtil.calculateLevel(getLevel(param.getParentId()), param.getParentId()));
        sysDept.setOperator(RequestHolder.getCurrentUser().getUsername());
        sysDept.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        sysDept.setOperateTime(new Date());
        sysDeptMapper.insertSelective(sysDept);
    }

    private boolean checkExist(Integer parentId, String deptName, Integer deptId) {
        return false;
    }

    private String getLevel(Integer deptId) {
        SysDept sysDept = sysDeptMapper.selectByPrimaryKey(deptId);
        if(sysDept == null){
            return null;
        }
        return sysDept.getLevel();
    }

}
