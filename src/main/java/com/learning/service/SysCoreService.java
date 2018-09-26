package com.learning.service;

import com.learning.model.SysAcl;

import java.util.List;

public interface SysCoreService {

    List<SysAcl> getCurrentUserAclList();

    List<SysAcl> getRoleAclList(int roleId);

    List<SysAcl> getUserAclList(int userId);

    boolean hasUrlAcl(String url);
}
