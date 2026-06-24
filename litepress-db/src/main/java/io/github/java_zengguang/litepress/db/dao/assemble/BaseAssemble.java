package io.github.java_zengguang.litepress.db.dao.assemble;

import io.github.java_zengguang.litepress.core.annotation.AutoIncrease;
import io.github.java_zengguang.litepress.core.annotation.NotCommitField;
import io.github.java_zengguang.litepress.core.annotation.PrimaryKey;
import io.github.java_zengguang.litepress.core.bean.entity.MetaColumnPo;
import io.github.java_zengguang.litepress.core.bean.entity.MetaDataPo;
import io.github.java_zengguang.litepress.db.dao.template.EntityDaoTemplate;
import io.github.java_zengguang.litepress.db.dao.template.EntityDaoTemplateFactory;
import io.github.java_zengguang.litepress.db.util.DBUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseAssemble<T> implements Assemble<T> {

    public String dbType;

    public BaseAssemble(String dbType) {
        this.dbType = dbType;
    }

    public T assembling(MetaDataPo metaData, T obj) throws IllegalAccessException {
        List<MetaColumnPo> metaColumnPos = metaData.columnPos;
        EntityDaoTemplate entityDaoTemplate = EntityDaoTemplateFactory.getTemplate(metaData.dbType);
        Class<?> classes = obj.getClass();
        Field[] fields = classes.getFields();
        for (Field field : fields) {
            String fileName = field.getName();
            MetaColumnPo metaColumnPo = metaColumnPos.stream().filter(x -> fileName.equalsIgnoreCase(x.columnLabel)).findFirst().orElse(null);
            if (metaColumnPo != null) {
                Object fieldValue = entityDaoTemplate.translateObject(field, metaColumnPo.jdbcValue);
                if (fieldValue != null) {
                    field.set(obj, fieldValue);
                }
            }
        }
        return obj;
    }

    public MetaDataPo analysis(T obj) throws IllegalAccessException, InstantiationException {
        List<MetaColumnPo> metadataEntityList = new ArrayList<>();
        Class<?> classes = obj.getClass();
        String tableName = DBUtils.getTableNameFromModel(classes);
        String entityName = classes.getSimpleName();
        Field[] fields = classes.getFields();
        EntityDaoTemplate simpleEntityDaoTemplate = EntityDaoTemplateFactory.getTemplate(dbType);

        for (Field field : fields) {
            MetaColumnPo metadataEntity = new MetaColumnPo();
            metadataEntity.fieldName = field.getName();
            metadataEntity.fieldType = field.getType().getSimpleName();
            metadataEntity.jdbcValue = field.get(obj);
            metadataEntity = simpleEntityDaoTemplate.translateDatabase(metadataEntity);
            NotCommitField notCommitField = field.getAnnotation(NotCommitField.class);
            if (notCommitField == null) {
                metadataEntity.isNotCommit = "1";
            } else {
                metadataEntity.isNotCommit = "0";
            }
            PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);
            if (primaryKey != null) {
                metadataEntity.isPK = "1";
            } else {
                metadataEntity.isPK = "0";
            }
            AutoIncrease annotation = field.getAnnotation(AutoIncrease.class);
            if (annotation != null) {
                metadataEntity.isAutoIncrease = "1";
            } else {
                metadataEntity.isAutoIncrease = "0";
            }

            metadataEntityList.add(metadataEntity);
        }
        MetaDataPo metaDataPo=new MetaDataPo();
        metaDataPo.columnPos=metadataEntityList;
        metaDataPo.tableName=tableName;
        metaDataPo.entityName=entityName;
        return metaDataPo;
    }

}
