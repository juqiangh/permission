package com.learning.service.impl;

import com.google.common.base.Preconditions;
import com.learning.beans.PageQuery;
import com.learning.beans.PageResult;
import com.learning.common.RequestHolder;
import com.learning.dao.SysUserMapper;
import com.learning.exception.ParamException;
import com.learning.model.SysUser;
import com.learning.param.UserParam;
import com.learning.service.SysUserService;
import com.learning.util.BeanValidator;
import com.learning.util.IpUtil;
import com.learning.util.MD5Util;
import com.learning.util.PasswordUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class SysUserServiceImpl implements SysUserService {

    @Resource
    private SysUserMapper sysUserMapper;

    public void save(UserParam param) {
        BeanValidator.check(param);
        if(checkTelephoneExist(param.getTelephone(), param.getId())){
            throw new ParamException("电话已被占用");
        }
        if(checkEmailExist(param.getMail(), param.getId())){
            throw new ParamException("邮箱已被占用");
        }
        String password = PasswordUtil.randomPassword();
        //TODO
        password = "123456";
        String encryptedPassword = MD5Util.encrypt(password);
        SysUser user = SysUser.builder().username(param.getUsername()).telephone(param.getTelephone()).mail(param.getMail())
                .password(encryptedPassword).deptId(param.getDeptId()).status(param.getStatus())
                .remark(param.getRemark()).build();
        user.setOperator(RequestHolder.getCurrentUser().getUsername());
        user.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        user.setOperateTime(new Date());

        // TODO: sendEmail
        sysUserMapper.insertSelective(user);
    }

    public void update(UserParam param) {
        BeanValidator.check(param);
        if(checkTelephoneExist(param.getTelephone(), param.getId())){
            throw new ParamException("电话已被占用");
        }
        if(checkEmailExist(param.getMail(), param.getId())){
            throw new ParamException("邮箱已被占用");
        }
        SysUser before = sysUserMapper.selectByPrimaryKey(param.getId());
        Preconditions.checkNotNull(before, "待更新的用户不存在");
        SysUser after = SysUser.builder().id(param.getId()).username(param.getUsername()).telephone(param.getTelephone())
                .mail(param.getMail()).deptId(param.getDeptId()).status(param.getStatus())
                .remark(param.getRemark()).build();
        after.setOperator(RequestHolder.getCurrentUser().getUsername());
        after.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        after.setOperateTime(new Date());
        sysUserMapper.updateByPrimaryKeySelective(after);
    }

    public SysUser findByKeyword(String keyword) {
        return sysUserMapper.findByKeyword(keyword);
    }

    public PageResult<SysUser> getPageByDeptId(int deptId, PageQuery page) {
        BeanValidator.check(page);
        int count = sysUserMapper.countByDeptId(deptId);
        if(count > 0 ){
            List<SysUser> list = sysUserMapper.getPageByDeptId(deptId, page);
            return PageResult.<SysUser>builder().data(list).total(count).build();
        }
        return PageResult.<SysUser>builder().build();
    }

    public List<SysUser> getAll() {
        return sysUserMapper.getAll();
    }

    private boolean checkTelephoneExist(String telephone, Integer userId) {
        return sysUserMapper.countByTelephone(telephone, userId) > 0;
    }

    private boolean checkEmailExist(String mail, Integer userId) {
        return sysUserMapper.countByMail(mail, userId) > 0;
    }
}
