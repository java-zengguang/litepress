package com.zg.task;


import com.zg.sinosig.check.CheckSQL;
import com.zg.sinosig.check.SimpleCheckSQL;
import com.zg.sinosig.execute.ExecuteSQL;
import com.zg.sinosig.execute.SimpleExecute;
import com.zg.sinosig.generate.GenerateSQL;
import com.zg.sinosig.generate.SVNSimpleGeneraterSQL;

import com.zg.sinosig.load.LoadDataBaseStructure;
import com.zg.sinosig.load.SimpleLoadDataBaseStructure;
import com.zg.sinosig.result.SQLExecuteResult;
import com.zg.sinosig.result.SQLExecuteResultImpl;
import com.zg.util.reflect.FieldUtils;
import com.zg.util.reflect.SerializeUtils;
import com.zg.webdemo.entity.SinoSigSQLLogEntity;
import com.zg.webdemo.service.sinosigsqllog.SinoSigSQLLogService;

import java.util.*;

public class SQLExcuteTask {


    private String executeRoot="D:\\test\\SQLExecuteTask\\";

    private List<SinoSigSQLLogEntity> doExecute() throws Exception {
        CheckSQL checkSQL = new SimpleCheckSQL();
        ExecuteSQL executeSQL = new SimpleExecute();
        String executeBatchNo = "" + (new Date()).getTime();
        List<SinoSigSQLLogEntity> resultList=new ArrayList<>();
        //uat 执行
        if (true) {
            List<SinoSigSQLLogEntity> executeList = new ArrayList<>();
            GenerateSQL generateSQL = new SVNSimpleGeneraterSQL(executeRoot, executeBatchNo);
            List<SinoSigSQLLogEntity> list = generateSQL.initLoadSinoSingSQL();
            for(SinoSigSQLLogEntity sinoSigSQLLogEntity:list){
                SinoSigSQLLogEntity sigSQLLogEntity= (SinoSigSQLLogEntity) sinoSigSQLLogEntity.clone();
                sigSQLLogEntity.environment="uat";
                executeList.add(sigSQLLogEntity);
            }

            executeList = checkSQL.checkSQL(executeList);
            executeList= executeSQL.excute(executeList);
            resultList.addAll(executeList);

        }

        //stage 执行
        if (true) {
            List<SinoSigSQLLogEntity> executeList = new ArrayList<>();
            GenerateSQL generateSQL = new SVNSimpleGeneraterSQL(executeRoot, executeBatchNo);
            List<SinoSigSQLLogEntity> list = generateSQL.initLoadSinoSingSQL();
            for(SinoSigSQLLogEntity sinoSigSQLLogEntity:list){
                SinoSigSQLLogEntity sigSQLLogEntity= (SinoSigSQLLogEntity) sinoSigSQLLogEntity.clone();
                sigSQLLogEntity.environment="stage";
                executeList.add(sigSQLLogEntity);
            }

            executeList = checkSQL.checkSQL(executeList);
            executeList= executeSQL.excute(executeList);
            resultList.addAll(executeList);
        }

        if(true) {
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





    public boolean doStart() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 17); // 控制时
        calendar.set(Calendar.MINUTE, 40);       // 控制分
        calendar.set(Calendar.SECOND, 0);       // 控制秒

        Date time = calendar.getTime();         // 得出执行任务的时间,此处为今天的17：30：00

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                System.out.println("-------脚本执行定时任务启动--------");
                try {
                    doExecute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("-------脚本执行定时任务完成--------");
            }
        }, time, 1000 * 60 * 60 * 24);// 这里设定将延时每天固定执行

        return true;
    }

    public static void main(String args[]) {
        System.out.println("启动定时脚本自动执行程序");
        SQLExcuteTask sqlExcuteTask = new SQLExcuteTask();
        sqlExcuteTask.doStart();
    }
}
