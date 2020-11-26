package com.zg.prestuctural.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zg.util.reflect.FieldUtils;
import org.apache.commons.net.ntp.TimeStamp;

import java.util.UUID;

public class CacheEntity {
    public String uuid= UUID.randomUUID().toString();
    public long timeStamp=System.currentTimeMillis();;
    public long validMillisecond;
    public Class aClass;
    public Object jsonObject;

    public CacheEntity(JSONObject jsonObject,Class aClass,long validMillisecond){
        this.jsonObject=jsonObject;
        this.aClass=aClass;
        this.validMillisecond=validMillisecond;
    }

    public CacheEntity(Object object,long validMillisecond){
        jsonObject=JSONObject.toJSON(object);
        this.aClass=object.getClass();
        this.validMillisecond=validMillisecond;
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
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public Class getaClass() {
        return aClass;
    }

    public void setaClass(Class aClass) {
        this.aClass = aClass;
    }
}
