package com.zg.network.common.heartbeat;

import com.zg.network.bean.ChannelBean;
import org.tinylog.Logger;

import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2019/3/4 0004.
 */
public abstract class BaseHeartbeatHandle implements Runnable {

    public Map<String, ChannelBean> channelMap;

    public long HBTIME = 0;   //心跳时间


    public BaseHeartbeatHandle(Map<String, ChannelBean> channelMap, long HBTIME) {
        this.channelMap = channelMap;
        this.HBTIME = HBTIME;

    }


    public abstract void execut();


    protected void sendHeartbeat() throws Exception {

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                execut();
            }
        }, new Date(), HBTIME);

    }

    @Override
    public void run() {
        try {
            if (HBTIME != 0) {
                sendHeartbeat();
            }
        } catch (Exception e) {
            Logger.error(e);
        }
    }
}
