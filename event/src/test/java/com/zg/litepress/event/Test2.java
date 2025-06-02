package com.zg.litepress.event;



import com.zg.litepress.event.bus.RocketMQBus;
import com.zg.litepress.event.en.EventStage;

import com.zg.litepress.event.entity.RocketConfig;
import com.zg.litepress.event.entity.SSDBConfig;
import com.zg.litepress.event.event.BaseEvent;
import com.zg.litepress.event.event.manager.EventStateManager;
import com.zg.litepress.event.event.manager.SimpleEventStateManager;
import com.zg.litepress.event.event.rule.EventTransitionRule;
import com.zg.litepress.event.event.rule.EventTransitionRuleBuilder;
import com.zg.litepress.event.exception.StateTransitinException;
import com.zg.litepress.event.subsriber.BaseEventListener;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.nutz.ssdb4j.SSDBs;
import org.nutz.ssdb4j.spi.SSDB;
import org.tinylog.Logger;

import java.io.IOException;

public class Test2 {

    private static SSDB createSSDB(SSDBConfig ssdbConfig) {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setTestWhileIdle(true);
        config.setMaxWaitMillis(1000);
        config.setMaxTotal(10);
        config.setMinIdle(0);
        config.setMaxIdle(10);
        config.setTestOnBorrow(true);
        SSDB ssdb = SSDBs.pool(ssdbConfig.ip, Integer.parseInt(ssdbConfig.port), Integer.parseInt(ssdbConfig.somillis), config,
                ssdbConfig.password.getBytes());
        return ssdb;
    }


    public static void main(String args[]) throws MQClientException, InterruptedException, IOException, StateTransitinException, MQBrokerException, RemotingException {


        RocketConfig rocketConfig = new RocketConfig();
        rocketConfig.group = "BASE_STATE";
        rocketConfig.namesrvAddr = "10.7.128.187:9876;10.7.128.188:9876";
        rocketConfig.accessKey = "rocketmq2";
        rocketConfig.secretKey = "12345678";
        rocketConfig.topic = "BASE_STATE";

        SSDBConfig ssdbConfig = new SSDBConfig();

        ssdbConfig.ip = "10.7.128.189";
        ssdbConfig.port = "5508";
        ssdbConfig.password = "Tm9udmVoaWNsZVAzODBTU0RCQXV0aFRva2Vu";

        SSDB ssdb = createSSDB(ssdbConfig);
        RocketMQBus messageBus = new RocketMQBus(rocketConfig);

for(int i=0;i<1000000;i++) {
    Logger.info("初始化");
}
        messageBus.subscriber("say", new BaseEventListener() {
            @Override
            public void callBack(String eventMessage) throws Exception {
                Logger.info("我收到了 say " + eventMessage);
            }
        });


        EventTransitionRule eventTransitionRule = new EventTransitionRuleBuilder().event("say").to("done")
                .action((baseEvent) -> {
                    ssdb.decr(baseEvent.businessNo, 1);
                    Logger.info("计数");
                }).build();


        EventStateManager eventStateManager = new SimpleEventStateManager();
        eventStateManager.addEventTransitionRule("say", EventStage.SUCCESSFUL.name(), eventTransitionRule);

        messageBus.setEventStateManager(eventStateManager);
        messageBus.init();

        ssdb.set("001", 10);
        Logger.info("事件发布");
        for(int i=0;i<10;i++) {
            messageBus.publish(new BaseEvent("say", "001", "hello"));
        }


        boolean flag = true;
        while (flag) {

            Integer x = ssdb.get("001").asInt();
            Logger.info(x);
            if (x < 1) {
                flag = false;
            }else{
                Thread.sleep(1000);
            }
        }


        Logger.info("结束");

    }
}
