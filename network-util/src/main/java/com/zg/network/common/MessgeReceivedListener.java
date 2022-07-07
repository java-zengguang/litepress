package com.zg.network.common;



/**
 * Created by Administrator on 2019/2/22 0022.
 */
public interface MessgeReceivedListener {
    public void onMessageReceived(Object response);

    public void onMessageDisconnect();

    public void onMessageConnect();
}
