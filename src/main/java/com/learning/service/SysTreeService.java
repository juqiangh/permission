package com.learning.service;

import com.learning.dto.AclModuleLevelDto;
import com.learning.dto.DeptLevelDto;

import java.util.List;

public interface SysTreeService {

    List<DeptLevelDto> deptTree();

    List<AclModuleLevelDto> aclModuleTree();

    List<AclModuleLevelDto> roleTree(int roleId);

    List<AclModuleLevelDto> userAclTree(int userId);
}
