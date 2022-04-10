package com.zg.mvc.entity;

import com.zg.bean.entity.MainModel;

/**
 * Created by Administrator on 2018/12/10 0010.
 */
public class MVCOption extends MainModel {
    public String controllerPackage;
    public String controllerSuffix;
    public String upLoadSuffix;
    public String upLoadPackage;
    public String upLoadPath;
    public String temporaryFilePath;
    public String projectRoot;

    public MVCOption clone() {
        MVCOption config = null;
        config = (MVCOption) super.clone();
        return config;
    }
}
