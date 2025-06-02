package com.zg.litepress.event;

import com.zg.litepress.event.bus.ZoreMQBus;
import com.zg.litepress.event.entity.ZoreMQConfig;
import com.zg.litepress.event.event.BaseEvent;
import com.zg.litepress.event.subsriber.BaseEventListener;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.tinylog.Logger;

public class Test0 {

    public static void main(String args[]) throws Exception {
        ZoreMQConfig zoreMQConfig = new ZoreMQConfig();
        zoreMQConfig.port = "5555";
        zoreMQConfig.registerURL = "10.7.136.172:2181";
        ZoreMQBus bus = new ZoreMQBus(zoreMQConfig);
        bus.subscriber("/hello", new BaseEventListener() {
            @Override
            public void callBack(String eventMessage) throws Exception {
                Logger.info("hello " + eventMessage);
            }
        });

        bus.subscriber("/1", new BaseEventListener() {
            @Override
            public void callBack(String eventMessage) throws Exception {
                Logger.info("1 " + eventMessage);
            }
        });

        bus.init();


        bus.publish(new BaseEvent("/hello", "zengguang"));
        bus.publish(new BaseEvent("/hello", "zengguang"));
        bus.publish(new BaseEvent("/1", "zengguang"));


        Thread.sleep(10000);
    }
}
