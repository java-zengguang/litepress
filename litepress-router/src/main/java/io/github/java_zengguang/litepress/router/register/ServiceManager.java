package io.github.java_zengguang.litepress.router.register;

import io.github.java_zengguang.litepress.router.entity.RouterEntity;

public interface ServiceManager {

    RouterEntity start(RouterService routerService) throws Exception;
    void close(RouterEntity router);
    void restart(RouterEntity router) throws Exception;
}
