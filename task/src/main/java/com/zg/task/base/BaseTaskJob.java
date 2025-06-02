package com.zg.task.base;

import com.alibaba.druid.support.logging.Log;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.tinylog.Logger;

public abstract class BaseTaskJob implements Job {

    public abstract void dealTask();

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        Logger.info("定时任务开始"+jobExecutionContext.getFireInstanceId());
        dealTask();
        Logger.info("定时任务结束"+jobExecutionContext.getFireInstanceId());
    }
}
