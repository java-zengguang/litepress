package com.zg.common.util.reflect;

import java.util.HashMap;

/**
 * Created by Administrator on 2018/12/24 0024.
 */
public class JsonMap extends HashMap {
    public JsonMap(Object success, Object message, Object list) {
        super.put("success", success);
        super.put("message", message);
        super.put("list", list);
    }
}
