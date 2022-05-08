package com.zg.common.dao.assemble;

import com.zg.common.bean.entity.MetadataEntity;
import com.zg.common.dao.template.SimpleEntityDaoTemplate;
import com.zg.common.util.reflect.DynamicClass;
import com.zg.common.util.reflect.EntityUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class SimpleAssemble extends BaseAssemble {

    @Override
    public List assembList(List<List<MetadataEntity>> templeList, Class classes) throws IllegalAccessException, InstantiationException {
        List modelList=new ArrayList();
        if(templeList!=null&&templeList.size()>0) {
            for (List<MetadataEntity> columnList : templeList) {
                Object obj=classes.newInstance();
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
        List<List<MetadataEntity>> lists=new ArrayList<>();
        for(Object obj:objs){
            lists.add(analysis(obj));
        }
        return lists;
    }


}
