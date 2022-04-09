package com.zg.admin.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mysql.cj.xdevapi.JsonArray;
import com.zg.admin.entity.User;
import com.zg.direction.register.ZookeeperUtil;
import com.zg.mvc.annotation.controller.Controller;
import com.zg.mvc.annotation.controller.ResultMapping;
import com.zg.mvc.controller.BaseController;
import com.zg.mvc.entity.MessageBean;
import com.zg.util.reflect.JsonUtils;
import io.netty.handler.codec.json.JsonObjectDecoder;

import java.util.List;
import java.util.Map;

@Controller("/zookeeper")
public class ZookeeperController extends BaseController {

    @ResultMapping("/getAllZone.do")
    public String getAllZone() {
        String jsonS = "";
        try {
            ZookeeperUtil zookeeperUtil = new ZookeeperUtil("127.0.0.1:2181");
            JSONArray jsonArray = zookeeperUtil.findChildNodesJson("/");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("success", true);
            jsonObject.put("message", "成功");
            jsonObject.put("object", jsonArray);
            jsonS = jsonObject.toJSONString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "json::" + jsonS;
    }


}
