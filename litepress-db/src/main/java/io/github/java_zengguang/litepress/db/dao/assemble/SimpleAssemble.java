package io.github.java_zengguang.litepress.db.dao.assemble;

import io.github.java_zengguang.litepress.core.bean.entity.MetadataEntity;

import java.util.ArrayList;
import java.util.List;

public class SimpleAssemble extends BaseAssemble {

    public SimpleAssemble(String dbType) {
        super(dbType);
    }

    @Override
    public List assembList(List<List<MetadataEntity>> templeList, Class classes) throws IllegalAccessException, InstantiationException {
        List modelList = new ArrayList();
        if (templeList != null && templeList.size() > 0) {
            for (List<MetadataEntity> columnList : templeList) {
                Object obj = classes.newInstance();
                for (MetadataEntity metadataEntity : columnList) {
                    obj = assembling(metadataEntity, obj);
                    modelList.add(obj);
                }
            }
        }
        return modelList;
    }

    @Override
    public List<List<MetadataEntity>> analysisList(List<Object> objs) throws IllegalAccessException, InstantiationException {
        List<List<MetadataEntity>> lists = new ArrayList<>();
        for (Object obj : objs) {
            lists.add(analysis(obj));
        }
        return lists;
    }


}
