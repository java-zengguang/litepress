package com.zg.bean.entity;

public class OptionDB extends MainModel{
    public String dataSourceName;
    public String url = null;
    public int maxPoolSize = 5;
    public String driver = null;
    public String username = null;
    public String password = null;
    public String DBType=null;
    public String DBPType=null;

    public OptionDB() {
    }

    public OptionDB(String url, int maxPoolSize, String driver, String username, String password, String DBType, String DBPType) {
        this.url = url;
        this.maxPoolSize = maxPoolSize;
        this.driver = driver;
        this.username = username;
        this.password = password;
        this.DBType = DBType;
        this.DBPType = DBPType;
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

    public String getDBType() {
        return DBType;
    }

    public void setDBType(String DBType) {
        this.DBType = DBType;
    }

    public String getDBPType() {
        return DBPType;
    }

    public void setDBPType(String DBPType) {
        this.DBPType = DBPType;
    }
}
