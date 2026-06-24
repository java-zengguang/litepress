package io.github.java_zengguang.litepress.db.dao.assemble;

import io.github.java_zengguang.litepress.core.bean.entity.MetaColumnPo;
import io.github.java_zengguang.litepress.core.bean.entity.MetaDataPo;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class SimpleAssemble<T> extends BaseAssemble<T> {

    public SimpleAssemble(String dbType) {
        super(dbType);
    }

    @Override
    public List<T> assembList(List<MetaDataPo> templeList, Class<T> classes) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        List<T> modelList = new ArrayList<>();
        if (templeList != null && !templeList.isEmpty()) {
            for (MetaDataPo metaDataPo : templeList) {
                T obj = classes.getDeclaredConstructor().newInstance();
                obj = assembling(metaDataPo, obj);
                modelList.add(obj);
            }
        }
        return modelList;
    }

    @Override
    public List<MetaDataPo> analysisList(List<T> obs) throws IllegalAccessException, InstantiationException {
        List<MetaDataPo> lists = new ArrayList<>();
        for (T obj : obs) {
            lists.add(analysis(obj));
        }
        return lists;
    }


}
