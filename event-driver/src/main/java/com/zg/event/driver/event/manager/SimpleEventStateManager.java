package com.zg.event.driver.event.manager;

import com.alibaba.fastjson.JSON;
import com.zg.event.driver.entity.SSDBConfig;
import com.zg.event.driver.event.BaseEvent;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.nutz.ssdb4j.SSDBs;
import org.nutz.ssdb4j.spi.Response;
import org.nutz.ssdb4j.spi.SSDB;
import org.tinylog.Logger;

import java.io.IOException;

public  class SimpleEventStateManager extends BaseEventStateManager {

/*    public SSDBConfig ssdbConfig;



    private SSDB createSSDB() {
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
    }*/




}
