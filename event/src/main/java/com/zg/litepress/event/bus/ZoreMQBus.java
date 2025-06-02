package com.zg.litepress.event.bus;

import com.zg.litepress.event.entity.ZoreMQConfig;

public class ZoreMQBus extends BaseZeroMQBus{
    public ZoreMQBus(ZoreMQConfig zoreMQConfig) {
        super(zoreMQConfig);
    }
}
