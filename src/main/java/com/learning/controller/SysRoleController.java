package com.learning.controller;

import com.learning.common.JsonData;
import com.learning.param.RoleParam;
import com.learning.service.SysRoleService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;

@Controller
@RequestMapping("/sys/role")
public class SysRoleController {

    @Resource
    private SysRoleService sysRoleService;

    @RequestMapping("/role.page")
    public ModelAndView page(){
        return new ModelAndView("role");
    }

    @RequestMapping("/save.json")
    public JsonData save(RoleParam roleParam) {
        sysRoleService.save(roleParam);
        return JsonData.success();
    }

    @RequestMapping("/update.json")
    public JsonData update(RoleParam roleParam) {
        sysRoleService.update(roleParam);
        return JsonData.success();
    }

    @RequestMapping("/list.json")
    @ResponseBody
    public JsonData list() {
        return JsonData.success(sysRoleService.getAll());
    }
}
