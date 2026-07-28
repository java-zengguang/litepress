package io.github.java_zengguang.litepress.db.dao.template;

public class EntityDaoTemplateFactory {
    public static EntityDaoTemplate getTemplate(String dbType) {
        EntityDaoTemplate entityDaoTemplate = null;
        if ("MYSQL".equals(dbType)) {
            entityDaoTemplate = new MysqlEntityDaoTemplate();
        }
        if ("ORACLE".equals(dbType)) {
            entityDaoTemplate = new OracleEntityDaoTemplate();
        }
        if ("H2".equals(dbType)) {
            entityDaoTemplate = new H2EntityDaoTemplate();
        }
        if ("OB".equals(dbType)) {
            entityDaoTemplate = new OracleEntityDaoTemplate();
        }
        if ("OB-MYSQL".equals(dbType)) {
            entityDaoTemplate = new MysqlEntityDaoTemplate();
        }
        if ("OB-ORACLE".equals(dbType)) {
            entityDaoTemplate = new MysqlEntityDaoTemplate();
        }
        if ("SQLITE".equals(dbType)) {
            entityDaoTemplate = new SqliteEntityDaoTemplate();
        }
        return entityDaoTemplate;
    }
}
