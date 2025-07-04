package io.github.java_zengguang.litepress.db.dao.assemble;

import io.github.java_zengguang.litepress.core.bean.entity.MetadataEntity;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public interface Assemble<T> {
    //装配数据到实体类
    List<T> assembList(List<List<MetadataEntity>> templeList, Class<T> classes) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException;

    //实体类逆向解析
    List<List<MetadataEntity>> analysisList(List<T> obs) throws IllegalAccessException, InstantiationException;

    //装配数据到实体类
    T assembling(MetadataEntity metadataEntity, T obj) throws IllegalAccessException;

    //实体类逆向解析
    List<MetadataEntity> analysis(T obj) throws IllegalAccessException, InstantiationException;
}
