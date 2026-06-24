package io.github.java_zengguang.litepress.router.register;

import io.github.java_zengguang.litepress.router.entity.RouterEntity;

public interface RouterService {
    RouterEntity start();
    void close();
    RouterEntity reStart();
}
