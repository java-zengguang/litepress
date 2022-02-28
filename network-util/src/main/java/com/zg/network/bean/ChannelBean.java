package com.zg.network.bean;

import io.netty.channel.Channel;

/**
 * Created by Administrator on 2019/3/4 0004.
 */
public class ChannelBean {

    public String uuid;
    public Channel channel;
    public long time;
    public int count;
    public int status;   // 0 未发送心跳   1 已发送心跳  -1 channel失效
    public String token;
    public String heartBeatID;

    public ChannelBean(String uuid, Channel channel, long time,String token, int count, int status) {
        this.uuid = uuid;
        this.channel = channel;
        this.time = time;
        this.token=token;
        this.count = count;
        this.status = status;
    }
}
