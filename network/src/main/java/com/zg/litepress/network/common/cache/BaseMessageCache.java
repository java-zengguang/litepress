package com.zg.litepress.network.common.cache;

import com.zg.litepress.network.entity.BaseRequest;
import com.zg.litepress.network.entity.BaseTranslationProtocol;

import java.util.concurrent.ConcurrentHashMap;


public class BaseMessageCache {
    //用来实现同步的重要组件，根据请求的id阻塞线程，实现同步
    private static ConcurrentHashMap<String, BaseRequest> requestConcurrentHashMap = new ConcurrentHashMap<>();

    public static void addWaitingRequest(BaseRequest request) throws InterruptedException {
        requestConcurrentHashMap.put(request.id, request);
        request.block();
    }

    public static synchronized void dealResponse(BaseTranslationProtocol baseTranslationProtocol) throws InterruptedException {
        BaseRequest request = requestConcurrentHashMap.remove(baseTranslationProtocol.id);
        if (request != null) {
            request.result = baseTranslationProtocol;
            request.state = "3";
            request.unblock();
        }

    }

}
