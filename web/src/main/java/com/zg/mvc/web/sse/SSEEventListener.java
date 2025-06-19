package com.zg.mvc.web.sse;

import com.zg.common.util.reflect.JsonUtil;
import com.zg.event.driver.subsriber.BaseEventListener;

public abstract class SSEEventListener extends BaseEventListener {
    @Override
    public void callBack(String eventMessage)   {
        SSEDto sseDto = JsonUtil.string2Obj(eventMessage, SSEDto.class);
        dealSSE(sseDto);
    }

    public abstract void dealSSE(SSEDto sseDto);
}
