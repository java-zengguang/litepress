package io.github.java_zengguang.litepress.event.entity;

import io.github.java_zengguang.litepress.router.annotation.ZKRegister;
import io.github.java_zengguang.litepress.router.entity.RouterEntity;

@ZKRegister(name="eventRouter",namespace ="event",routerType = "event")
public class ZeroMQRouter extends RouterEntity {

}
