package com.zg.network.im.client;

import com.alibaba.fastjson.JSON;
import com.zg.network.bean.UserBean;
import com.zg.network.bean.ZGMPBean;
import com.zg.network.common.client.BaseClientHandler;
import com.zg.network.common.MessgeReceivedListener;
import com.zg.util.reflect.JsonUtils;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by Administrator on 2019/2/22 0022.
 */


public class IMClientHandler extends BaseClientHandler<String> {


    private ClientDB clientDB=new ClientDB();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println(" get msg >> " + msg);

        ZGMPBean response = JSON.parseObject(msg, ZGMPBean.class);

        String methodType = response.methodType;
        //判断是否是合法的请求

        switch (methodType) {

            case "LOGIN": {

                if (response.status < 0) {
                    System.out.println(response.errorStr);
                } else {
                    UserBean user=new UserBean();
                    user.uuid = response.uuid;
                    user.token = response.token;
                    clientDB.insert("user",user);
                    System.out.println(response.message);
                }
                break;

            }
            case "SEND": {
                System.out.println("message " + response.uuid + " :" + response.message);
                break;
            }

            case "LOGOUT":{
                System.out.println("message  :" + response.message);
                break;
            }

            case "HEARTBEAT":{
                System.out.println("message  :" + response.message);
                String json=null;
                ZGMPBean request=new ZGMPBean("REQUEST");
                request.methodType="HEARTBEAT";
                //UserBean userBean=(UserBean) clientDB.selectOne("user");
                request.uuid=response.uuid;
                request.message="维持心跳";
                request.heartBeatID=response.heartBeatID;
                json= JsonUtils.objectToJson(request).toString();
                ctx.writeAndFlush(json+"\r\n");
                break;
            }

            default: {
                break;
            }


        }


    }

    public void remove(MessgeReceivedListener messgeReceivedListener) {
    }

    public void addMessgeReceivedListener(MessgeReceivedListener messgeReceivedListener) {
    }
}
