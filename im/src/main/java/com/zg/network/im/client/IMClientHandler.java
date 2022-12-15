package com.zg.network.im.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zg.incache.prestuctural.manager.CacheManager;
import com.zg.network.bean.UserBean;
import com.zg.network.bean.ZGMPBean;
import com.zg.network.common.MessgeReceivedListener;
import com.zg.network.common.client.BaseClientHandler;
import com.zg.network.common.fileservcie.ReceiveFile;
import com.zg.network.common.fileservcie.SendFile;
import com.zg.network.im.utils.AudioUtils;

import com.zg.common.util.reflect.EntityUtils;
import com.zg.common.util.url.GetServerRealPathUnit;
import io.netty.channel.ChannelHandlerContext;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by Administrator on 2019/2/22 0022.
 */


public class IMClientHandler extends BaseClientHandler<String> {

    //private ClientDB clientDB=new ClientDB();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws UnknownHostException {
        // logger.info(" get msg >> " + msg);

        ZGMPBean response = JSON.parseObject(msg, ZGMPBean.class);

        String methodType = response.methodType;
        //判断是否是合法的请求

        switch (methodType) {

            case "LOGIN": {

                if (response.status < 0) {
                    logger.info(response.errorStr);
                } else {
                    UserBean user = new UserBean();
                    user.uuid = response.uuid;
                    user.token = response.token;
                    // clientDB.insert("user",user);
                    CacheManager.put("user", user, 30 * 60 * 1000);
                    logger.info(response.message);
                }
                break;

            }
            case "SEND": {
                AudioUtils.playAudioThread();
                logger.info("message >> " + response.uuid + " : " + response.message);
                break;
            }

            case "LOGOUT": {
                logger.info(response.message);
                break;
            }

            case "SYS": {
                logger.info(response.message);
                break;
            }

            case "FILESERVICEREQUEST": {
                String fileName = new File(response.message).getName();
                String rootPath = GetServerRealPathUnit.getPath("file");
                logger.info(response.uuid + " 发送来个文件" + fileName + "  存放在目录：" + rootPath + " 下");
                //BeanFactory.createBean("IM");
                // String rootPath="D:\\test";
                File file = new File(rootPath, fileName);
                Integer port = 9999;
                InetAddress localhost = InetAddress.getLocalHost();
                String ip = localhost.getHostAddress();
                ReceiveFile receiveFile = new ReceiveFile(port, file);
                Thread thread = new Thread(receiveFile);
                thread.start();
                ZGMPBean request = response;
                request.methodType = "SEND";
                request.operationType = "FILESERVICEREADY";
                String uuid = request.targetUuid;
                String targetUuid = request.uuid;
                request.targetUuid = targetUuid;
                request.uuid = uuid;
                String message = "";
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("ip", ip);
                jsonObject.put("port", port);
                jsonObject.put("filePath", request.message);
                message = JSON.toJSONString(jsonObject);
                request.message = message;
                String json = EntityUtils.serialize(request);
                ctx.writeAndFlush(json + "\r\n");
                break;
            }

            case "FILESERVICEREADY": {
                logger.info("开始传输");
                JSONObject jsonObj = JSON.parseObject(response.message);
                File file = new File(jsonObj.getString("filePath"));
                SendFile sendFile = new SendFile(file, jsonObj.getString("ip"), jsonObj.getInteger("port"));
                Thread thread = new Thread(sendFile);
                thread.start();
                break;
            }

            case "HEARTBEAT": {
                //    logger.info("message  :" + response.message);
                String json = null;
                ZGMPBean request = new ZGMPBean("REQUEST");
                request.methodType = "HEARTBEAT";
                //UserBean userBean=(UserBean) clientDB.selectOne("user");
                UserBean user = (UserBean) CacheManager.get("user");
                CacheManager.put("user", user, 30 * 60 * 1000);
                request.uuid = response.uuid;
                request.message = "维持心跳";
                request.heartBeatID = response.heartBeatID;
                json = EntityUtils.serialize(request);
                ctx.writeAndFlush(json + "\r\n");
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
