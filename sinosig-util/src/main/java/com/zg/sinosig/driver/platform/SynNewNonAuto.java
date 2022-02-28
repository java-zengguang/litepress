package com.zg.sinosig.driver.platform;

import com.zg.sinosig.driver.SunAutoDriver;
import com.zg.sinosig.driver.execute.ExecuteSQL;
import com.zg.sinosig.driver.execute.SimpleExecute;
import com.zg.webdemo.entity.SinoSigSQLLogEntity;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.StepPluginType;
import org.pentaho.di.trans.TransHopMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.insertupdate.InsertUpdateMeta;
import org.pentaho.di.trans.steps.tableinput.TableInputMeta;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SynNewNonAuto implements SunAutoDriver {


    private boolean executeNewNonAuto(List<SinoSigSQLLogEntity> sinoSigSQLLogEntitys) throws Exception {

        List<SinoSigSQLLogEntity> list=new ArrayList<>();
        List<SinoSigSQLLogEntity> list1=new ArrayList<>();
        List<SinoSigSQLLogEntity> list2=new ArrayList<>();
        for(SinoSigSQLLogEntity sinoSigSQLLogEntity:sinoSigSQLLogEntitys){
            sinoSigSQLLogEntity.databasename="保单库";
            sinoSigSQLLogEntity.systemflag="new-non-auto";
            sinoSigSQLLogEntity.owner="nvpolicy";

            SinoSigSQLLogEntity entity= (SinoSigSQLLogEntity) sinoSigSQLLogEntity.clone();
            SinoSigSQLLogEntity entity1= (SinoSigSQLLogEntity) sinoSigSQLLogEntity.clone();
            SinoSigSQLLogEntity entity2= (SinoSigSQLLogEntity) sinoSigSQLLogEntity.clone();
            entity.environment="int";
            entity1.environment="uat";
            entity2.environment="stage";
            list.add(entity);
            list1.add(entity1);
            list2.add(entity2);
        }

        ExecuteSQL executeSQL=new SimpleExecute();
        try {
            executeSQL.excute(list);
        } catch (SQLException e) {
            e.printStackTrace();
           throw  new Exception("int执行错误");
        }
        try {
            executeSQL.excute(list1);
        } catch (SQLException e) {
            e.printStackTrace();
            throw  new Exception("uat执行错误");
        }
        try {
            executeSQL.excute(list2);
        } catch (SQLException e) {
            e.printStackTrace();
            throw  new Exception("stage执行错误");
        }
        return true;
    }


    @Override
    public List<SinoSigSQLLogEntity> doExecute(String systemFlag, List<SinoSigSQLLogEntity> list) {
        List<SinoSigSQLLogEntity> sinoSigSQLLogEntities=new ArrayList<>();
        for(SinoSigSQLLogEntity sinoSigSQLLogEntity:list){
            if("3".equals(sinoSigSQLLogEntity.executestate)){
                SinoSigSQLLogEntity entity= (SinoSigSQLLogEntity) sinoSigSQLLogEntity.clone();
                sinoSigSQLLogEntities.add(entity);
            }
        }
        if(sinoSigSQLLogEntities.size()>0) {
            try {
                executeNewNonAuto(sinoSigSQLLogEntities);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }
}
