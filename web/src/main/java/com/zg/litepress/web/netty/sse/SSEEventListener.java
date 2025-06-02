package com.zg.litepress.web.netty.sse;


import com.zg.litepress.core.util.reflect.JsonUtil;
import com.zg.litepress.event.subsriber.BaseEventListener;

public abstract class SSEEventListener extends BaseEventListener {
    @Override
    public void callBack(String eventMessage)   {
        SSEDto sseDto = JsonUtil.string2Obj(eventMessage, SSEDto.class);
        dealSSE(sseDto);
    }

    public abstract void dealSSE(SSEDto sseDto);
}
