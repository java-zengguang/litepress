package io.github.java_zengguang.litepress.react.selector;

import io.github.java_zengguang.litepress.react.deal.DealHandler;

public interface Selector {
    void register(DealHandler dealHandler,Integer semaphoreValue);
}
