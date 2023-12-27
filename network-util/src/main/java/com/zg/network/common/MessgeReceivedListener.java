package com.zg.network.common;


/**
 * Created by Administrator on 2019/2/22 0022.
 */
public interface MessgeReceivedListener {
    void onMessageReceived(Object response);

    void onMessageDisconnect();

    void onMessageConnect();
}
