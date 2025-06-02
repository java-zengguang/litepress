package com.zg.litepress.event.subsriber.components;

import com.zg.litepress.event.event.BaseEvent;

public interface EventComponent {

    BaseEvent dealEvent(BaseEvent baseEvent) throws Exception;

}
