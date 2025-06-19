package com.zg.event.driver.bus;


import com.zg.common.error.BizException;
import com.zg.common.util.IpConfig;
import com.zg.common.util.reflect.JsonUtil;
import com.zg.event.driver.entity.ZoreMQConfig;
import com.zg.event.driver.event.BaseEvent;
import com.zg.event.driver.exception.StateTransitinException;
import com.zg.event.driver.subsriber.EventListener;
import com.zg.router.entity.RouterEntity;
import com.zg.router.entity.RouterRegisterConfig;
import com.zg.router.register.ServiceManager;
import com.zg.router.register.RouterRegister;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.tinylog.Logger;
import org.zeromq.ZMQ;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BaseBistributedZeroMQBus extends BaseMessageBus implements  MessageBus {

    private ZoreMQConfig zoreMQConfig;

    private ZMQ.Context context;

    private Map<String, ZMQ.Socket> publisherMap = new HashMap();

    private Map<String, ZMQ.Socket> subscriberMap = new HashMap();


    private RouterRegister<RouterEntity> routerRegister;

    private String ip;

    private ExecutorService customerExecutor ;

    private ZMQ.Socket getSubscriber(String eventType) {
        String address = "tcp://" + ip + ":" + zoreMQConfig.port;
        ZMQ.Socket subscriber = subscriberMap.get(address);
        try {
            RouterEntity routerEntity = new RouterEntity();
            routerEntity.host = ip;
            routerEntity.port = Integer.parseInt(zoreMQConfig.port);
            routerEntity.path = eventType;
            routerEntity.version = new Date().getTime() + "";
            routerEntity.routerType = "event";
            routerRegister.putRouter(routerEntity);
        } catch (Exception e) {
            Logger.error(e, "注册失败");
        }


        return subscriber;
    }

    private ZMQ.Socket getPublisher(String eventType) throws InterruptedException {
        RouterEntity router = routerRegister.getRouter(eventType);
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
        routerRegisterConfig.namespace="/event";
        routerRegisterConfig.routerType="event";
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

        customerExecutor = Executors.newVirtualThreadPerTaskExecutor();

    }

    public void init() throws MQClientException, InterruptedException, UnknownHostException {
        String address = "tcp://" + ip + ":" + zoreMQConfig.port;
        ZMQ.Socket subscriber = subscriberMap.get(address);
        Logger.info("初始化");
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(new Runnable() {
            @Override
            public void run() {
                // 订阅特定主题
                while (true) {
                    String receivedTopic = subscriber.recvStr(0); // 接收主题
                    String message = subscriber.recvStr(0);       // 接收消息
                    customerExecutor.submit(()->{
                        doCustomer(receivedTopic, message);
                    });
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
                doInvokeEventListener(eventListener, baseEvent);
            } catch (StateTransitinException e) {
                Logger.error(e);
            }

        }

    }

    @Override
    public void publish(BaseEvent baseEvent) throws IOException, StateTransitinException, MQBrokerException, RemotingException, InterruptedException, MQClientException {

        publishBefore(baseEvent);
        ZMQ.Socket publisher = getPublisher(baseEvent.eventType);
        if (publisher == null) {
            throw new BizException("未找到事件监听");
        }
        publisher.sendMore(baseEvent.eventType); // 发送主题
        publisher.send(JsonUtil.obj2String(baseEvent));   // 发送消息
        publishAfter(baseEvent);
    }


    @Override
    public void subscriber(String eventType, EventListener listener) throws MQClientException, InterruptedException {
        if (!eventListenerMap.containsKey(eventType)) {
            ZMQ.Socket subscriber = getSubscriber(eventType);
            subscriber.subscribe(eventType.getBytes());
            eventListenerMap.put(eventType, listener);
        }


    }



}
