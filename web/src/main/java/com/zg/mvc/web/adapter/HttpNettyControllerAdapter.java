package com.zg.mvc.web.adapter;


public class HttpNettyControllerAdapter extends BaseControllerAdapter  {

    private static HttpNettyControllerAdapter nettyControllerAdapter;
    private HttpNettyControllerAdapter() {}
    public synchronized static HttpNettyControllerAdapter getInstance() {
        if (nettyControllerAdapter == null) {
            nettyControllerAdapter = new HttpNettyControllerAdapter();
        }
        return nettyControllerAdapter;
    }



}
