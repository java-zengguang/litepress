package com.zg.litepress.core.bean.entity;

public class OptionDB extends MainModel {
    public String dataSourceName;
    public String databaseName = null;
    public String url = null;
    public int maxPoolSize = 5;
    public String driver = null;
    public String username = null;
    public String password = null;
    public String dbtype = null;
    public String dbptype = null;

    public String owner;

    public String resourcesURL;

    public String resourcesUser;

    public OptionDB() {
    }

    public OptionDB(String url, int maxPoolSize, String driver, String username, String password, String dbtype, String dbptype) {
        this.url = url;
        this.maxPoolSize = maxPoolSize;
        this.driver = driver;
        this.username = username;
        this.password = password;
        this.dbtype = dbtype;
        this.dbptype = dbptype;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDbtype() {
        return dbtype;
    }

    public void setDbtype(String dbtype) {
        this.dbtype = dbtype;
    }

    public String getDbptype() {
        return dbptype;
    }

    public void setDbptype(String dbptype) {
        this.dbptype = dbptype;
    }
}
