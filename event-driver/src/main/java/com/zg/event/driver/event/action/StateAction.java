package com.zg.event.driver.event.action;

import com.zg.event.driver.event.BaseEvent;

public interface StateAction {
   void deal(BaseEvent baseEvent) throws Exception;
}
