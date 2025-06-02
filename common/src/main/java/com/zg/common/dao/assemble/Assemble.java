package com.zg.common.dao.assemble;

import com.zg.common.bean.entity.MetadataEntity;

import java.util.List;

public interface Assemble {
    //装配数据到实体类
    List assembList(List<List<MetadataEntity>> templeList, Class classes) throws IllegalAccessException, InstantiationException;

    //实体类逆向解析
    List<List<MetadataEntity>> analysisList(List<Object> objs) throws IllegalAccessException, InstantiationException;

    //装配数据到实体类
    Object assembling(MetadataEntity metadataEntity, Object obj) throws IllegalAccessException;

    //实体类逆向解析
    List<MetadataEntity> analysis(Object obj) throws IllegalAccessException, InstantiationException;
}
