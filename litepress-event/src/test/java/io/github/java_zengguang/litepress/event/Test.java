package io.github.java_zengguang.litepress.event;


import io.github.java_zengguang.litepress.event.bus.RocketMQBus;
import io.github.java_zengguang.litepress.event.entity.RocketConfig;
import io.github.java_zengguang.litepress.event.event.BaseEvent;
import io.github.java_zengguang.litepress.event.event.rule.EventTransitionRule;
import io.github.java_zengguang.litepress.event.event.rule.EventTransitionRuleBuilder;
import io.github.java_zengguang.litepress.event.subsriber.BaseEventListener;
import io.github.java_zengguang.litepress.react.semaphore.impl.LocalSemaphoreManager;
import org.tinylog.Logger;

public class Test {

    public static void main(String args[]) throws Exception {

        RocketConfig rocketConfig = new RocketConfig();
        rocketConfig.group = "BASE_STATE";
        rocketConfig.namesrvAddr = "10.7.128.187:9876;10.7.128.188:9876";
        rocketConfig.accessKey = "rocketmq2";
        rocketConfig.secretKey = "12345678";
        rocketConfig.topic = "BASE_STATE";


        RocketMQBus messageBus = new RocketMQBus(rocketConfig);


        Logger.info("初始化");
        BaseEventListener eventListener = new BaseEventListener() {

            @Override
            public void saveEventState(BaseEvent baseEvent) {

            }
        };


        EventTransitionRule eventTransitionRule = new EventTransitionRuleBuilder()
                .event("say")
                .form("say")
                .to("say-done")
                .action((baseEvent) -> {
                    Logger.info("打招呼");
                }).build();

        EventTransitionRule eventTransitionRule1 = new EventTransitionRuleBuilder()
                .event("say")
                .form("say-done")
                .to("call-done")
                .action((baseEvent) -> {
                    Logger.info("回招呼！");
                }).build();
        eventListener.addEventTransitionRule(eventTransitionRule);
        eventListener.addEventTransitionRule(eventTransitionRule1);

        messageBus.register("say", eventListener);


        messageBus.setSemaphoreManager(new LocalSemaphoreManager());
        messageBus.init();

        Logger.info("暂停消费");
        messageBus.suspendCustomer();
        Thread.sleep(10000);

        Logger.info("事件发布");
        messageBus.publish(new BaseEvent("say","say", "1243"));

        Logger.info("开始消费");
        messageBus.resumeCustomer();

    }
}
