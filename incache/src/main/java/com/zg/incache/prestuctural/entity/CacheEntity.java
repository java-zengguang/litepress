package com.zg.incache.prestuctural.entity;



import com.zg.common.util.reflect.JsonUtil;
import jakarta.json.JsonObject;

import java.util.UUID;

public class CacheEntity {
    public String uuid = UUID.randomUUID().toString();
    public long timeStamp = System.currentTimeMillis();
    public long validMillisecond;
    public Class aClass;
    public String jsonObject;



    public CacheEntity(Object object, long validMillisecond) {
        jsonObject = JsonUtil.obj2String(object);
        this.aClass = object.getClass();
        this.validMillisecond = validMillisecond;
    }


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public long getValidMillisecond() {
        return validMillisecond;
    }

    public void setValidMillisecond(long validMillisecond) {
        this.validMillisecond = validMillisecond;
    }

    public Object getJsonObject() {
        return JsonUtil.string2Obj(jsonObject,aClass);
    }


    public Class getaClass() {
        return aClass;
    }

    public void setaClass(Class aClass) {
        this.aClass = aClass;
    }
}
