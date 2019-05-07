package com.zg.mvc.controller;

import com.zg.database.util.JDBCUtils;
import com.zg.mvc.annotation.controller.Controller;
import com.zg.mvc.annotation.controller.ResultMapping;
import com.zg.util.reflect.JsonMap;
import com.zg.util.reflect.JsonUtils;
import org.apache.commons.collections.map.HashedMap;

import java.sql.SQLException;
import java.util.*;

/**
 * Created by Administrator on 2018/12/5 0005.
 */
@Controller("/SQLController")
public class SQLController {


    @ResultMapping("/querySQL.do")
    public String querySQL(String sql) throws Exception {
        List list=JDBCUtils.selectToMapList(sql);
        //return "forward::/firstController/hello.do";
        // return "staticURL::/WEB-INF/html/hello.html";
        JsonMap json=new JsonMap(true,"操作成功",list);
        return "json::" + JsonUtils.objectToJson(json);
    }


    @ResultMapping("/updateSQL.do")
    public String updateSQL(String sql) throws IllegalAccessException {
        Map json=new HashedMap();
        try {
            int count=JDBCUtils.operation(sql);
            JDBCUtils.commit();
            json.put("success",true);
            json.put("message","操作成功，共有"+count+"行受影响");
        } catch (SQLException e) {
            e.printStackTrace();
            json.put("success",false);
            json.put("message","操作失败");
        }
        return "json::"+JsonUtils.objectToJson(json);
    }
}
