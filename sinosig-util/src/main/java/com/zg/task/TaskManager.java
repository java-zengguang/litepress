package com.zg.task;

import com.zg.bean.entity.JobTriggerEntity;
import com.zg.init.Config;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Date;

public class TaskManager {



    private void doMain() throws SchedulerException {

        //创建一个调度器，也就是一个Quartz容器
        //声明一个scheduler的工厂schedulerFactory
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        //通过schedulerFactory来实例化一个Scheduler
        Scheduler scheduler = schedulerFactory.getScheduler();

        String taskArrays[]={"CreateSVNDirTiming","SQLExcuteTaskTiming"};

        for(String taskName:taskArrays) {
            JobTriggerEntity jobTriggerEntity = (JobTriggerEntity) Config.getConfig(taskName);
            //创建一个JobDetail
            JobDetail jobDetail = null;
            try {
                jobDetail = JobBuilder.newJob((Class<? extends Job>) Class.forName(jobTriggerEntity.jobEntity.className))
                        .withDescription(jobTriggerEntity.jobEntity.description)
                        .withIdentity(jobTriggerEntity.jobEntity.name, jobTriggerEntity.jobEntity.group)
                        .build();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            //创建一个trigger触发规则
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withDescription(jobTriggerEntity.description)
                    .startAt(new Date())
                    .withIdentity(jobTriggerEntity.name, jobTriggerEntity.group)
                    .withSchedule(CronScheduleBuilder.cronSchedule(jobTriggerEntity.cron))
                    .build();

            //将Job和Trigger注册到scheduler容器中
            scheduler.scheduleJob(jobDetail, trigger);
        }

        scheduler.start();

    }

    public static void main(String args[]){
        TaskManager taskManager=new TaskManager();
        try {
            taskManager.doMain();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

}
