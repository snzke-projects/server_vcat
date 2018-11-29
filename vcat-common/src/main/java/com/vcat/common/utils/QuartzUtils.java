package com.vcat.common.utils;

import com.vcat.common.spring.ApplicationContextHelper;
import org.apache.log4j.Logger;
import org.quartz.*;
import org.quartz.impl.StdScheduler;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * 定时任务工具类
 */
public class QuartzUtils {
	// JAVA任务触发器组
	private static final String TRIGGER_GROUP = "JAVA_TRIGGER_GROUP";
	// JAVA任务组
	private static final String JOB_GROUP = "JAVA_JOB_GROUP";
	// 任务调度类
	private static Scheduler scheduler;
	private static Logger LOG = Logger.getLogger(QuartzUtils.class);

	/**
	 * 获取任务调度类
	 * @return
	 */
	public static Scheduler getScheduler(){
		if(null == scheduler){
			scheduler = ((StdScheduler)ApplicationContextHelper.getBean("scheduler"));
			return scheduler;
		}
		return scheduler;
	}

	/**
	 * 设置新任务
	 * @param jobName 任务名称
	 * @param jobClass 任务类
	 * @param triggerTime 任务触发时间表达式
	 * @param jobData 任务相关参数
	 * @return
	 */
	public static JobKey pushJob(String jobName,Class<? extends Job> jobClass,String triggerTime,String desc,Map<String,? extends Object> jobData){
		JobKey key = JobKey.jobKey(jobName, JOB_GROUP);

		try {
			Scheduler scheduler = getScheduler();

			// 如果库中存在该任务则修改任务执行时间
			if(scheduler.checkExists(key)){
				updateJob(jobName,triggerTime);
				return key;
			}

            JobBuilder jobBuilder = JobBuilder.newJob(jobClass);

            // 任务Name
            jobBuilder.withIdentity(jobName, JOB_GROUP);

            // 任务备注
            jobBuilder.withDescription(desc);

			// 任务实体
			JobDetail jobDetail = jobBuilder.build();

			// 封装JOB参数
			jobDetail.getJobDataMap().putAll(jobData);

			// 触发器构建器
			CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(triggerTime);

			// 任务触发器
			Trigger trigger = TriggerBuilder.newTrigger().withIdentity(jobName,TRIGGER_GROUP).withSchedule(scheduleBuilder).build();

			// 将任务添加到调度器
			scheduler.scheduleJob(jobDetail, trigger);

			LOG.info("JAVA服务端添加定时任务[" + key + "][" + triggerTime + "]完成");

			if(!scheduler.isShutdown()){
				scheduler.start();
			}
			return key;
		} catch (Exception e) {
            LOG.error("JAVA服务端添加定时任务[" + key + "][" + triggerTime + "]失败：" + e.getMessage());
		}
		return null;
	}


	/**
	 * 获取指定时间的表达式
	 * @param date
	 * @return
	 */
	public static String getCronExpression(Date date){
		java.util.Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.SECOND) + " " + cal.get(Calendar.MINUTE) + " " + cal.get(Calendar.HOUR_OF_DAY) + " " + cal.get(Calendar.DAY_OF_MONTH) + " " + (cal.get(Calendar.MONTH) + 1) + " ? " + cal.get(Calendar.YEAR);
	}

	/**
	 * 更新定时任务触发时间
	 * @param jobName
	 * @param time
	 */
	public static boolean updateJob(String jobName, String time) {
		try {
			Scheduler scheduler = getScheduler();
			CronTrigger trigger = (CronTrigger) scheduler.getTrigger(TriggerKey.triggerKey(jobName,TRIGGER_GROUP));
			if (trigger == null) {
                LOG.info("JAVA服务端修改定时任务[" + jobName + "][" + time + "]失败：未找到相应Trigger");
				return false;
			}
			String oldTime = trigger.getCronExpression();
			if (!oldTime.equalsIgnoreCase(time)) {
				TriggerKey triggerKey = TriggerKey.triggerKey(jobName, TRIGGER_GROUP);

                //表达式调度构建器
				CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(time);

                //按新的cronExpression表达式重新构建trigger
				trigger = trigger.getTriggerBuilder().withIdentity(triggerKey)
						.withSchedule(scheduleBuilder).build();

                //按新的trigger重新设置job执行
				scheduler.rescheduleJob(triggerKey, trigger);
				if(!scheduler.isShutdown()){
					scheduler.start();
				}
				LOG.info("JAVA服务端修改定时任务[" + jobName + "][" + time + "]完成");
			}
            return true;
		} catch (Exception e) {
            LOG.error("JAVA服务端修改定时任务[" + jobName + "][" + time + "]失败：" + e.getMessage());
		}
        return false;
	}

	/**
	 * 删除定时任务
	 * @param jobName
	 * @return
	 */
	public static boolean deleteJob(String jobName){
        JobKey key = JobKey.jobKey(jobName, JOB_GROUP);
        return deleteJob(key);
	}

    /**
     * 删除定时任务
     * @param key
     * @return
     */
    public static boolean deleteJob(JobKey key){
        try {
            Scheduler scheduler = getScheduler();

            // 停止触发器
            scheduler.pauseTrigger(TriggerKey.triggerKey(key.getName(), TRIGGER_GROUP));
            // 移除触发器
            scheduler.unscheduleJob(TriggerKey.triggerKey(key.getName(), TRIGGER_GROUP));
            // 删除任务
            scheduler.deleteJob(key);

            LOG.info("JAVA服务端删除定时任务[" + key.getName() + "]完成");
            return true;
        } catch (SchedulerException e) {
            LOG.error("JAVA服务端删除定时任务[" + key.getName() + "]失败：" + e.getMessage());
        }
        return false;
    }

    /**
     * 立即执行任务
     * @param key
     * @throws SchedulerException
     */
    public static void invoke(JobKey key){
        try {
            scheduler.triggerJob(key);
            LOG.info("JAVA服务端执行定时任务[" + key.getName() + "]完成");
        } catch (SchedulerException e) {
            e.printStackTrace();
            LOG.error("JAVA服务端执行任务["+key.getName()+"]失败：" + e.getMessage());
        }
    }

    /**
     * 立即执行任务
     * @param key
     * @param jobData
     * @throws SchedulerException
     */
    public static void invoke(JobKey key,Map<String,Object> jobData){
        try {
            scheduler.triggerJob(key,new JobDataMap(jobData));
            LOG.info("JAVA服务端执行定时任务[" + key.getName() + "]完成");
        } catch (SchedulerException e) {
            e.printStackTrace();
            LOG.error("JAVA服务端执行任务["+key.getName()+"]失败：" + e.getMessage());
        }
    }

    /**
     * 暂停任务
     * @param key
     * @throws SchedulerException
     */
    public static void pause(JobKey key){
        try {
            scheduler.pauseJob(key);
            LOG.info("JAVA服务端暂停定时任务[" + key.getName() + "]完成");
        } catch (SchedulerException e) {
            e.printStackTrace();
            LOG.error("JAVA服务端暂停任务["+key.getName()+"]失败：" + e.getMessage());
        }
    }

    /**
     * 恢复任务
     * @param key
     * @throws SchedulerException
     */
    public static void resume(JobKey key){
        try {
            scheduler.resumeJob(key);
            LOG.info("JAVA服务端恢复定时任务[" + key.getName() + "]完成");
        } catch (SchedulerException e) {
            e.printStackTrace();
            LOG.error("JAVA服务端恢复任务["+key.getName()+"]失败：" + e.getMessage());
        }
    }
}