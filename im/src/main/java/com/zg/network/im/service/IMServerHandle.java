package com.zg.network.im.service;

import com.alibaba.fastjson.JSON;
import com.zg.common.util.reflect.EntityUtils;
import com.zg.network.bean.ChannelBean;
import com.zg.network.bean.ZGMPBean;
import com.zg.network.common.service.BaseServiceHandler;
import com.zg.network.im.login.LoginManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.tinylog.Logger;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2019/2/22 0022.
 */
public class IMServerHandle extends BaseServiceHandler<String> {


    /***
     * 消息操作的业务类
     */
    //  private IMMessageBiz immessagebiz = new IMMessageBiz();


    private final long TIMEOUT = 10 * 1000;


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {
        // Logger.info(" get msg >> " + msg);
        ZGMPBean request = (ZGMPBean) EntityUtils.unSerialize(msg, ZGMPBean.class);//把JSON数据进行反序列化
        ZGMPBean response = new ZGMPBean("RESPONSE");
        response.sendTime = System.currentTimeMillis();
        request.sendTime = System.currentTimeMillis();

        if (request == null) {
            response.status = -1;
            response.errorStr = "Request error";
            String json = EntityUtils.serialize(response);
            ctx.writeAndFlush(json);
            return;
        }

        String methodType = request.methodType;
        // Logger.info("the req method >> " + methodType);
        response.methodType = methodType;
        if (methodType != null) {
            switch (methodType) {
                case ("LOGIN"): {

                    Map<String, String> map = LoginManager.login(ctx, request);
                    String uuid = map.get("uuid");
                    if (uuid != null) {
                        String token = map.get("token");
                        response.message = "Login ok";
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
                    Logger.info(request.toString());
                    String uuid = request.targetUuid;
                    if (uuid != null && "all".equals(uuid)) {
                        List<Channel> channelList = IMChannelGroups.getAllChannel();
                        for (Channel channel : channelList) {
                            request.direction = "RESPONSE";
                            if (request.operationType != null && !"".equals(request.operationType)) {
                                request.methodType = request.operationType;
                            }
                            String json = EntityUtils.serialize(request);
                            channel.writeAndFlush(json + "\r\n");  //转发数据
                        }
                        response.message = "Send ok";

                    } else {

                        Channel channel = IMChannelGroups.getChannel(uuid);
                        if (channel == null) {
                            response.message = "The other party is not online";

                        } else {
                            request.direction = "RESPONSE";
                            if (request.operationType != null && !"".equals(request.operationType)) {
                                request.methodType = request.operationType;
                            }
                            String json = EntityUtils.serialize(request);
                            channel.writeAndFlush(json + "\r\n");  //转发数据
                            response.message = "Send ok";
                        }
                    }
                    response.methodType = "SYS";
                    String resonseJson = EntityUtils.serialize(response);
                    ctx.writeAndFlush(resonseJson + "\r\n");
                    break;
                }

                case ("LOGOUT"): {
                    String uuid = request.uuid;
                    String token = request.token;

                    if (LoginManager.logout(uuid, token)) {

                        response.status = 0;
                        response.message = "Logout ok";
                        response.methodType = methodType;
                    } else {
                        response.status = -1;
                        response.errorStr = "Logout error";

                    }
                    String json = EntityUtils.serialize(response);
                    ctx.writeAndFlush(json + "\r\n");
                    break;
                }

                case ("HEARTBEAT"): {
                    //  Logger.info(request.message);
                    String uuid = request.uuid;
                    ChannelBean channelBean = IMChannelGroups.get(uuid);
                    if (channelBean != null && channelBean.heartBeatID.equals(request.heartBeatID)) {
                        long expectTime = channelBean.time + TIMEOUT;
                        long actualTime = new Date().getTime();
                   /* Logger.info("应到时间" + expectTime);
                    Logger.info("实到时间" + actualTime);*/
                        if (expectTime > actualTime) {
                            channelBean.count = 3;
                        } else {
                            Logger.info(channelBean.uuid + "The " + channelBean.count + "th disconnection");
                        }
                    } else {
                        Logger.info("Heartbeat packet timeout");
                    }
                    break;
                }


                case "FILESERVICEREQUEST": {
                    Logger.info("请求打开文件服务");

                    break;
                }

                case "FILESERVICEREADY": {
                    Logger.info("文件服务已打开");

                    break;
                }

                default: {
                    response.errorStr = "Status error";
                    response.status = -1; //状态码
                    String json = EntityUtils.serialize(response);
                    ctx.writeAndFlush(json + "\r\n");
                    break;
                }
            }

        }

    }
}

