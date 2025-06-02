package com.zg.router.register;

import com.zg.router.entity.RouterEntity;

public interface RouterService {
    RouterEntity start();
    void close();
    RouterEntity reStart();
}
