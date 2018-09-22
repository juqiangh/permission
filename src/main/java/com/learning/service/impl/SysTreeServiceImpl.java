package com.learning.service.impl;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.learning.dao.SysDeptMapper;
import com.learning.dto.DeptLevelDto;
import com.learning.model.SysDept;
import com.learning.service.SysTreeService;
import com.learning.util.LevelUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class SysTreeServiceImpl implements SysTreeService {

    @Resource
    private SysDeptMapper sysDeptMapper;

    public List<DeptLevelDto> deptTree() {
        List<SysDept> deptList = sysDeptMapper.getAllDept();
        List<DeptLevelDto> dtoList = Lists.newArrayList();
        for (SysDept dept : deptList) {
            DeptLevelDto deptLevelDto = DeptLevelDto.adopt(dept);
            dtoList.add(deptLevelDto);
        }
        return deptListToTree(dtoList);
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
}
