package io.github.java_zengguang.litepress.event.bus;


import io.github.java_zengguang.litepress.event.entity.RocketConfig;
import org.apache.rocketmq.client.exception.MQClientException;

public class RocketMQBus extends BaseRocketMQBus {


    public RocketMQBus(RocketConfig rocketConfig){
        super(rocketConfig);
    }



}
