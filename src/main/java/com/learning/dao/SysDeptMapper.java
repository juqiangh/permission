package com.learning.dao;

import com.learning.model.SysDept;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysDeptMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysDept record);

    int insertSelective(SysDept record);

    SysDept selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysDept record);

    int updateByPrimaryKey(SysDept record);

    List<SysDept> getAllDept();

    int countByNameAndParentId(@Param("parentId") Integer parentId, @Param("name") String name, @Param("id") Integer id);

    List<SysDept> getChildDeptListByLevel(@Param("level") String level);

    int batchUpdateLevel(@Param("sysDeptList") List<SysDept> sysDeptList);
}