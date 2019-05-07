package com.zg.admin.controller;

import com.zg.admin.entity.Role;
import com.zg.admin.service.RoleService;
import com.zg.admin.service.RoleServiceInte;
import com.zg.handler.CommitInterfaceHandler;
import com.zg.mvc.annotation.controller.Controller;
import com.zg.mvc.annotation.controller.ResultMapping;
import com.zg.mvc.controller.BaseController;
import com.zg.mvc.entity.MessageBean;
import com.zg.util.reflect.JsonUtils;

import java.lang.reflect.Proxy;
import java.util.List;

/**
 * Created by Administrator on 2019/3/14 0014.
 */

@Controller("/role")
public class RoleController extends BaseController{

    private RoleServiceInte roleService= (RoleServiceInte) Proxy.newProxyInstance(RoleService.class.getClassLoader(),RoleService.class.getInterfaces(),new CommitInterfaceHandler(new RoleService(),"insertRole,editRole,deleteRoles"));

    @ResultMapping("/getRoleList.do")
    public String getRoleList(Role role) throws IllegalAccessException {

        List list = roleService.getRoleList(role);

        if(list!=null && list.size()>0) {
            json = new MessageBean("操作成功", true, list);
        }else{
            json = new MessageBean("操作失败", false, list);
        }
        return "json::"+ JsonUtils.objectToJson(json);
    }


    @ResultMapping("/insertRole.do")
    public String insertRole(Role role) throws IllegalAccessException {
        if(roleService.insertRole(role)) {

            json = new MessageBean("操作成功", true, null);

        }else{

            json = new MessageBean("操作失败", false, null);

        }
        return "json::"+JsonUtils.objectToJson(json);
    }

    @ResultMapping("/editRole.do")
    public String editRole(Role role) throws IllegalAccessException {
        if(roleService.editRole(role)) {

            json = new MessageBean("操作成功", true, null);

        }else{

            json = new MessageBean("操作失败", false, null);

        }
        return "json::"+JsonUtils.objectToJson(json);
    }



    @ResultMapping("/deleteRoles.do")
    public String deleteRoles(String ids) throws IllegalAccessException {
        if(roleService.deleteRoles(ids)) {

            json = new MessageBean("操作成功", true, null);

        }else{

            json = new MessageBean("操作失败", false, null);

        }
        return "json::"+JsonUtils.objectToJson(json);
    }




}
