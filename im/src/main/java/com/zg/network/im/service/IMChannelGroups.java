package com.zg.network.im.service;

import com.zg.network.bean.ChannelBean;
import com.zg.network.common.service.BaseChannelGroups;
import io.netty.channel.Channel;

import java.util.Date;

public class IMChannelGroups extends BaseChannelGroups{
    public static void put(String key, String token, Channel channel){
        ChannelBean channelBean=new ChannelBean(key,channel,new Date().getTime(),token,3,0);
        put(key,channelBean);
    }


    public static Channel getChannel(String key){
        ChannelBean channelBean= get(key);
        if(channelBean!=null){
            return channelBean.channel;
        }
        return null;
    }

}
