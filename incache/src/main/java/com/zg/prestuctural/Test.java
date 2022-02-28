package com.zg.prestuctural;

import com.zg.prestuctural.entity.CacheEntity;
import com.zg.prestuctural.manager.CacheManager;

import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String args[]){
        List list=new ArrayList<>();
        list.add("123");
        list.add("234");
        CacheManager.put("nihao",list,1000);
        System.out.println(CacheManager.get("nihao"));

    }
}
