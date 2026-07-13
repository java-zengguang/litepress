package io.github.java_zengguang.litepress.db.dao.assemble;

import io.github.java_zengguang.litepress.db.po.EntityDataPo;
import io.github.java_zengguang.litepress.db.po.MetaDataPo;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public interface Assemble<T> {
    //装配数据到实体类
    List<T> assembList(List<MetaDataPo> templeList, Class<T> classes) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException;

    //实体类逆向解析
    List<EntityDataPo> analysisList(List<T> obs) throws IllegalAccessException, InstantiationException;

    //装配数据到实体类
    T assembling(MetaDataPo metaDataPo, T obj) throws IllegalAccessException;


    EntityDataPo analysis(T obj) throws IllegalAccessException, InstantiationException;

}
