package com.zg.network.common.service;

import com.zg.network.bean.ChannelBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Administrator on 2019/3/4 0004.
 */
public class BaseChannelGroups {
    public static Map<String, ChannelBean> channelGroups = new ConcurrentHashMap<>();


    public static void put(String key, ChannelBean channelBean) {
        channelGroups.put(key, channelBean);
    }

    public static ChannelBean get(String key) {
        return channelGroups.get(key);
    }

    public static List<ChannelBean> getAll() {
        List list = new ArrayList();
        Set<String> keySet = channelGroups.keySet();
        for (String key : keySet) {
            list.add(channelGroups.get(key));
        }
        return list;
    }


    public static void remove(String key) {
        channelGroups.remove(key);
    }

    public static Map<String, ChannelBean> getChanelGroups() {
        return channelGroups;
    }
}
