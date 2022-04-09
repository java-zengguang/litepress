package com.zg.network.im.service;

import com.zg.network.bean.ChannelBean;
import com.zg.network.common.service.BaseChannelGroups;
import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class IMChannelGroups extends BaseChannelGroups {
    public static void put(String key, String token, Channel channel) {
        ChannelBean channelBean = new ChannelBean(key, channel, new Date().getTime(), token, 3, 0);
        put(key, channelBean);
    }


    public static Channel getChannel(String key) {
        ChannelBean channelBean = get(key);
        if (channelBean != null) {
            return channelBean.channel;
        }
        return null;
    }


    public static List<Channel> getAllChannel() {
        List<ChannelBean> list = getAll();
        List<Channel> channelList = new ArrayList<>();
        for (ChannelBean channelBean : list) {
            channelList.add(channelBean.channel);
        }
        return channelList;
    }

}
