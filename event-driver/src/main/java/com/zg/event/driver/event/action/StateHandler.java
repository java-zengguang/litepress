package com.zg.event.driver.event.action;

import com.zg.event.driver.event.BaseEvent;

public interface StateHandler {
   boolean deal(BaseEvent baseEvent);

}
