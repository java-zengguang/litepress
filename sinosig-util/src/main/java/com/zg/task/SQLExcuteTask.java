package com.zg.task;


import com.zg.sinosig.driver.AutoDriver;
import com.zg.sinosig.driver.SimpleAutoDriver;
import com.zg.sinosig.execute.ExecuteSQL;
import com.zg.sinosig.execute.SimpleExecute;
import com.zg.sinosig.introduce.IntroduceSQL;

import com.zg.sinosig.introduce.SimpleIntroduceSQL;
import com.zg.sinosig.load.LoadDataBaseStructure;
import com.zg.sinosig.load.SimpleLoadDataBaseStructure;
import com.zg.sinosig.result.SQLExecuteResult;
import com.zg.sinosig.result.SQLExecuteResultImpl;
import com.zg.webdemo.entity.SinoSigSQLLogEntity;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.*;

public class SQLExcuteTask implements Job {


    private String executeRoot="D:\\test\\SQLExecuteTask\\";

    private List<SinoSigSQLLogEntity> doExecute() throws Exception {

        ExecuteSQL executeSQL = new SimpleExecute();
        String executeBatchNo = "" + (new Date()).getTime();
        List<SinoSigSQLLogEntity> resultList=new ArrayList<>();
        // 执行
        if (true) {
            List<SinoSigSQLLogEntity> executeList = new ArrayList<>();
            IntroduceSQL introduceSQL = new SimpleIntroduceSQL(executeRoot, executeBatchNo);
            List<SinoSigSQLLogEntity> list = introduceSQL.initLoadSinoSigSQL();
            for(SinoSigSQLLogEntity sinoSigSQLLogEntity:list){
                SinoSigSQLLogEntity sigSQLLogEntity= (SinoSigSQLLogEntity) sinoSigSQLLogEntity.clone();
                executeList.add(sigSQLLogEntity);
            }
            AutoDriver autoDriver=new SimpleAutoDriver();
            list=autoDriver.doStart(list);
            resultList.addAll(list);

        }


        if(false) {
            //重新加载表结构
            try {
                LoadDataBaseStructure loadDataBaseStructure = new SimpleLoadDataBaseStructure();
                loadDataBaseStructure.reLoadStructure(resultList);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //执行结果同步svn
        if(true){
            SQLExecuteResult sqlExecuteResult=new SQLExecuteResultImpl();
            sqlExecuteResult.doResult(resultList);
        }

       return resultList;
    }






    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("-------脚本执行定时任务启动--------");
        try {
            doExecute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("-------脚本执行定时任务完成--------");
    }


    public static void main(String args[]) throws Exception {
        System.out.println("启动定时脚本自动执行程序");
        SQLExcuteTask sqlExcuteTask = new SQLExcuteTask();
        sqlExcuteTask.doExecute();
    }
}
