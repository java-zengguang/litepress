package io.github.java_zengguang.litepress.event.bus;

import io.github.java_zengguang.litepress.event.entity.ZoreMQConfig;

public class ZoreMQBus extends BaseZeroMQBus{
    public ZoreMQBus(ZoreMQConfig zoreMQConfig) {
        super(zoreMQConfig);
    }
}
