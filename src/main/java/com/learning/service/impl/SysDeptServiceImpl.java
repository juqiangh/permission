package com.learning.service.impl;

import com.google.common.base.Preconditions;
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
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

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

    public void update(DeptParam param) {
        BeanValidator.check(param);
        if(checkExist(param.getParentId(), param.getName(), param.getId())){
            throw new ParamException("同一层级下存在相同名称的部门");
        }
        //修改之前的部门
        SysDept before = sysDeptMapper.selectByPrimaryKey(param.getId());
        Preconditions.checkNotNull(before, "待更新的部门不存在");
        //修改后的部门
        SysDept after = SysDept.builder().id(param.getId()).name(param.getName()).parentId(param.getParentId())
                .seq(param.getSeq()).remark(param.getRemark()).build();
        after.setLevel(LevelUtil.calculateLevel(getLevel(param.getParentId()), param.getParentId()));
        after.setOperator(RequestHolder.getCurrentUser().getUsername());
        after.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        after.setOperateTime(new Date());

        updateWithChild(before, after);
    }

    @Transactional
    private void updateWithChild(SysDept before, SysDept after) {
        String newLevelPrefix = after.getLevel();
        String oldLevelPrefix = before.getLevel();
        if(!newLevelPrefix.equals(oldLevelPrefix)) {
            List<SysDept> deptList = sysDeptMapper.getChildDeptListByLevel(before.getLevel());
            if(CollectionUtils.isNotEmpty(deptList)) {
                for(SysDept sysDept : deptList) {
                    String level = sysDept.getLevel();
                    if(level.indexOf(oldLevelPrefix) == 0) {
                        level = newLevelPrefix + level.substring(oldLevelPrefix.length());
                        sysDept.setLevel(level);
                    }
                }
                sysDeptMapper.batchUpdateLevel(deptList);
            }
        }
        sysDeptMapper.updateByPrimaryKeySelective(after);
    }

    private boolean checkExist(Integer parentId, String deptName, Integer deptId) {
        return sysDeptMapper.countByNameAndParentId(parentId, deptName, deptId) > 0;
    }

    private String getLevel(Integer deptId) {
        SysDept sysDept = sysDeptMapper.selectByPrimaryKey(deptId);
        if(sysDept == null){
            return null;
        }
        return sysDept.getLevel();
    }

}
