package com.zg.direction.server;

import com.zg.common.bean.factory.BeanFactory;
import com.zg.direction.entity.ProviderConfig;
import com.zg.network.common.service.BaseService;
import com.zg.network.common.service.BaseServiceHandler;

public class ProviderService extends BaseService {

    private static ProviderConfig providerConfig = (ProviderConfig) BeanFactory.createBean("providerConfig");

    public ProviderService(BaseServiceHandler<String> baseServiceHandler) {
        super(baseServiceHandler, providerConfig.DTPPort);
    }

    public static void main(String args[]) {
        ProviderService providerService = new ProviderService(new ProviderServiceHandler());
        Thread thread = new Thread(providerService);
        thread.start();
    }

    @Override
    public void startHeartbeat() {

    }
}
