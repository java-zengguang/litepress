package io.github.java_zengguang.litepress.event.client;

import io.github.java_zengguang.litepress.event.entity.RocketConfig;
import org.apache.rocketmq.acl.common.AclClientRPCHook;
import org.apache.rocketmq.acl.common.SessionCredentials;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.rebalance.AllocateMessageQueueAveragely;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.apache.rocketmq.remoting.RPCHook;
import org.tinylog.Logger;

public class RocketMQFactory {
    private static final RocketMQFactory rocketMQFactory = new RocketMQFactory();

    private RocketMQFactory() {
    }

    public static RocketMQFactory getInstance() {
        return rocketMQFactory;
    }

    public DefaultMQPushConsumer getMQConsumer(RocketConfig rocketConfig, String tags) throws MQClientException {
        Logger.info("创建消费者");
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(rocketConfig.group + "Customer", new AclClientRPCHook(new SessionCredentials(rocketConfig.accessKey, rocketConfig.secretKey)), new AllocateMessageQueueAveragely());
        // 设置访问密钥和密钥
        if (rocketConfig.workMode.contains("C")) {  //集群
            consumer.setMessageModel(MessageModel.CLUSTERING); //使用集群模式，初始化的时候定义监听的tag
        } else {
            consumer.setMessageModel(MessageModel.BROADCASTING);  //广播
        }
        if (rocketConfig.workMode.contains("P")) { //并行
            consumer.setConsumeThreadMin(Integer.parseInt(rocketConfig.consumeThreadMin));
            consumer.setConsumeThreadMax(Integer.parseInt(rocketConfig.consumeThreadMax));
        } else {  //串行
            //限定先进先出和一次消费一条
            consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
            consumer.setConsumeMessageBatchMaxSize(1);
        }
        consumer.setNamesrvAddr(rocketConfig.namesrvAddr);
        consumer.subscribe(rocketConfig.topic, tags);
        consumer.setMaxReconsumeTimes(Integer.parseInt(rocketConfig.retryCount));
        return consumer;
    }

    public DefaultMQProducer getMQProducer(RocketConfig rocketConfig) {
        Logger.info("创建生产者");
        RPCHook rpcHook = new AclClientRPCHook(new SessionCredentials(rocketConfig.accessKey, rocketConfig.secretKey));
        DefaultMQProducer producer = new DefaultMQProducer(rocketConfig.group + "Provider", rpcHook);
        // 设置身份验证的RPCHook
        producer.setNamesrvAddr(rocketConfig.namesrvAddr);
        producer.setSendMsgTimeout(3000);
        // 启动生产者实例
        return producer;
    }
}
