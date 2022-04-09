package com.zg.admin.controller;


import com.zg.admin.entity.Menu;
import com.zg.admin.entity.MenuInfo;
import com.zg.admin.service.MenuService;
import com.zg.handler.ProxyUtils;
import com.zg.mvc.annotation.controller.Controller;
import com.zg.mvc.annotation.controller.ResultMapping;
import com.zg.mvc.controller.BaseController;
import com.zg.mvc.entity.MessageBean;
import com.zg.util.redis.RedisUtils;
import com.zg.util.reflect.JsonUtils;

import java.util.List;

@Controller("/menu")
public class MenuController extends BaseController {

    public MenuService menuService = (MenuService) ProxyUtils.getProxyClass(new MenuService(), "insertMenu,updateMenu");


    @ResultMapping("/getRootMenu.do")
    public String getRootMenu(Integer id, Integer level, String type) throws IllegalAccessException {

        String jsonString = RedisUtils.get("menu");

        if (jsonString == null || "".equals(jsonString)) {

            List list = null;
            if ("tree".equals(type)) {
                list = menuService.getMenuTree(id, level, Menu.class);
            }

            if ("table".equals(type)) {
                list = menuService.getMenuTree(id, level, MenuInfo.class);
            }

            jsonString = JsonUtils.objectToJson(list).toString();
            RedisUtils.append("menu", jsonString);
        }
        return "json::" + jsonString;
    }

    @ResultMapping("/getMenu.do")
    public String getMenu(String id) throws IllegalAccessException {
        List list = menuService.getMenuById(id, MenuInfo.class);
        json = new MessageBean("操作成功", true, list);
        return "json::" + JsonUtils.objectToJson(json);
    }


    @ResultMapping("/insertMenu.do")
    public String insertMenu(MenuInfo menuInfo) throws IllegalAccessException {

        if (menuService.insertMenu(menuInfo)) {
            json = new MessageBean("操作成功", true, null);
        } else {
            json = new MessageBean("操作失败", false, null);
        }

        return "json::" + JsonUtils.objectToJson(json);
    }


    @ResultMapping("/updateMenu.do")
    public String updateMenu(MenuInfo menuInfo, Integer id) throws IllegalAccessException {
        if (menuService.updateMenu(menuInfo, id)) {
            json = new MessageBean("操作成功", true, null);

        } else {
            json = new MessageBean("操作失败 ", false, null);

        }

        return "json::" + JsonUtils.objectToJson(json);
    }
}
