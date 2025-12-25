package io.github.java_zengguang.litepress.web.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpResponseEntity {
    public Map<String, List<String>> headers=new HashMap<>();
    public List<CookieEntity> cookies=new ArrayList<>();
    public Integer statusCode=200;
    public Object result;


    public  static HttpResponseEntity error(String message,Integer statusCode){
        HttpResponseEntity httpResponseEntity=new HttpResponseEntity();
        httpResponseEntity.statusCode=statusCode;
        httpResponseEntity.result=message;
        return httpResponseEntity;
    }

}
