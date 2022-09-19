package com.zg.common.dao.assemble;

import com.zg.common.annotation.AutoIncrease;
import com.zg.common.annotation.DBType;
import com.zg.common.annotation.NotCommitField;
import com.zg.common.annotation.PrimaryKey;
import com.zg.common.bean.entity.MetadataEntity;
import com.zg.common.dao.template.EntityDaoTemplate;
import com.zg.common.dao.template.EntityDaoTemplateFactory;
import com.zg.common.util.reflect.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseAssemble implements Assemble {
    public Logger logger = LoggerFactory.getLogger(this.getClass());
    public String dbType;

    public BaseAssemble(String dbType) {
        this.dbType = dbType;
    }

    public Object assembling(MetadataEntity metadataEntity, Object obj) throws IllegalAccessException {
        Class classes= obj.getClass();
        Field[] fields= classes.getFields();
        for(Field field: fields){
            String fileName=field.getName();
            if(fileName.equals(metadataEntity.fieldName)){
                if(field.getType().getSimpleName().equals(metadataEntity.fieldType)){
                    field.set(obj,metadataEntity.fieldValue);
                }else{
                    logger.info("实体类类型不匹配"+classes+":"+fileName);
                }

            }
        }
        return obj;
    }

    public List<MetadataEntity> analysis(Object obj) throws IllegalAccessException, InstantiationException {
        List<MetadataEntity> metadataEntityList=new ArrayList<>();
        Class classes=obj.getClass();
        String tableName = EntityUtils.getTableNameFromModel(classes);
        Field fields[] = classes.getFields();
        EntityDaoTemplate simpleEntityDaoTemplate= EntityDaoTemplateFactory.getTemplate(dbType);

        for (Field field : fields) {
            MetadataEntity metadataEntity=new MetadataEntity();
            metadataEntity.fieldName=field.getName();
            metadataEntity.fieldType=field.getType().getSimpleName();
            metadataEntity.objectValue=field.get(obj);
            metadataEntity.entityName=tableName;
            metadataEntity=simpleEntityDaoTemplate.translateDatabase(metadataEntity);
            NotCommitField notCommitField = field.getAnnotation(NotCommitField.class);
            if (notCommitField == null) {
                metadataEntity.isNotCommit="1";
            }else{
                metadataEntity.isNotCommit="0";
            }
            PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);
            if(primaryKey!=null){
                metadataEntity.isPK="1";
            }else {
                metadataEntity.isPK="0";
            }
            AutoIncrease annotation = field.getAnnotation(AutoIncrease.class);
            if(annotation!=null){
                metadataEntity.isAutoIncrease="1";
            }else {
                metadataEntity.isAutoIncrease="0";
            }

            metadataEntityList.add(metadataEntity);
        }
        return metadataEntityList;
    }

}
