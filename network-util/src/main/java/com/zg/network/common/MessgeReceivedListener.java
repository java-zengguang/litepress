package com.zg.network.common;


import javax.xml.ws.Response;

/**
 * Created by Administrator on 2019/2/22 0022.
 */
public interface MessgeReceivedListener {
    public void onMessageReceived(Response msg);

    public void onMessageDisconnect();

    public void onMessageConnect();
}
