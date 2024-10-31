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

    public SSDBConfig ssdbConfig;



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
    }


    public void saveTraceLog(BaseEvent baseEvent) {
        try {
            SSDB ssdb = createSSDB();
            ssdb.hset(baseEvent.processID, baseEvent.eventID, JSON.toJSONString(baseEvent));
            ssdb.close();
        } catch (IOException e) {
            Logger.error("SSDB关闭链接异常");

            throw new RuntimeException(e);
        }
    }


    public String getState(String bussNo) throws IOException {

        //查询事件ID是否正在执行中，处于正在执行状态的事件不能重复执行
        SSDB ssdb = createSSDB();
        Response response = ssdb.get(bussNo);
        ssdb.close();
        if (response.notFound()) {
            return null;
        } else {
            return response.asString();
        }


    }


    public void persistenceState(String stateBusiness,String state) throws IOException {
        SSDB ssdb = createSSDB();
        ssdb.set(stateBusiness,state);
        ssdb.close();
    }




}
