package com.zg.network.common.heartbeat;

import com.zg.network.bean.ChannelBean;

import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2019/3/4 0004.
 */
public abstract class BaseHeartbeatHandle implements Runnable{

    public Map<String,ChannelBean> channelMap;

    public final long HBTIME=60*1000;   //心跳时间


    public BaseHeartbeatHandle(Map<String,ChannelBean> channelMap){
       this.channelMap=channelMap;

    }


    public abstract void execut();


    protected  void sendHeartbeat() throws Exception {

        Timer timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                execut();
            }
        },new Date(),HBTIME);

    }

    @Override
    public void run() {
        try {
            sendHeartbeat();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
