package com.zg.network.im.service;

import com.alibaba.fastjson.JSON;
import com.zg.network.bean.ChannelBean;
import com.zg.network.bean.ZGMPBean;
import com.zg.network.common.service.BaseServiceHandler;
import com.zg.network.im.login.LoginManager;
import com.zg.util.reflect.JsonUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.util.Date;
import java.util.Map;

/**
 * Created by Administrator on 2019/2/22 0022.
 */
public class IMServerHandle extends BaseServiceHandler<String> {
    /***
     * 消息操作的业务类
     */
    //  private IMMessageBiz immessagebiz = new IMMessageBiz();


    private final long TIMEOUT = 50 * 1000;


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        // System.out.println(" get msg >> " + msg);
        ZGMPBean request = (ZGMPBean) JsonUtils.jsonToObject(msg, ZGMPBean.class);//把JSON数据进行反序列化
        ZGMPBean response = new ZGMPBean("RESPONSE");
        response.sendTime = System.currentTimeMillis();
        request.sendTime = System.currentTimeMillis();

        if (request == null) {
            response.status = -1;
            response.errorStr = "请求无效";
            String json = JsonUtils.objectToJson(response).toString();
            ctx.writeAndFlush(json);
            return;
        }

        String methodType = request.methodType;
        System.out.println("the req method >> " + methodType);
        response.methodType = methodType;
        if(methodType!=null) {
            switch (methodType) {
                case ("LOGIN"): {

                    Map<String, String> map = LoginManager.login(ctx, request);
                    String uuid = map.get("uuid");
                    if (uuid != null) {
                        String token = map.get("token");
                        response.message = "login ok";
                        response.status = 0;   //设置状态
                        response.uuid = uuid;
                        response.token = token;
                    } else {
                        response.errorStr = map.get("message");
                        response.status = -1; //状态码
                    }
                    String json = JSON.toJSONString(response);
                    ctx.writeAndFlush(json + "\r\n");  //发送josn字符串数据，注意后面一定要加"\r\n"
                    break;
                }
                case ("SEND"): {
                    // System.out.println(request.message);
                    String uuid = request.targetUuid;
                    Channel channel = IMChannelGroups.getChannel(uuid);
                    if (channel == null) {
                        response.message = "对方未上线";

                    } else {
                        request.direction = "RESPONSE";
                        String json = JsonUtils.objectToJson(request).toString();
                        channel.writeAndFlush(json + "\r\n");  //转发数据
                        response.message = "成功发送";
                    }
                    String resonseJson = JsonUtils.objectToJson(response).toString();
                    ctx.writeAndFlush(resonseJson + "\r\n");
                    break;
                }

                case ("LOGOUT"): {
                    String uuid = request.uuid;
                    String token = request.token;

                    if (LoginManager.logout(uuid, token)) {

                        response.status = 0;
                        response.message = "退出成功";
                        response.methodType = methodType;
                    } else {
                        response.status = -1;
                        response.errorStr = "退出出错";

                    }
                    String json = JsonUtils.objectToJson(response).toString();
                    ctx.writeAndFlush(json + "\r\n");
                    break;
                }

                case ("HEARTBEAT"): {
                    //  System.out.println(request.message);
                    String uuid = request.uuid;
                    ChannelBean channelBean = IMChannelGroups.get(uuid);
                    if (channelBean.heartBeatID.equals(request.heartBeatID)) {
                        long expectTime = channelBean.time + TIMEOUT;
                        long actualTime = new Date().getTime();
                   /* System.out.println("应到时间" + expectTime);
                    System.out.println("实到时间" + actualTime);*/
                        if (expectTime > actualTime) {
                            channelBean.count = 3;
                        } else {
                            System.out.println(channelBean.uuid + " 第" + channelBean.count + "次断开");
                        }
                    } else {
                        System.out.println("此心跳包超时");
                    }


                    break;
                }


                default: {
                    response.errorStr = "操作码错误";
                    response.status = -1; //状态码
                    String json = JsonUtils.objectToJson(response).toString();
                    ctx.writeAndFlush(json + "\r\n");
                    break;
                }
            }

        }

    }
}

