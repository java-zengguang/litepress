package com.zg.network.im.service;

import com.zg.network.common.service.BaseKeepService;
import com.zg.network.common.service.BaseKeepServiceHandler;


/**
 * Created by Administrator on 2019/2/22 0022.
 */
public class IMService extends BaseKeepService implements Runnable {


    public IMService(BaseKeepServiceHandler baseServiceHandler, int port) {
        super(baseServiceHandler, port);
    }



    @Override
    public void run() {
        super.doMain();
    }
}
