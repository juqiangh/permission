package com.learning.service.impl;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.learning.dao.SysAclMapper;
import com.learning.dao.SysAclModuleMapper;
import com.learning.dao.SysDeptMapper;
import com.learning.dto.AclDto;
import com.learning.dto.AclModuleLevelDto;
import com.learning.dto.DeptLevelDto;
import com.learning.model.SysAcl;
import com.learning.model.SysAclModule;
import com.learning.model.SysDept;
import com.learning.service.SysCoreService;
import com.learning.service.SysTreeService;
import com.learning.util.LevelUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SysTreeServiceImpl implements SysTreeService {

    @Resource
    private SysDeptMapper sysDeptMapper;

    @Resource
    private SysAclModuleMapper sysAclModuleMapper;

    @Resource
    private SysCoreService sysCoreService;

    @Resource
    private SysAclMapper sysAclMapper;

    public List<DeptLevelDto> deptTree() {
        List<SysDept> deptList = sysDeptMapper.getAllDept();
        List<DeptLevelDto> dtoList = Lists.newArrayList();
        for (SysDept dept : deptList) {
            DeptLevelDto deptLevelDto = DeptLevelDto.adopt(dept);
            dtoList.add(deptLevelDto);
        }
        return deptListToTree(dtoList);
    }

    public List<AclModuleLevelDto> aclModuleTree() {
        List<SysAclModule> aclModuleList = sysAclModuleMapper.getAllAclModule();
        List<AclModuleLevelDto> dtoList = Lists.newArrayList();
        for (SysAclModule aclModule : aclModuleList){
            dtoList.add(AclModuleLevelDto.adapt(aclModule));
        }

        return aclModuleListToTree(dtoList);
    }

    public List<AclModuleLevelDto> roleTree(int roleId) {
        // 1、当前用户已分配的权限点
        List<SysAcl> userAclList = sysCoreService.getCurrentUserAclList();
        // 2、当前角色分配的权限点
        List<SysAcl> roleAclList = sysCoreService.getRoleAclList(roleId);
        // 3、当前系统所有权限点
        List<AclDto> aclDtoList = Lists.newArrayList();

        Set<Integer> userAclIdSet = userAclList.stream().map(sysAcl -> sysAcl.getId()).collect(Collectors.toSet());
        Set<Integer> roleAclIdSet = roleAclList.stream().map(sysAcl -> sysAcl.getId()).collect(Collectors.toSet());

        List<SysAcl> allAclList = sysAclMapper.getAll();
        for(SysAcl acl : allAclList) {
            AclDto dto = AclDto.adapt(acl);
            if(userAclIdSet.contains(acl.getId())) {
                dto.setHasAcl(true);
            }
            if(roleAclIdSet.contains(acl.getId())) {
                dto.setChecked(true);
            }
            aclDtoList.add(dto);
        }
        return aclListToTree(aclDtoList);
    }

    private List<AclModuleLevelDto> aclListToTree(List<AclDto> aclDtoList) {
        if (CollectionUtils.isEmpty(aclDtoList)) {
            return Lists.newArrayList();
        }
        List<AclModuleLevelDto> aclModuleLevelList = aclModuleTree();
        Multimap<Integer, AclDto> moduleIdAclMap = ArrayListMultimap.create();
        for(AclDto acl : aclDtoList) {
            if(acl.getStatus() == 1){
                moduleIdAclMap.put(acl.getAclModuleId(), acl);
            }
        }
        bindAclsWithOrder(aclModuleLevelList, moduleIdAclMap);
        return aclModuleLevelList;
    }

    private void bindAclsWithOrder(List<AclModuleLevelDto> aclModuleLevelList, Multimap<Integer, AclDto> moduleIdAclMap) {
        if (CollectionUtils.isEmpty(aclModuleLevelList)) {
            return;
        }
        for(AclModuleLevelDto dto : aclModuleLevelList) {
            List<AclDto> aclDtoList = (List<AclDto>)moduleIdAclMap.get(dto.getId());
            if (CollectionUtils.isNotEmpty(aclDtoList)) {
                Collections.sort(aclDtoList, aclSeqComparator);
                dto.setAclList(aclDtoList);
            }
            bindAclsWithOrder(dto.getAclModuleList(), moduleIdAclMap);
        }
    }

    private List<AclModuleLevelDto> aclModuleListToTree(List<AclModuleLevelDto> aclModuleList) {
        if(CollectionUtils.isEmpty(aclModuleList)) {
            return Lists.newArrayList();
        }
        // level -> [aclmodule1, aclmodule2, ...] Map<String, List<Object>>
        Multimap<String, AclModuleLevelDto> levelAclModuleMap = ArrayListMultimap.create();
        List<AclModuleLevelDto> rootList = Lists.newArrayList();
        for(AclModuleLevelDto aclModule : aclModuleList) {
            levelAclModuleMap.put(aclModule.getLevel(), aclModule);
            if(LevelUtil.ROOT.equals(aclModule.getLevel())){
                rootList.add(aclModule);
            }
        }
        Collections.sort(rootList, aclModuleSeqComparator);
        transformAclModuleTree(rootList, levelAclModuleMap);
        return rootList;
    }

    private void transformAclModuleTree(List<AclModuleLevelDto> dtoList, Multimap<String, AclModuleLevelDto> levelAclModuleMap) {
        for(AclModuleLevelDto aclModuleLevelDto : dtoList){
            String nextLevel = LevelUtil.calculateLevel(aclModuleLevelDto.getLevel(), aclModuleLevelDto.getId());
            List<AclModuleLevelDto> tempList = (List<AclModuleLevelDto>) levelAclModuleMap.get(nextLevel);
            if(CollectionUtils.isNotEmpty(tempList)){
                Collections.sort(tempList, aclModuleSeqComparator);
                transformAclModuleTree(tempList, levelAclModuleMap);
                aclModuleLevelDto.setAclModuleList(tempList);
            }
        }
    }

    private List<DeptLevelDto> deptListToTree(List<DeptLevelDto> deptLevelList) {
        if(CollectionUtils.isEmpty(deptLevelList)) {
            return Lists.newArrayList();
        }
        // level -> [dept1, dept2, ...] Map<String, List<Object>>
        Multimap<String, DeptLevelDto> levelDeptMap = ArrayListMultimap.create();
        List<DeptLevelDto> rootList = Lists.newArrayList();
        for (DeptLevelDto deptLevelDto : deptLevelList){
            levelDeptMap.put(deptLevelDto.getLevel(), deptLevelDto);
            if (LevelUtil.ROOT.equals(deptLevelDto.getLevel())) {
                rootList.add(deptLevelDto);
            }
        }
        // 按照seq从小到大排序
        Collections.sort(rootList, new Comparator<DeptLevelDto>() {
            public int compare(DeptLevelDto o1, DeptLevelDto o2) {
                return o1.getSeq() - o2.getSeq();
            }
        });
        // 递归生成树
        transformDeptTree(rootList, levelDeptMap);
        return rootList;
    }

    // level:0, 0, all 0->0.1,0.2
    // level:0.1
    // level:0.2
    private void transformDeptTree(List<DeptLevelDto> deptLevelList, Multimap<String, DeptLevelDto> levelDeptMap) {
        for(DeptLevelDto deptLevelDto : deptLevelList) {
            //获取下一层级
            String nextLevel = LevelUtil.calculateLevel(deptLevelDto.getLevel(), deptLevelDto.getId());
            //获取下一层的数据
            List<DeptLevelDto> tempDeptList = (List<DeptLevelDto>) levelDeptMap.get(nextLevel);
            if (CollectionUtils.isNotEmpty(tempDeptList)) {
                //排序
                Collections.sort(tempDeptList, deptSeqComparator);
                //递归下一层部门
                transformDeptTree(tempDeptList, levelDeptMap);
                //设置下一层部门
                deptLevelDto.setDeptList(tempDeptList);
            }
        }
    }

    private Comparator<DeptLevelDto> deptSeqComparator = new Comparator<DeptLevelDto>() {
        public int compare(DeptLevelDto o1, DeptLevelDto o2) {
            return o1.getSeq() - o2.getSeq();
        }
    };

    private Comparator<AclModuleLevelDto> aclModuleSeqComparator = new Comparator<AclModuleLevelDto>() {
        public int compare(AclModuleLevelDto o1, AclModuleLevelDto o2) {
            return o1.getSeq() - o2.getSeq();
        }
    };

    private Comparator<AclDto> aclSeqComparator = new Comparator<AclDto>() {
        public int compare(AclDto o1, AclDto o2) {
            return o1.getSeq() - o2.getSeq();
        }
    };
}
