package com.zg.litepress.router.register;

import com.zg.litepress.router.entity.RouterEntity;

public interface RouterService {
    RouterEntity start();
    void close();
    RouterEntity reStart();
}
