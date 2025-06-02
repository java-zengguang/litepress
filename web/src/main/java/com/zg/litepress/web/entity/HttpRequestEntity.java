package com.zg.litepress.web.entity;

import com.zg.litepress.core.bean.entity.MainModel;
import java.util.*;

public class HttpRequestEntity extends MainModel {
    public String methodType;
    public String contentType;
    public String url;
    public String path;
    public Map<String, List<String>> headers=new HashMap<>();
    public List<CookieEntity> cookies=new ArrayList<>();
    public String sceneType; //场景类型  FILE 、 JSON  、FORM  、SSE
    public Map<String, Object> paramMap=new HashMap<>();

}
