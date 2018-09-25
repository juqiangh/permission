package com.learning.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.learning.common.RequestHolder;
import com.learning.dao.SysRoleAclMapper;
import com.learning.model.SysRoleAcl;
import com.learning.service.SysRoleAclService;
import com.learning.util.IpUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class SysRoleAclServiceImpl implements SysRoleAclService {

    @Resource
    private SysRoleAclMapper sysRoleAclMapper;

    public void changeRoleAcls(Integer roleId, List<Integer> aclIdList) {
        List<Integer> originAclIdList = sysRoleAclMapper.getAclIdListByRoleIdList(Lists.newArrayList(roleId));
        if(originAclIdList.size() == aclIdList.size()) {
            Set<Integer> originAclIdSet = Sets.newHashSet(originAclIdList);
            Set<Integer> aclIdSet = Sets.newHashSet(aclIdList);
            originAclIdSet.removeAll(aclIdSet);
            if(CollectionUtils.isEmpty(originAclIdSet)){
                return ;
            }
        }
        updateRoleAcls(roleId, aclIdList);
    }

    @Transactional
    private void updateRoleAcls(int roleId, List<Integer> aclIdList) {
        sysRoleAclMapper.deleteByRoleId(roleId);

        if (CollectionUtils.isEmpty(aclIdList)) {
            return;
        }
        List<SysRoleAcl> roleAclList = Lists.newArrayList();
        for(Integer aclId : aclIdList) {
            SysRoleAcl roleAcl = SysRoleAcl.builder().roleId(roleId).aclId(aclId).operator(RequestHolder.getCurrentUser().getUsername())
                    .operateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest())).operateTime(new Date()).build();
            roleAclList.add(roleAcl);
        }
        sysRoleAclMapper.batchInsert(roleAclList);
    }

}
