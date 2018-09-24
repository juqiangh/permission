package com.learning.service;

import com.learning.model.SysRole;
import com.learning.param.RoleParam;

import java.util.List;

public interface SysRoleService {

    void save(RoleParam param);

    void update(RoleParam param);

    List<SysRole> getAll();
}
