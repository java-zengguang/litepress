package com.zg.mvc.controller;

import com.zg.common.util.reflect.JsonUtils;
import com.zg.mvc.annotation.controller.Controller;
import com.zg.mvc.annotation.controller.ResultMapping;
import com.zg.mvc.entity.MessageBean;
import com.zg.mvc.entity.UserInfo;
import com.zg.mvc.service.LoginService;
import com.zg.mvc.util.AESUtil;
import org.cacheonix.Cacheonix;
import org.cacheonix.cache.Cache;
import org.cacheonix.impl.DistributedCacheonix;

@Controller("/test")
public class LoginController extends BaseController {

    private LoginService loginService;
    private static String key = "helloworld";
    private static Cacheonix cacheonix = DistributedCacheonix.getInstance();  //分布式本地缓存，任意节点登录，所有节点授权
    private static Cache loginCache = cacheonix.getCache("loginCache");

    @ResultMapping("/Login.do")
    public String login(String userName, String passWorld) throws Exception {
        UserInfo userInfo = loginService.login(userName, passWorld);
        if (userInfo != null) {
            String token = AESUtil.encodeData(JsonUtils.objectToJsonString(userInfo), key);
            loginCache.put(token, userInfo);
            json = new MessageBean("登录成功", true, token);
        } else {
            json = new MessageBean("登录失败", false, null);
        }

        return "json::" + JsonUtils.objectToJson(json);
    }

}
