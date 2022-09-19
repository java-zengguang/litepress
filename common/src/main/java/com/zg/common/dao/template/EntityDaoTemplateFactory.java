package com.zg.common.dao.template;

public class EntityDaoTemplateFactory {
    public static EntityDaoTemplate getTemplate(String dbType){
        EntityDaoTemplate entityDaoTemplate=null;
        if("MYSQL".equals(dbType)){
            entityDaoTemplate=new MysqlEntityDaoTemplate();
        }
        if("ORACLE".equals(dbType)){
            entityDaoTemplate=new OracleEntityDaoTemplate();
        }

        return entityDaoTemplate;
    }
}
