package com.learning.controller;

import com.learning.common.JsonData;
import com.learning.dto.DeptLevelDto;
import com.learning.param.DeptParam;
import com.learning.service.SysDeptService;
import com.learning.service.SysTreeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/sys/dept")
@Slf4j
public class SysDeptController {

    @Resource
    private SysDeptService sysDeptService;

    @Resource
    private SysTreeService sysTreeService;

    @GetMapping("/dept.page")
    public ModelAndView page(){
        return new ModelAndView("dept");
    }

    @PostMapping("/save.json")
    public JsonData saveDept(DeptParam param) {
        sysDeptService.save(param);
        return JsonData.success();
    }

    @GetMapping("/tree.json")
    public JsonData tree () {
        List<DeptLevelDto> dtoList = sysTreeService.deptTree();
        return JsonData.success(dtoList);
    }

    @PostMapping("/update.json")
    public JsonData updateDept(DeptParam param) {
        sysDeptService.update(param);
        return JsonData.success();
    }

    @RequestMapping("/delete.json")
    @ResponseBody
    public JsonData delete(@RequestParam("id") int id) {
        sysDeptService.delete(id);
        return JsonData.success();
    }
}
