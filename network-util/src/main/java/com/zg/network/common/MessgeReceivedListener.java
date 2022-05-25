package com.zg.network.common;


import redis.clients.jedis.Response;

/**
 * Created by Administrator on 2019/2/22 0022.
 */
public interface MessgeReceivedListener {
    public void onMessageReceived(Response msg);

    public void onMessageDisconnect();

    public void onMessageConnect();
}
