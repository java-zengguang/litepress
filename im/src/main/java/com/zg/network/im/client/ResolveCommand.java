package com.zg.network.im.client;

import com.zg.incache.prestuctural.manager.CacheManager;
import com.zg.network.bean.UserBean;
import com.zg.network.bean.ZGMPBean;
import org.tinylog.Logger;


/**
 * Created by Administrator on 2019/2/27 0027.
 */
public class ResolveCommand {

    // private ClientDB clientDB = new ClientDB();

    public ZGMPBean resolveCommand(String command) {
        ZGMPBean request = new ZGMPBean("REQUEST");
        try {
            String[] s = command.split(" ");
            if ("send".equals(s[0])) {
                request.methodType = "SEND";
                request.targetUuid = s[1];
                request.message = s[2];
            } else if ("login".equals(s[0])) {
                request.methodType = "LOGIN";
                request.username = s[1];
                request.password = s[2];
            } else if ("logout".equals(s[0])) {
                request.methodType = "LOGOUT";
            } else if ("file".equals(s[0])) {
                request.methodType = "SEND";
                request.operationType = "FILESERVICEREQUEST";
                request.targetUuid = s[1];
                request.message = s[2];
            }

            if (!checkRequest(request)) {
                return null;
            }
        } catch (Exception e) {
            Logger.error("命令错误");
        }
        return request;
    }


    public boolean checkRequest(ZGMPBean request) {
        String methodType = request.methodType;

        switch (methodType) {
            case "LOGIN": {
                // Logger.info(" to login  ");
                break;
            }
            case "LOGOUT": {
                if (!isLogin(request)) {
                    Logger.info("还未登陆");
                    return false;
                }
                break;
            }
            case "SEND": {
                if (!isLogin(request)) {
                    Logger.info("还未登陆");
                    return false;
                }
                // Logger.info(" to " + request.message);
                break;
            }

            default: {
                Logger.info(" 未识别得操作类型 " + methodType);
                break;
            }
        }
        return true;
    }

    public boolean isLogin(ZGMPBean request) {
        UserBean userBean = (UserBean) CacheManager.get("user");
        if (userBean != null) {
            request.uuid = userBean.uuid;
            request.token = userBean.token;
            return true;
        } else {
            return false;
        }
    }


}

