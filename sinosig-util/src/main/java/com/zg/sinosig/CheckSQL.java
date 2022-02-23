package com.zg.sinosig;

import com.zg.sinosig.driver.AutoDriver;
import com.zg.sinosig.driver.CheckAutoDriver;
import com.zg.sinosig.introduce.IntroduceSQL;
import com.zg.sinosig.introduce.SimpleIntroduceSQL;
import com.zg.sinosig.report.ReportDataBase;
import com.zg.sinosig.report.SimpleReportBataBase;
import com.zg.util.io.FileUtils;
import com.zg.webdemo.entity.SinoSigSQLLogEntity;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CheckSQL implements Job {


    private String executeRoot= FileUtils.PATH;

    private List<SinoSigSQLLogEntity> doExecute() throws Exception {


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
            AutoDriver autoDriver=new CheckAutoDriver();
            list=autoDriver.doStart(list);
            resultList.addAll(list);

        }



        //返回执行结果
        if(true){
            ReportDataBase sqlExecuteResult=new SimpleReportBataBase(executeRoot);

            sqlExecuteResult.doResult(resultList);
        }

       return resultList;
    }






    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("------启动脚本校验程序--------");
        try {
            doExecute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("-------脚本校验程序完成--------");
    }


    public static void main(String args[]) throws Exception {
        System.out.println("程序启动");
        CheckSQL sqlExcuteTask = new CheckSQL();
        sqlExcuteTask.doExecute();
    }
}
