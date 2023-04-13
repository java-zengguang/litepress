package com.zg.network.im.service;

import com.zg.common.util.reflect.EntityUtils;
import com.zg.network.bean.ChannelBean;
import com.zg.network.bean.ZGMPBean;
import com.zg.network.common.heartbeat.BaseHeartbeatHandle;
import com.zg.network.im.login.LoginManager;
import io.netty.channel.Channel;
import org.tinylog.Logger;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Administrator on 2019/3/4 0004.
 */
public class IMHeartbeatHandle extends BaseHeartbeatHandle {

    //  public final long HBTIME=30*60*1000;   //心跳时间


    public IMHeartbeatHandle(Map<String, ChannelBean> channelMap, long HBTIME) {
        super(channelMap, HBTIME);


    }

    @Override
    public void execut() {

        for (Map.Entry<String, ChannelBean> entry : channelMap.entrySet()) {
            ChannelBean channelBean = entry.getValue();
            int status = channelBean.status;
            if (status < 0) {    //判断是否失效
                Logger.info(channelBean.uuid + " 的channel失效");

                LoginManager.logout(channelBean.uuid, channelBean.token);  //退出登陆

            } else {

                if (channelBean.count < 0) {    //判断是否已经多次未接收到心跳回复
                    channelBean.status = -1;
                } else {   //发送心跳信息
                    channelBean.heartBeatID = UUID.randomUUID().toString();   //更新心跳ID
                    Channel channel = channelBean.channel;
                    String json = null;
                    ZGMPBean response = new ZGMPBean("RESPONSE");
                    response.uuid = channelBean.uuid;
                    Logger.info("向" + channelBean.uuid + "发送心跳");
                    response.message = "心跳";
                    response.methodType = "HEARTBEAT";
                    response.heartBeatID = channelBean.heartBeatID;
                    response.sendTime = new Date().getTime();
                    json = EntityUtils.serialize(response);
                    channel.writeAndFlush(json + "\r\n");
                    channelBean.time = new Date().getTime();
                    channelBean.count--;
                }
            }
        }
    }


}
