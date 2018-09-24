package com.learning.service;

import com.learning.beans.PageQuery;
import com.learning.beans.PageResult;
import com.learning.model.SysAcl;
import com.learning.param.AclParam;

public interface SysAclService {

    void save(AclParam param);

    void update(AclParam param);

    PageResult<SysAcl> getPageByAclModuleId(int aclModuleId, PageQuery page);
}
