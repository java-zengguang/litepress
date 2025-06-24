package io.github.java_zengguang.litepress.task;

import io.github.java_zengguang.litepress.core.init.Config;
import io.github.java_zengguang.litepress.task.entity.JobTriggerEntity;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.tinylog.Logger;


import java.text.ParseException;
import java.text.SimpleDateFormat;

public class TaskManager {

   private String taskArrays[];

    public TaskManager(String[] taskArrays) {
        this.taskArrays = taskArrays;
    }




    public void doMain() throws SchedulerException, ParseException {

        //创建一个调度器，也就是一个Quartz容器
        //声明一个scheduler的工厂schedulerFactory
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        //通过schedulerFactory来实例化一个Scheduler
        Scheduler scheduler = schedulerFactory.getScheduler();


        for (String taskName : taskArrays) {
            JobTriggerEntity jobTriggerEntity = (JobTriggerEntity) Config.getConfig(taskName);
            if (jobTriggerEntity == null) {
                continue;
            }
            Logger.info("开始创建job"+jobTriggerEntity.name);
            //创建一个JobDetail
            JobDetail jobDetail = null;
            try {
                jobDetail = JobBuilder
                        .newJob((Class<? extends Job>) Class.forName(jobTriggerEntity.className))
                        .withDescription(jobTriggerEntity.description)
                        .withIdentity(jobTriggerEntity.name, jobTriggerEntity.group)
                        .build();
            } catch (ClassNotFoundException e) {
                Logger.error(e);
            }
            Logger.info("开始创建触发规则"+jobTriggerEntity.name);
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
            //创建一个trigger触发规则
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withDescription(jobTriggerEntity.description)
                    .startAt(sdf.parse(jobTriggerEntity.startAt))
                    .withIdentity(jobTriggerEntity.name, jobTriggerEntity.group)
                    .withSchedule(CronScheduleBuilder.cronSchedule(jobTriggerEntity.cron))
                    .build();

            //将Job和Trigger注册到scheduler容器中
            scheduler.scheduleJob(jobDetail, trigger);

        }

        scheduler.start();

    }

}
