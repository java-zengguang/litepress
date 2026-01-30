package io.github.java_zengguang.litepress.event;

import io.github.java_zengguang.litepress.event.bus.ZoreMQBus;
import io.github.java_zengguang.litepress.event.entity.ZoreMQConfig;
import io.github.java_zengguang.litepress.event.event.BaseEvent;
import io.github.java_zengguang.litepress.event.subsriber.BaseEventListener;
import org.tinylog.Logger;

public class Test0 {

    public static void main(String args[]) throws Exception {
        ZoreMQConfig zoreMQConfig = new ZoreMQConfig();
        zoreMQConfig.port = "5555";
        zoreMQConfig.registerURL = "10.7.136.172:2181";
        ZoreMQBus bus = new ZoreMQBus(zoreMQConfig);
        bus.register("/hello", new BaseEventListener() {

            @Override
            public void saveEventState(BaseEvent baseEvent) {

            }
        });

        bus.register("/1", new BaseEventListener() {
            @Override
            public void saveEventState(BaseEvent baseEvent) {

            }
        });

        bus.init();


        bus.publish(new BaseEvent("/hello", "zengguang"));
        bus.publish(new BaseEvent("/hello", "zengguang"));
        bus.publish(new BaseEvent("/1", "zengguang"));


        Thread.sleep(10000);
    }
}
