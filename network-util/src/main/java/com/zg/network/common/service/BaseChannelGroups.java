package com.zg.network.common.service;

import com.zg.network.bean.ChannelBean;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Administrator on 2019/3/4 0004.
 */
public class BaseChannelGroups {
    public static Map<String, ChannelBean> channelGroups = new ConcurrentHashMap<>();




    public static void put(String key,ChannelBean channelBean){
        channelGroups.put(key,channelBean);
    }

    public static ChannelBean get(String key){
        return channelGroups.get(key);
    }

    public static void remove(String key) {
        channelGroups.remove(key);
    }

    public static Map<String,ChannelBean> getChanelGroups(){
        return channelGroups;
    }
}
