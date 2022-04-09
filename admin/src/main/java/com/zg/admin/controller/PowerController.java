package com.zg.admin.controller;

import com.zg.admin.entity.Power;
import com.zg.admin.service.PowerService;
import com.zg.handler.ProxyUtils;
import com.zg.mvc.annotation.controller.Controller;
import com.zg.mvc.annotation.controller.ResultMapping;
import com.zg.mvc.controller.BaseController;
import com.zg.mvc.entity.MessageBean;
import com.zg.util.reflect.JsonUtils;

import java.util.List;

@Controller("/power")
public class PowerController extends BaseController {

    private PowerService powerService = (PowerService) ProxyUtils.getProxyClass(new PowerService(), "");


    @ResultMapping("/getPowerList.do")
    public String getPowerList(Power power) throws IllegalAccessException {

        List list = powerService.getPowerList(power);
        json = new MessageBean("成功", true, list);
        return "json::" + JsonUtils.objectToJson(json);

    }


}
