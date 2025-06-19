package com.zg.mvc.controller;

import com.zg.common.util.reflect.JsonMap;
import com.zg.common.util.reflect.JsonUtils;
import com.zg.mvc.annotation.controller.Controller;
import com.zg.mvc.annotation.controller.ResultMapping;

@Controller("/test")
public class TestController extends BaseController {

    @ResultMapping("/test.do")
    public String test() throws Exception {
        JsonMap json = new JsonMap(true, "操作成功", null);
        return "json::" + JsonUtils.objectToJson(json);
    }

}
