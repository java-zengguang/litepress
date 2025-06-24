package io.github.java_zengguang.litepress.web.netty.sse;


import io.github.java_zengguang.litepress.core.util.reflect.JsonUtil;
import io.github.java_zengguang.litepress.event.subsriber.BaseEventListener;

public abstract class SSEEventListener extends BaseEventListener {
    @Override
    public void callBack(String eventMessage)   {
        SSEDto sseDto = JsonUtil.string2Obj(eventMessage, SSEDto.class);
        dealSSE(sseDto);
    }

    public abstract void dealSSE(SSEDto sseDto);
}
