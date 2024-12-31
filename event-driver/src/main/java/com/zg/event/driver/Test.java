package com.zg.event.driver;


import com.zg.database.react.semaphore.impl.LocalSemaphoreManager;
import com.zg.event.driver.bus.RocketMQBus;
import com.zg.event.driver.en.EventStage;
import com.zg.event.driver.entity.RocketConfig;
import com.zg.event.driver.event.BaseEvent;
import com.zg.event.driver.event.manager.EventStateManager;
import com.zg.event.driver.event.manager.SimpleEventStateManager;
import com.zg.event.driver.event.rule.EventTransitionRule;
import com.zg.event.driver.event.rule.EventTransitionRuleBuilder;
import com.zg.event.driver.exception.StateTransitinException;
import com.zg.event.driver.subsriber.BaseEventListener;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.tinylog.Logger;

import java.io.IOException;

public class Test {

    public static void main(String args[]) throws MQClientException, InterruptedException, IOException, StateTransitinException, MQBrokerException, RemotingException {

        RocketConfig rocketConfig = new RocketConfig();
        rocketConfig.group = "BASE_STATE";
        rocketConfig.namesrvAddr = "10.7.128.187:9876;10.7.128.188:9876";
        rocketConfig.accessKey = "rocketmq2";
        rocketConfig.secretKey = "12345678";
        rocketConfig.topic = "BASE_STATE";

/*        SSDBConfig ssdbConfig=new SSDBConfig();

        ssdbConfig.ip="10.7.128.189";
        ssdbConfig.port="5508";
        ssdbConfig.password="Tm9udmVoaWNsZVAzODBTU0RCQXV0aFRva2Vu";*/


        RocketMQBus messageBus = new RocketMQBus(rocketConfig);


        Logger.info("初始化");
        messageBus.subscriber("say", new BaseEventListener() {
            @Override
            public void callBack(String eventMessage) throws Exception {
                Logger.info("我收到了 say " + eventMessage);
            }
        });

        messageBus.subscriber("say-done", new BaseEventListener() {
            @Override
            public void callBack(String eventMessage) throws Exception {
                Logger.info("我收到了 say-done " + eventMessage);
            }
        });

        EventTransitionRule eventTransitionRule = new EventTransitionRuleBuilder().event("say").to("done").action((baseEvent) -> {
            Logger.info("回招呼");
            try {
                messageBus.publish(new BaseEvent("say-done", baseEvent.businessNo, baseEvent.processStage, "hello-done"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (StateTransitinException e) {
                throw new RuntimeException(e);
            } catch (MQBrokerException e) {
                throw new RuntimeException(e);
            } catch (RemotingException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (MQClientException e) {
                throw new RuntimeException(e);
            }
        }).build();

        EventTransitionRule eventTransitionRule1 = new EventTransitionRuleBuilder().event("say-done").form("done").to("good").action((baseEvent) -> {
            Logger.info("完成处理"+baseEvent.businessNo+baseEvent.processStage);
        }).build();

        EventStateManager eventStateManager = new SimpleEventStateManager();
        eventStateManager.addEventTransitionRule("say", EventStage.SUCCESSFUL.name(), eventTransitionRule);
        eventStateManager.addEventTransitionRule("say", EventStage.SUCCESSFUL.name(),eventTransitionRule1);

        messageBus.setEventStateManager(eventStateManager);
        messageBus.setSemaphoreManager(new LocalSemaphoreManager());
        messageBus.init();

        Logger.info("暂停消费");
        messageBus.suspendCustomer();
        Thread.sleep(10000);

        Logger.info("事件发布");
        messageBus.publish(new BaseEvent("say", "006", "hello"));
        messageBus.publish(new BaseEvent("say", "007", "hello1"));
        messageBus.publish(new BaseEvent("say", "008", "hello2"));
        Logger.info("开始消费");
        messageBus.resumeCustomer();

    }
}
