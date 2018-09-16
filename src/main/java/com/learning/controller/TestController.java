package com.learning.controller;

import com.learning.common.ApplicationContextHelper;
import com.learning.common.JsonData;
import com.learning.dao.SysAclModuleMapper;
import com.learning.exception.ParamException;
import com.learning.model.SysAclModule;
import com.learning.param.TestVo;
import com.learning.util.BeanValidator;
import com.learning.util.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/test")
@Slf4j
public class TestController {

    @GetMapping("/hello.json")
    public JsonData hello(){
        log.info("hello");
        return JsonData.success("hello,permission");
    }

    @RequestMapping("/validate.json")
    @ResponseBody
    public JsonData validate(TestVo vo) throws ParamException {
        log.info("validate");
        SysAclModuleMapper sysAclModuleMapper = ApplicationContextHelper.popBean(SysAclModuleMapper.class);
        SysAclModule module = sysAclModuleMapper.selectByPrimaryKey(1);
        log.info(JsonMapper.obj2String(module));
        BeanValidator.check(vo);
        return JsonData.success("test validate");
    }
}
