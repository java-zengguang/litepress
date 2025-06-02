package com.zg.router.register;

import com.zg.router.entity.RouterEntity;

public interface ServiceManager {

    RouterEntity start(RouterService routerService) throws Exception;
    void close(RouterEntity router);
    void restart(RouterEntity router) throws Exception;
}
