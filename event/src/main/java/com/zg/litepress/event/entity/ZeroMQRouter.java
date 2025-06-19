package com.zg.litepress.event.entity;

import com.zg.litepress.router.annotation.ZKRegister;
import com.zg.litepress.router.entity.RouterEntity;

@ZKRegister(name="eventRouter",namespace ="event",routerType = "event")
public class ZeroMQRouter extends RouterEntity {

}
