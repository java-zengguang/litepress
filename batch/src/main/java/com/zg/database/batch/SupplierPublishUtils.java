package com.zg.database.batch;


import com.zg.database.batch.handler.WorkHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Supplier;

public class SupplierPublishUtils {


    public static List dataSupplierPublishParallel(Supplier<List<?>> dataSupplier, WorkHandler workHandler) throws ExecutionException, InterruptedException {
        // 创建一个固定大小的线程池
        ExecutorService executor = new ThreadPoolExecutor(3, 3,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
        List resultList = new ArrayList<>();
        boolean hasMoreData = true;
        while (hasMoreData) {
            List<?> batchData = dataSupplier.get();
            if (batchData == null || batchData.isEmpty()) {
                hasMoreData = false;
            } else {
                Object result = executor.submit(new Callable<Object>() {
                    @Override
                    public Object call() throws Exception {
                        return workHandler.doHandler(batchData);
                    }
                }).get();
                resultList.add(result);
            }
        }
        executor.shutdown();
        return resultList;
    }

    public static List dataSupplierPublishSerial(Supplier<List<?>> dataSupplier, WorkHandler workHandler) {
        List resultList = new ArrayList<>();
        boolean hasMoreData = true;
        while (hasMoreData) {
            List<?> batchData = dataSupplier.get();
            if (batchData == null || batchData.isEmpty()) {
                hasMoreData = false;
            } else {
                resultList.add(workHandler.doHandler(batchData));
            }
        }
        return resultList;
    }


}
