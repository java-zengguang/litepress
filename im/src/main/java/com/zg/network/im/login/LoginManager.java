package com.zg.network.im.login;

import com.zg.network.bean.ZGMPBean;
import com.zg.network.im.service.IMChannelGroups;
import io.netty.channel.ChannelHandlerContext;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2019/3/4 0004.
 */
public class LoginManager {
    // private static LoginServiceInte loginService = LoginServiceProvider.getInstance();

    public static synchronized Map login(ChannelHandlerContext ctx, ZGMPBean request)  {
        String username = request.username;
        String password = request.password;
     /*   String token = loginService.registToken("", "", "");
        Map<String, String> map = loginService.login(password, username, token);
        String uuid=map.get("uuid");
        token=map.get("token");*/
        String uuid = username;
        String token = password;
        Map map = new HashMap();
        map.put("uuid", uuid);
        map.put("token", token);
        if (uuid != null) {
            IMChannelGroups.put(uuid, token, ctx.channel());
        }
        return map;

    }

    public static synchronized boolean logout(String uuid, String token) {
    /*    if(loginService.updateLoginInvalid(uuid,token)>0){
            IMChannelGroups.remove(uuid);
            return true;
        }else {
            return false;
        }
    }*/

        IMChannelGroups.remove(uuid);
        return true;

    }

}
