package com.zg.event.driver.subsriber.components;

import com.zg.event.driver.event.BaseEvent;

public interface EventComponent {

    BaseEvent dealEvent(BaseEvent baseEvent) throws Exception;

}
