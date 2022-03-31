package com.zg.mvc.controller;

import com.zg.database.util.JDBCUtils;
import com.zg.mvc.annotation.controller.Controller;
import com.zg.mvc.annotation.controller.ResultMapping;
import com.zg.util.reflect.JsonMap;
import com.zg.util.reflect.JsonUtils;
import org.apache.commons.collections.map.HashedMap;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/12/5 0005.
 */
@Controller("/TestController")
public class TestController {


    @ResultMapping("/test.do")
    public String test() throws Exception {
        JsonMap json=new JsonMap(true,"操作成功",null);
        return "json::" + JsonUtils.objectToJson(json);
    }


}
