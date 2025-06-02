package com.zg.litepress.event.event.action;

import com.zg.litepress.event.event.BaseEvent;

public interface StateAction {
   void deal(BaseEvent baseEvent) throws Exception;
}
