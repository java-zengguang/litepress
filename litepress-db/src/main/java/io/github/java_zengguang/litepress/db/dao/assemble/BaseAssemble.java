package io.github.java_zengguang.litepress.db.dao.assemble;

import io.github.java_zengguang.litepress.core.annotation.AutoIncrease;
import io.github.java_zengguang.litepress.core.annotation.NotCommitField;
import io.github.java_zengguang.litepress.core.annotation.PrimaryKey;
import io.github.java_zengguang.litepress.db.dao.template.EntityDaoTemplate;
import io.github.java_zengguang.litepress.db.dao.template.EntityDaoTemplateFactory;
import io.github.java_zengguang.litepress.db.po.EntityDataPo;
import io.github.java_zengguang.litepress.db.po.EntityFieldPo;
import io.github.java_zengguang.litepress.db.po.MetaColumnPo;
import io.github.java_zengguang.litepress.db.po.MetaDataPo;
import io.github.java_zengguang.litepress.db.util.DBUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseAssemble<T> implements Assemble<T> {

    public String dbType;

    public BaseAssemble(String dbType) {
        this.dbType = dbType;
    }

    //直接利用元数据转换
    public T assembling(MetaDataPo metaData, T obj) throws IllegalAccessException {
        List<MetaColumnPo> metaColumnPos = metaData.columnPos;
        EntityDaoTemplate entityDaoTemplate = EntityDaoTemplateFactory.getTemplate(dbType);
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


    //读取实体类信息，用户后续转换sql语句
    public EntityDataPo analysis(T obj) throws IllegalAccessException {
        List<EntityFieldPo> entityFieldPos = new ArrayList<>();
        Class<?> classes = obj.getClass();
        String tableName = DBUtils.getTableNameFromModel(classes);
        String entityName = classes.getSimpleName();
        Field[] fields = classes.getFields();
        //    EntityDaoTemplate simpleEntityDaoTemplate = EntityDaoTemplateFactory.getTemplate(dbType);

        for (Field field : fields) {
            EntityFieldPo metadataEntity = new EntityFieldPo();
            metadataEntity.fieldName = field.getName();
            metadataEntity.fieldType = field.getType().getSimpleName();
            metadataEntity.fieldValue = field.get(obj);

            //  metadataEntity = simpleEntityDaoTemplate.translateDatabase(metadataEntity);
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

            entityFieldPos.add(metadataEntity);
        }
        EntityDataPo entityDataPo = new EntityDataPo();
        entityDataPo.entityFieldPos = entityFieldPos;
        entityDataPo.entityName = entityName;
        entityDataPo.tableName = tableName;
        return entityDataPo;
    }

}
