package com.zg.util.sinosing;

import com.zg.bean.entity.OptionDB;

public  class DatabaseUtil {

    public static OptionDB getOptionDB(String type, String database) {
        OptionDB optionDB = new OptionDB();
        String userName = "";
        String ip = "";
        String password="HvgaE#7ML_";
        String instanceName = "";

        if ("dev".equals(type)) {
            ip = "10.7.129.85";
            if ("保单库".equals(database)) {
                instanceName = "nvpoldev";
                userName = "nvpolicy";
            }
            if ("产品工厂库".equals(database)) {
                instanceName = "nvpoldev";
                userName = "nvfactory";
            }
            if ("投保单库".equals(database)) {
                instanceName = "nvprodev";
                userName = "nvproposal";
            }
            if ("批单修改库".equals(database)) {
                instanceName = "nvprodev";
                userName = "nvendorsement";
            }
            if ("统一工作台库".equals(database)) {
                instanceName = "nvprodev";
                userName = "nvapimgmt";
            }
            if ("汇总库".equals(database)) {
                instanceName = "nvsundev";
                userName = "nvpolicy";
            }
        } else if ("int".equals(type)) {
            ip = "10.7.129.88";
            if ("保单库".equals(database)) {
                instanceName = "nvpolint";
                userName = "nvpolicy";
            }
            if ("产品工厂库".equals(database)) {
                instanceName = "nvpolint";
                userName = "nvfactory";
            }
            if ("投保单库".equals(database)) {
                instanceName = "nvproint";
                userName = "nvproposal";
            }
            if ("批单修改库".equals(database)) {
                instanceName = "nvproint";
                userName = "nvendorsement";
            }
            if ("统一工作台库".equals(database)) {
                instanceName = "nvproint";
                userName = "nvportal";
            }
            if ("汇总库".equals(database)) {
                instanceName = "nvsunint";
                userName = "nvpolicy";
            }
        } else if ("uat".equals(type)) {
            ip = "10.7.129.89";
            if ("保单库".equals(database)) {
                instanceName = "nvpoluat";
                userName = "nvpolicy";
            }
            if ("产品工厂库".equals(database)) {
                instanceName = "nvpoluat";
                userName = "nvfactory";
            }
            if ("投保单库".equals(database)) {
                instanceName = "nvprouat";
                userName = "nvproposal";
            }
            if ("批单修改库".equals(database)) {
                instanceName = "nvprouat";
                userName = "nvendorsement";
            }
            if ("统一工作台库".equals(database)) {
                instanceName = "nvprouat";
                userName = "nvportal";
            }
            if ("汇总库".equals(database)) {
                instanceName = "nvsunuat";
                userName = "nvpolicy";
            }
        } else if ("stage".equals(type)) {
            ip = "10.7.129.96";
            if ("保单库".equals(database)) {
                instanceName = "nvpolsta";
                userName = "nvpolicy";
            }
            if ("产品工厂库".equals(database)) {
                instanceName = "nvpolsta";
                userName = "nvfactory";
            }
            if ("投保单库".equals(database)) {
                instanceName = "nvprosta";
                userName = "nvproposal";
            }
            if ("批单修改库".equals(database)) {
                instanceName = "nvprosta";
                userName = "nvendorsement";
            }
            if ("统一工作台库".equals(database)) {
                instanceName = "nvprosta";
                userName = "nvportal";
            }
            if ("汇总库".equals(database)) {
                instanceName = "nvsunsta";
                userName = "nvpolicy";
            }
        }

        if("old_dev".equals(type)){
            if ("投保单库".equals(database)) {
                ip="10.8.199.28";
                instanceName = "pcoreuat";
                userName = "basecode";
                password="basecode";
            }
            if ("保单库".equals(database)) {
                ip="10.8.199.29";
                instanceName = "stagedb";
                userName = "sinosoft";
                password="sinosoft";
            }
        }

        optionDB.url = "jdbc:oracle:thin:@" + ip + ":1521/" + instanceName;
        optionDB.username = userName;
        optionDB.password = password;
        optionDB.driver = "oracle.jdbc.driver.OracleDriver";
        String target="";
        switch (database){
            case "保单库":{
                target="nvpolicy";
                break;
            }
            case "投保单库":{
                target="nvproposal";
                break;
            }
            case "批单修改库":{
                target="nvendorsement";
                break;
            }
            case "产品工厂库":{
                target="nvfactory";
                break;
            }
            case "统一工作台库":{
                target="nvportal";
                break;
            }

            case "汇总库":{
                target="nvsun";
                break;
            }

        }
        optionDB.dataSourceName=type+"_"+target;
        return optionDB;
    }

    public static OptionDB getConfigOptionDB(){
        OptionDB optionDB = new OptionDB();
        String userName = "basecode";
        String ip = "10.7.129.30";
        String instanceName = "platuat";
        optionDB.url = "jdbc:oracle:thin:@" + ip + ":1521/" + instanceName;
        optionDB.username = userName;
        optionDB.password = "basecode";
        optionDB.driver = "oracle.jdbc.driver.OracleDriver";
        return optionDB;
    }

}
