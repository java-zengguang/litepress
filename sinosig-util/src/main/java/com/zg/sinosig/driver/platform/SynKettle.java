package com.zg.sinosig.driver.platform;

import com.zg.bean.entity.OptionDB;
import com.zg.init.Config;
import com.zg.sinosig.driver.SunAutoDriver;
import com.zg.webdemo.entity.SinoSigSQLLogEntity;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.StepPluginType;
import org.pentaho.di.trans.TransHopMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.insertupdate.InsertUpdateMeta;
import org.pentaho.di.trans.steps.tableinput.TableInputMeta;

import java.util.List;

public class SynKettle  implements SunAutoDriver {

    private boolean createKettleJob(SinoSigSQLLogEntity sourceEntity,SinoSigSQLLogEntity targetEntity){
        TransMeta transMeta = new TransMeta();

        //设置转化的名称
        transMeta.setName("insert_update");
        //添加转换的数据库连接
        DatabaseMeta dataMeta = new DatabaseMeta("dev_platform_platform", "ORACLE", "Native","10.7.129.30", "platuat", "1521", "basecode", "basecode");
        transMeta.addDatabase(dataMeta);
        DatabaseMeta targetMeta = new DatabaseMeta("dev_new-non-auto_nvpolicy", "MySQL", "Native","10.7.129.85", "nvpoldev", "1521", "nvpolicy", "HvgaE#7ML_");
        transMeta.addDatabase(targetMeta);
        //registry是给每个步骤生成一个标识id
        PluginRegistry registry = PluginRegistry.getInstance();

        if(true) {
            String tableName="";
            //第一个表输入步骤(TableInputMeta)
            TableInputMeta tableInput = new TableInputMeta();
            String tableInputPluginId = registry.getPluginId(StepPluginType.class, tableInput);
            //给表输入添加一个DatabaseMeta连接数据库
            DatabaseMeta database_bjdt = transMeta.findDatabase("dev_platform_platform");
            tableInput.setDatabaseMeta(database_bjdt);
            String selectSQL = "SELECT * FROM "+tableName;
            tableInput.setSQL(selectSQL);
            //添加TableInputMeta到转换中
            StepMeta tableInputMetaStep = new StepMeta(tableInputPluginId, "table input", tableInput);
            //给步骤添加在spoon工具中的显示位置
            tableInputMetaStep.setDraw(true);
            tableInputMetaStep.setLocation(100, 100);
            transMeta.addStep(tableInputMetaStep);

            //第二个步骤插入与更新

            InsertUpdateMeta insertUpdateMeta = new InsertUpdateMeta();
            String insertUpdateMetaPluginId = registry.getPluginId(StepPluginType.class, insertUpdateMeta);
            //添加数据库连接
            DatabaseMeta database_kettle = transMeta.findDatabase("db2");
            insertUpdateMeta.setDatabaseMeta(database_kettle);
            //设置操作的表
            insertUpdateMeta.setTableName("test2");
            //设置用来查询的关键字
            insertUpdateMeta.setKeyLookup(new String[]{"id"});
            insertUpdateMeta.setKeyStream(new String[]{"id"});
            insertUpdateMeta.setKeyStream2(new String[]{""});
            insertUpdateMeta.setKeyCondition(new String[]{"="});
            //设置要更新的字段
            String[] updatelookup = {"id", "name"};
            String[] updateStream = {"id", "name"};
            Boolean[] updateOrNot = {false, true};
            insertUpdateMeta.setUpdateLookup(updatelookup);
            insertUpdateMeta.setUpdateStream(updateStream);
            insertUpdateMeta.setUpdate(updateOrNot);
            //添加步骤到转换中
            StepMeta insertUpdateStep = new StepMeta(insertUpdateMetaPluginId, "insert_update", insertUpdateMeta);
            insertUpdateStep.setDraw(true);
            insertUpdateStep.setLocation(250, 100);
            transMeta.addStep(insertUpdateStep);
            //添加hop把两个步骤关联起来
            transMeta.addTransHop(new TransHopMeta(tableInputMetaStep, insertUpdateStep));
        }

        return true;
    }



    @Override
    public List<SinoSigSQLLogEntity> doExecute(String systemFlag, List<SinoSigSQLLogEntity> list) throws Exception {
        return null;
    }
}
