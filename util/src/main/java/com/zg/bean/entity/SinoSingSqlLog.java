package com.zg.bean.entity;

import com.zg.bean.annotation.AutoIncrease;

import java.util.Date;

public class SinoSingSqlLog extends MainModel{

    @AutoIncrease
    public String id;
    public String batchNo;
    public String serialno;
    public String planname;
    public String demandname;
    public String applyusername;
    public String environment;
    public String databasename;
    public String basesql;
    public String devsql;
    public String prosql;
    public Date executetime;
    public String executestate;
    public String errormassage;
    public String massage;
}
