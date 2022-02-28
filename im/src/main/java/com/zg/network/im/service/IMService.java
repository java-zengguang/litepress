package com.zg.network.im.service;

import com.zg.network.common.service.BaseService;
import com.zg.network.common.service.BaseServiceHandler;

/**
 * Created by Administrator on 2019/2/22 0022.
 */
public  class IMService extends BaseService {


    public IMService(BaseServiceHandler<String> baseServiceHandler, int port) {
        super(baseServiceHandler, port);
    }

    @Override
    public void startHeartbeat() {
        IMHeartbeatHandle IMHeartbeatHandle =new IMHeartbeatHandle(IMChannelGroups.getChanelGroups(),60*1000 );
        Thread t=new Thread(IMHeartbeatHandle);
        t.start();
    }
}
