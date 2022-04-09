package com.zg.direction.server;

import com.zg.bean.factory.BeanFactory;
import com.zg.direction.entity.ProviderConfig;
import com.zg.network.common.service.BaseService;
import com.zg.network.common.service.BaseServiceHandler;

public class ProviderService extends BaseService {

    private static ProviderConfig providerConfig = (ProviderConfig) BeanFactory.createBean("providerConfig");

    public ProviderService(BaseServiceHandler<String> baseServiceHandler, int port) {
        super(baseServiceHandler, port);
    }

    @Override
    public void startHeartbeat() {
   /*  IMHeartbeatHandle IMHeartbeatHandle =new IMHeartbeatHandle(BaseChannelGroups.getChanelGroups());
            Thread t=new Thread(IMHeartbeatHandle);
            t.start();*/
    }


    public static void main(String args[]) {
        ProviderService providerService = new ProviderService(new ProviderServiceHandler(), providerConfig.DTPPort);
        Thread thread = new Thread(providerService);
        thread.start();
    }
}
