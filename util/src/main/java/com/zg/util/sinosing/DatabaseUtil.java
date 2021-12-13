package com.zg.util.sinosing;

import com.zg.bean.entity.OptionDB;

public  class DatabaseUtil {
    public static OptionDB getOptionDB(String type, String database) {
        OptionDB optionDB = new OptionDB();
        String userName = "";
        String ip = "";
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
        optionDB.url = "jdbc:oracle:thin:@" + ip + ":1521/" + instanceName;
        optionDB.username = userName;
        optionDB.password = "HvgaE#7ML_";
        optionDB.driver = "oracle.jdbc.driver.OracleDriver";
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
