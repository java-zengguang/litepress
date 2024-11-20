package com.zg.event.driver.bus;


import com.zg.common.error.BizException;
import com.zg.common.util.IpConfig;
import com.zg.common.util.reflect.JsonUtil;
import com.zg.event.driver.en.EventStage;
import com.zg.event.driver.en.ProcessState;
import com.zg.event.driver.entity.ZoreMQConfig;
import com.zg.event.driver.event.BaseEvent;
import com.zg.event.driver.event.rule.EventTransitionRule;
import com.zg.event.driver.exception.StateTransitinException;
import com.zg.event.driver.subsriber.EventListener;
import com.zg.router.entity.RouterEntity;
import com.zg.router.entity.RouterRegisterConfig;
import com.zg.router.register.RouterRegister;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.tinylog.Logger;
import org.zeromq.ZMQ;
import zmq.socket.reqrep.Router;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BaseBistributedZeroMQBus extends BaseMessageBus implements RocketMQManager, MessageBus {

    private ZoreMQConfig zoreMQConfig;

    private ZMQ.Context context;

    private Map<String, ZMQ.Socket> publisherMap = new HashMap();

    private Map<String, ZMQ.Socket> subscriberMap = new HashMap();


    private RouterRegister<RouterEntity> routerRegister;

    private String ip;


    private ZMQ.Socket getSubscriber(String eventType) {
        String address = "tcp://" + ip + ":" + zoreMQConfig.port;
        ZMQ.Socket subscriber = subscriberMap.get(address);
        try {
            RouterEntity routerEntity = new RouterEntity();
            routerEntity.host = ip;
            routerEntity.port = Integer.parseInt(zoreMQConfig.port);
            routerEntity.path = eventType;
            routerEntity.clientVersion = new Date().getTime() + "";
            routerEntity.serviceType = "event";
            routerRegister.putRouter(routerEntity);
        } catch (Exception e) {
            Logger.error(e, "注册失败");
        }


        return subscriber;
    }

    private ZMQ.Socket getPublisher(String eventType) {
        RouterEntity router = routerRegister.getRouter("event", eventType);
        if (router != null) {
            String address = "tcp://" + router.host + ":" + zoreMQConfig.port;
            ZMQ.Socket publisher = publisherMap.get(address);
            if (publisher == null) {
                publisher = context.socket(ZMQ.PUB);
                publisher.connect(address);
                publisherMap.put(address, publisher);
            }
            return publisher;
        }
        return null;

    }

    public BaseBistributedZeroMQBus(ZoreMQConfig zoreMQConfig) throws Exception {
        this.ip = IpConfig.getLocalHostLANAddress().getHostAddress();
        this.zoreMQConfig = zoreMQConfig;
        context = ZMQ.context(1);
        RouterRegisterConfig routerRegisterConfig = new RouterRegisterConfig();
        routerRegisterConfig.registerURL = zoreMQConfig.registerURL;
        routerRegister = RouterRegister.getInstance(routerRegisterConfig);


        //初始化本地发布者和订阅者

        String address = "tcp://" + ip + ":" + zoreMQConfig.port;
        ZMQ.Socket publisher = context.socket(ZMQ.PUB);
        publisher.bind(address);
        publisherMap.put(address, publisher);


        ZMQ.Socket subscriber = context.socket(ZMQ.SUB);
        subscriber.connect(address);
        subscriberMap.put(address, subscriber);


    }

    public void init() throws MQClientException, InterruptedException, UnknownHostException {
        String address = "tcp://" + ip + ":" + zoreMQConfig.port;
        ZMQ.Socket subscriber = subscriberMap.get(address);
        Logger.info("初始化");
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        executor.submit(new Runnable() {
            @Override
            public void run() {
                // 订阅特定主题
                while (true) {
                    String receivedTopic = subscriber.recvStr(0); // 接收主题
                    String message = subscriber.recvStr(0);       // 接收消息
                    doCustomer(receivedTopic, message);
                }
            }
        });
        executor.shutdown();

        Thread.sleep(2000);
    }

    private void doCustomer(String topic, String message) {
        Logger.info("消息监听    " + new String(message));
        EventListener eventListener = eventListenerMap.get(topic);
        if (eventListener != null) {
            try {
                BaseEvent baseEvent = JsonUtil.string2Obj(message, BaseEvent.class);
                //第一个事件监听把初始化状态改成运行中
                if (ProcessState.INIT.name().equals(baseEvent.processState)) {
                    baseEvent.processState = ProcessState.PROGRESS.name();
                }
                //如果流程为运行中，执行监听逻辑
                if (ProcessState.PROGRESS.name().equals(baseEvent.processState)) {  //只有流程状态为2的时候，才是
                    addTrace(baseEvent);  //绑定链路信息
                    doEventTransitionRules(EventStage.PROGRESS.name(), baseEvent);
                    try {
                        eventListener.dealEvent(baseEvent);
                        doEventTransitionRules(EventStage.SUCCESSFUL.name(), baseEvent);
                    } catch (Exception e) {
                        Logger.error("事件处理异常" + e);
                        baseEvent.errorMessage = e.getMessage();
                        doEventTransitionRules(EventStage.FAILURE.name(), baseEvent);
                        baseEvent.processState = ProcessState.FAILURE.name();  //遇到异常将流程修改为异常终止
                    }
                }
            } catch (StateTransitinException e) {
                Logger.error(e);
            }

        }

    }


    @Override
    public void publish(BaseEvent baseEvent) throws IOException, StateTransitinException, MQBrokerException, RemotingException, InterruptedException, MQClientException {
        addTrace(baseEvent);  //绑定链路信息

        // 将初始状态的，流程状态转化为进行中
        if ("-1".equals(baseEvent.processState)) {
            throw new StateTransitinException("流程已经因异常终止");
        }
        if ("1".equals(baseEvent.processState)) {
            throw new StateTransitinException("流程已经完成");
        }
        ZMQ.Socket publisher = getPublisher(baseEvent.eventType);

        if (publisher == null) {

            throw new BizException("未找到事件监听");
        }

        publisher.sendMore(baseEvent.eventType); // 发送主题
        publisher.send(JsonUtil.obj2String(baseEvent));   // 发送消息
        doEventTransitionRules(EventStage.INIT.name(), baseEvent);  //发布后执行状态机
    }


    @Override
    public void subscriber(String eventType, EventListener listener) throws MQClientException, InterruptedException {
        if (!eventListenerMap.containsKey(eventType)) {
            ZMQ.Socket subscriber = getSubscriber(eventType);
            subscriber.subscribe(eventType.getBytes());
            eventListenerMap.put(eventType, listener);
        }


    }

    //执行事件流转规则
    public void doEventTransitionRules(String stage, BaseEvent baseEvent) throws StateTransitinException {
        if (eventStateManager != null) {
            baseEvent.eventStage = stage;
            Set<EventTransitionRule> eventTransitionRuleSet = eventStateManager.getEventTransitionRule(stage, baseEvent);
            if (eventTransitionRuleSet != null && eventTransitionRuleSet.size() > 0) {
                for (EventTransitionRule eventTransitionRule : eventTransitionRuleSet) {
                    eventTransitionRule.doTransitionState(baseEvent);
                    eventTransitionRule.doAction(baseEvent);
                    try {
                        String nextEvent = eventTransitionRule.getNextEvent();
                        if (nextEvent != null) {
                            baseEvent.eventType = nextEvent;
                            publish(baseEvent);
                        }
                    } catch (Exception e) {
                        new StateTransitinException(baseEvent.eventID + "事件流中断，事件发送异常", e);
                    }
                }
            }
        }

    }

    @Override
    public void suspendCustomer() {

    }

    @Override
    public void resumeCustomer() {

    }


}
