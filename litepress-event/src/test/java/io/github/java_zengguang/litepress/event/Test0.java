package io.github.java_zengguang.litepress.event;

import io.github.java_zengguang.litepress.event.bus.ZoreMQBus;
import io.github.java_zengguang.litepress.event.entity.ZoreMQConfig;
import io.github.java_zengguang.litepress.event.event.BaseEvent;
import io.github.java_zengguang.litepress.event.subsriber.BaseEventListener;
import org.tinylog.Logger;

import java.util.Set;

public class Test0 {

    public static void main(String args[]) throws Exception {
        ZoreMQConfig zoreMQConfig = new ZoreMQConfig();
        zoreMQConfig.port = "5555";
        zoreMQConfig.registerURL = "10.7.136.172:2181";
        ZoreMQBus bus = new ZoreMQBus(zoreMQConfig);
        bus.register(new BaseEventListener(BaseEvent.class, Set.of("/hello")) {

            @Override
            public void init(BaseEvent baseEvent) throws Exception {

            }

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


        bus.init();


        bus.publish(new BaseEvent());
        bus.publish(new BaseEvent());
        bus.publish(new BaseEvent());


        Thread.sleep(10000);
    }
}
