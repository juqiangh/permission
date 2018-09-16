package com.learning.controller;

import com.learning.common.JsonData;
import com.learning.param.DeptParam;
import com.learning.service.SysDeptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/sys/dept")
@Slf4j
public class SysDeptController {

    @Resource
    private SysDeptService sysDeptService;

    @GetMapping("/save.json")
    public JsonData saveDept(DeptParam param) {
        sysDeptService.save(param);
        return JsonData.success();
    }

    @GetMapping("/tree.json")
    public JsonData tree () {
        return JsonData.success();
    }
}
