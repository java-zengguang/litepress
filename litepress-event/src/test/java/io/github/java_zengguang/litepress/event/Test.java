package io.github.java_zengguang.litepress.event;


import io.github.java_zengguang.litepress.event.bus.RocketMQBus;
import io.github.java_zengguang.litepress.event.entity.RocketConfig;
import io.github.java_zengguang.litepress.event.event.BaseEvent;
import io.github.java_zengguang.litepress.event.state.BaseStateModel;
import io.github.java_zengguang.litepress.event.state.StateModel;
import io.github.java_zengguang.litepress.event.state.po.StatePo;
import io.github.java_zengguang.litepress.event.state.rule.SimpleStateTransitionRule;
import io.github.java_zengguang.litepress.event.state.rule.EventTransitionRuleBuilder;
import io.github.java_zengguang.litepress.event.subsriber.BaseStateEventListener;
import io.github.java_zengguang.litepress.react.semaphore.impl.LocalSemaphoreManager;
import org.tinylog.Logger;

import java.util.List;

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



        SimpleStateTransitionRule stateTransitionRule = new EventTransitionRuleBuilder()
                .event("say")
                .to("say-done")
                .action((baseEvent) -> {
                    Logger.info("打招呼");
                    messageBus.publish(baseEvent);
                }).build();

        SimpleStateTransitionRule stateTransitionRule1 = new EventTransitionRuleBuilder()
                .event("call-say")
                .form(List.of("say-done"))
                .to("call-done")
                .action((baseEvent) -> {
                    Logger.info("回招呼！");
                }).build();

        StateModel stateModel=new BaseStateModel("xxxx") {
            @Override
            public StatePo getState(String id) {
                return null;
            }

            @Override
            public void saveState(String id, StatePo statePo) {

            }


        };

        BaseStateEventListener eventListener = new BaseStateEventListener(stateModel) {


            @Override
            public void dealEvent(BaseEvent event) throws Exception {

            }

            @Override
            public void beforeDealEvent(BaseEvent event) throws Exception {

            }

            @Override
            public void afterDealEvent(BaseEvent event) throws Exception {

            }


        };
        stateModel.addEventTransitionRule(stateTransitionRule);
        stateModel.addEventTransitionRule(stateTransitionRule1);



        messageBus.register( eventListener);


        messageBus.setSemaphoreManager(new LocalSemaphoreManager());
        messageBus.init();

        Logger.info("暂停消费");
        messageBus.suspendCustomer();
        Thread.sleep(10000);

        Logger.info("事件发布");
        messageBus.publish(new BaseEvent());

        Logger.info("开始消费");
        messageBus.resumeCustomer();

    }
}
