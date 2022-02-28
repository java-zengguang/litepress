package com.zg.sinosig;

import com.sinosig.saab.util.FileUtils;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.StepPluginType;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransHopMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.insertupdate.InsertUpdateMeta;
import org.pentaho.di.trans.steps.tableinput.TableInputMeta;

import java.io.File;

public class Test {

/*    *//**
     * 生成转换文件
     * @param args
     *//*
    public static void main(String[] args) {
        try {
            KettleEnvironment.init();
            Test test = new Test();
            TransMeta transMeta = test.generateMyOwnTrans();
            String transXml = transMeta.getXML();
            String transName = "etl/update_insert_Trans.ktr";
            File file = new File(transName);
            FileUtils.writeStringToFile(file, transXml, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }*/

    /**
     * 生成一个转化,把一个数据库中的数据转移到另一个数据库中,只有两个步骤,
     *      第一个是表输入,
     *      第二个是表插入与更新操作
     * @return
     * @throws KettleXMLException
     */
    public TransMeta generateMyOwnTrans() {
        TransMeta transMeta = new TransMeta();
        //设置转化的名称
        transMeta.setName("insert_update");
        //添加转换的数据库连接
        DatabaseMeta dataMeta = new DatabaseMeta("db1", "MySQL", "Native","localhost", "test", "3306", "root", "root");
        transMeta.addDatabase(dataMeta);
        DatabaseMeta targetMeta = new DatabaseMeta("db2", "MySQL", "Native","localhost", "test", "3306", "root", "root");
        transMeta.addDatabase(targetMeta);
        //registry是给每个步骤生成一个标识id
        PluginRegistry registry = PluginRegistry.getInstance();
        //第一个表输入步骤(TableInputMeta)
        TableInputMeta tableInput = new TableInputMeta();
        String tableInputPluginId = registry.getPluginId(StepPluginType.class, tableInput);
        //给表输入添加一个DatabaseMeta连接数据库
        DatabaseMeta database_bjdt = transMeta.findDatabase("db1");
        tableInput.setDatabaseMeta(database_bjdt);
        String selectSQL = "SELECT id,name FROM test1";
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
        String[] updatelookup = {"id","name"} ;
        String[] updateStream = {"id","name"} ;
        Boolean[] updateOrNot = {false,true};
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
        return transMeta;
    }


    /**
     * 上述操作将会产生一个ktr文件,接下来的操作是对ktr文件进行转换：
     */
    public static void main(String[] args) throws KettleException {
        //初始化ketlle
        KettleEnvironment.init();
        //创建转换元数据对象
        TransMeta meta = new TransMeta("etl/update_insert_Trans.ktr");
        Trans trans = new Trans(meta);
        trans.prepareExecution(null);
        trans.startThreads();
        trans.waitUntilFinished();
        if(trans.getErrors()==0){
            System.out.println("执行成功！");
        }
    }

}
