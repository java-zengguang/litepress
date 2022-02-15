package com.zg.webdemo.entity;

import com.zg.bean.annotation.AutoIncrease;
import com.zg.bean.annotation.FieldTypeMode;
import com.zg.bean.annotation.Model;
import com.zg.bean.annotation.NotCommitField;
import com.zg.bean.entity.MainModel;



@Model(tableName = "sinosigsqllog")
@FieldTypeMode(typeMode = "entity")
public class SinoSigSQLLogEntity extends MainModel {
    @AutoIncrease
    @NotCommitField
    public Integer id;
    public String batchno;
    public String serialno;
    public String planname;
    public String demandid;
    public String demandname;
    public String applyusername;
    public String environment;
    public String databasename;
    @NotCommitField
    public String owner;
    public String isconfig;
    public String sqltype;
    public String basesql;
    public String devsql;
    public String prosql;
    @NotCommitField
    public String executetime;
    //0-初始化  1-流程完成  3-待下一节点执行  -1-处理失败
    public String executestate;
    public String errormassage;
    public String sqlpurpose;
    public String sqldescribe;
    public String needpowertables;
    public String executebatchno;  //执行批次号
    public String systemflag; //系统标识  new-non-auto 非车新一代   old-non-auto  老非车  platform  平台
    @NotCommitField
    public String[] needPowerTableArray;

    public String getSqltype() {
        return sqltype.toUpperCase();
    }

    public void setSqltype(String sqltype) {
        this.sqltype = sqltype;
    }

    public String getSystemflag() {
        return systemflag;
    }

    public void setSystemflag(String systemflag) {
        this.systemflag = systemflag;
    }

    public String getBatchno() {
        return batchno;
    }

    public void setBatchno(String batchno) {
        this.batchno = batchno;
    }

    public String getSerialno() {
        return serialno;
    }

    public void setSerialno(String serialno) {
        this.serialno = serialno;
    }

    public String getPlanname() {
        return planname;
    }

    public void setPlanname(String planname) {
        this.planname = planname;
    }

    public String getDemandname() {
        return demandname;
    }

    public void setDemandname(String demandname) {
        this.demandname = demandname;
    }

    public String getApplyusername() {
        return applyusername;
    }

    public void setApplyusername(String applyusername) {
        this.applyusername = applyusername;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getDatabasename() {
        return databasename;
    }

    public void setDatabasename(String databasename) {
        this.databasename = databasename;
    }

    public String getBasesql() {
        return basesql;
    }

    public void setBasesql(String basesql) {
        this.basesql = basesql;
    }

    public String getDevsql() {
        return devsql;
    }

    public void setDevsql(String devsql) {
        this.devsql = devsql;
    }

    public String getProsql() {
        return prosql;
    }

    public void setProsql(String prosql) {
        this.prosql = prosql;
    }

    public String getExecutetime() {
        return executetime;
    }

    public void setExecutetime(String executetime) {
        this.executetime = executetime;
    }

    public String getExecutestate() {
        return executestate;
    }

    public void setExecutestate(String executestate) {
        this.executestate = executestate;
    }

    public String getErrormassage() {
        return errormassage;
    }

    public void setErrormassage(String errormassage) {
        if (this.errormassage==null){
            this.errormassage="";
        }
        this.errormassage = this.errormassage+";"+errormassage;
    }

    public String getDataSource() {
        String database=databasename;

/*        switch (database){
            case "保单库":{
                database="nvpolicy";
                break;
            }
            case "投保单库":{
                database="nvproposal";
                break;
            }
            case "批单修改库":{
                database="nvendorsement";
                break;
            }
            case "产品工厂库":{
                database="nvfactory";
                break;
            }
            case "统一工作台库":{
                database="nvportal";
                break;
            }

            case "汇总库":{
                database="nvsun";
                break;
            }

            case "平台库":{
                database="platform";
                break;
            }

        }*/
        database=environment+"_"+systemflag+"_"+owner;

        return database;
    }



}













