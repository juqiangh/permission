package com.learning.service;

import com.learning.dto.AclModuleLevelDto;
import com.learning.param.AclModuleParam;

import java.util.List;

public interface SysAclModuleService {

    void save(AclModuleParam param);

    void update(AclModuleParam param);
}
