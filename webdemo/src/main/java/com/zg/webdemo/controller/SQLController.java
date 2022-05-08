package com.zg.webdemo.controller;

import com.zg.mvc.annotation.controller.Controller;
import com.zg.mvc.annotation.controller.ResultMapping;
import com.zg.common.util.reflect.JsonMap;
import com.zg.common.util.reflect.JsonUtils;
import org.apache.commons.collections.map.HashedMap;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Administrator on 2018/12/5 0005.
 */
@Controller("/SQLController")
public class SQLController {


    @ResultMapping("/querySQL.do")
    public String querySQL(String sql) throws Exception {

        //return "forward::/firstController/hello.do";
        // return "staticURL::/WEB-INF/html/hello.html";
        JsonMap json = new JsonMap(true, "操作成功", new ArrayList<>());
        return "json::" + JsonUtils.objectToJson(json);
    }


    @ResultMapping("/updateSQL.do")
    public String updateSQL(String sql) throws IllegalAccessException {
        Map json = new HashedMap();
        try {

            json.put("success", true);
            json.put("message", "操作成功，共有" + 1 + "行受影响");
        } catch (Exception e) {
            e.printStackTrace();
            json.put("success", false);
            json.put("message", "操作失败");
        }
        return "json::" + JsonUtils.objectToJson(json);
    }
}
