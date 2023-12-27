package com.zg.chain.common.factory;

import com.zg.chain.common.drivers.AutoDriver;
import com.zg.chain.common.entity.BaseProcess;
import com.zg.chain.common.entity.BaseProcessBatch;
import org.tinylog.Logger;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class DoMainDriver {

    private static final DoMainDriver doMainDriver = new DoMainDriver();
    private final Map<String, Object> beanMap = new HashMap<>();
    private final DriverFactory driverFactory = DriverFactory.newInstance();

    public DoMainDriver() {
    }

    public static DoMainDriver getInstance() {
        return doMainDriver;
    }


    public static <T extends BaseProcessBatch<t>, t extends BaseProcess> List<T> doGroup(Class<T> classType, List<t> list) throws IllegalAccessException, InstantiationException {
        List<T> resultList = new ArrayList<>();
        //先分组，保证后续流程，数据完整性
        Map<String, List<t>> batchMap = list.stream().collect(Collectors.groupingBy(t::getExecutebatchno));
        Set<String> batchSet = batchMap.keySet();
        for (String batchNo : batchSet) {
            List<t> batchSinoSigSQLLogEntitys = batchMap.get(batchNo);
            Map<String, List<t>> systemFlagMap = batchSinoSigSQLLogEntitys.stream().collect(Collectors.groupingBy(BaseProcess::getSystemflag));
            Set<String> systemFlagSet = systemFlagMap.keySet();
            for (String systemFlag : systemFlagSet) {
                List<t> systemFlagSinoSigSQLLogEntitys = systemFlagMap.get(systemFlag);
                Map<String, List<t>> functionFlagMap = systemFlagSinoSigSQLLogEntitys.stream().collect(Collectors.groupingBy(BaseProcess::getFunctionflag));
                Set<String> functionFlagSet = functionFlagMap.keySet();
                for (String functionFlag : functionFlagSet) {
                    T sinoSigSQLBatchEntity = classType.newInstance();
                    sinoSigSQLBatchEntity.executebatchno = batchNo;
                    sinoSigSQLBatchEntity.systemflag = systemFlag;
                    sinoSigSQLBatchEntity.functionFlag = functionFlag;
                    sinoSigSQLBatchEntity.executeList = functionFlagMap.get(functionFlag);
                    resultList.add(sinoSigSQLBatchEntity);  //返回结果
                }

            }
        }

        return resultList;
    }


    public <T extends BaseProcessBatch<t>, t extends BaseProcess> List<T> doStart(Class<T> tClass, List<t> list) throws Exception {
        List<T> sinoSigSQLBatchEntities = doGroup(tClass, list);
        CountDownLatch cdl = new CountDownLatch(sinoSigSQLBatchEntities.size());
        ExecutorService executor = Executors.newCachedThreadPool();
        sinoSigSQLBatchEntities.stream().forEach(sinoSigSQLBatchEntitie -> executor.execute(() -> {
            Logger.info("线程开始");
            doExecute(sinoSigSQLBatchEntitie);
            Logger.info("第" + cdl.getCount() + "线程结束");
            cdl.countDown();
        }));
        executor.shutdown();
        cdl.await();
        return sinoSigSQLBatchEntities;

    }


    public <T extends BaseProcessBatch> T doExecute(T baseProcessBatch) {


        AutoDriver autoDriver = null;
        try {
            autoDriver = driverFactory.getDriver(baseProcessBatch.systemflag, baseProcessBatch.functionFlag);
            baseProcessBatch = autoDriver.doExecute(baseProcessBatch);
        } catch (Exception e) {
            Logger.error(e);
            baseProcessBatch.status = "-1";
            baseProcessBatch.setMessage(e.getMessage());
        }
        return baseProcessBatch;
    }


}
