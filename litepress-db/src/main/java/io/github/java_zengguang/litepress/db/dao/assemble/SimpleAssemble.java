package io.github.java_zengguang.litepress.db.dao.assemble;

import io.github.java_zengguang.litepress.core.bean.entity.MetadataEntity;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class SimpleAssemble<T> extends BaseAssemble<T> {

    public SimpleAssemble(String dbType) {
        super(dbType);
    }

    @Override
    public List<T> assembList(List<List<MetadataEntity>> templeList, Class<T> classes) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        List<T> modelList = new ArrayList<>();
        if (templeList != null && !templeList.isEmpty()) {
            for (List<MetadataEntity> columnList : templeList) {
                T obj = classes.getDeclaredConstructor().newInstance();
                for (MetadataEntity metadataEntity : columnList) {
                    obj = assembling(metadataEntity, obj);
                    modelList.add(obj);
                }
            }
        }
        return modelList;
    }

    @Override
    public List<List<MetadataEntity>> analysisList(List<T> obs) throws IllegalAccessException, InstantiationException {
        List<List<MetadataEntity>> lists = new ArrayList<>();
        for (T obj : obs) {
            lists.add(analysis(obj));
        }
        return lists;
    }


}
