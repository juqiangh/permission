package com.learning.service;

import com.learning.beans.PageQuery;
import com.learning.beans.PageResult;
import com.learning.model.*;
import com.learning.param.SearchLogParam;

public interface SysLogService {

    void recover(int id);

    PageResult<SysLogWithBLOBs> searchPageList(SearchLogParam param, PageQuery page);

    void saveDeptLog(SysDept before, SysDept after);

    void saveUserLog(SysUser before, SysUser after);

    void saveAclModuleLog(SysAclModule before, SysAclModule after);

    void saveAclLog(SysAcl before, SysAcl after);

    void saveRoleLog(SysRole before, SysRole after);
}
