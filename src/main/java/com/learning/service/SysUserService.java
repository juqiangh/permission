package com.learning.service;

import com.learning.beans.PageQuery;
import com.learning.beans.PageResult;
import com.learning.model.SysUser;
import com.learning.param.UserParam;

public interface SysUserService {

    void save(UserParam userParam);

    void update(UserParam param);

    SysUser findByKeyword(String keyword);

    PageResult<SysUser> getPageByDeptId(int deptId, PageQuery page);
}
