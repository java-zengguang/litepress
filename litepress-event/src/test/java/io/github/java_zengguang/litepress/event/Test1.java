package io.github.java_zengguang.litepress.event;


import io.github.java_zengguang.litepress.event.bus.MessageBus;
import io.github.java_zengguang.litepress.event.bus.RocketMQBus;
import io.github.java_zengguang.litepress.event.entity.RocketConfig;
import io.github.java_zengguang.litepress.event.event.BaseEvent;
import io.github.java_zengguang.litepress.event.subsriber.BaseEventListener;
import org.tinylog.Logger;

import java.util.Set;

public class Test1 {

    public static void main(String args[]) throws Exception {
        RocketConfig rocketConfig=  new RocketConfig();
        rocketConfig.group="BASE1";
        rocketConfig.namesrvAddr="10.7.128.187:9876;10.7.128.188:9876";
        rocketConfig.accessKey="rocketmq2";
        rocketConfig.secretKey="12345678";
        rocketConfig.topic="BASE";
        RocketMQBus messageBus=new RocketMQBus(rocketConfig);
        Logger.info("初始化");
        messageBus.register( new BaseEventListener(Set.of("tag1")) {




            @Override
            public void dealEvent(BaseEvent event) throws Exception {

            }

            @Override
            public void beforeDealEvent(BaseEvent event) throws Exception {

            }

            @Override
            public void afterDealEvent(BaseEvent event) throws Exception {

            }
        });
        messageBus.init();
        Logger.info("事件发布");
        messageBus.publish(new BaseEvent());
        messageBus.publish(new BaseEvent());
        messageBus.publish(new BaseEvent());
        messageBus.publish(new BaseEvent());
        messageBus.publish(new BaseEvent());
        messageBus.publish(new BaseEvent());
    }
}
