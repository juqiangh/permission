package com.learning.dto;


import com.google.common.collect.Lists;
import com.learning.model.SysDept;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.BeanUtils;

import java.util.List;

@Getter
@Setter
@ToString
public class DeptLevelDto extends SysDept{

    private List<DeptLevelDto> deptList = Lists.newArrayList();

    public static DeptLevelDto adopt(SysDept dept){
        DeptLevelDto deptLevelDto = new DeptLevelDto();
        BeanUtils.copyProperties(dept, deptLevelDto);
        return deptLevelDto;
    }
}
