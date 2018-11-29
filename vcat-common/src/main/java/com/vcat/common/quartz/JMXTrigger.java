package com.vcat.common.quartz;

import org.quartz.SimpleTrigger;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * JMX_Quartz触发器
 */
public class JMXTrigger implements Serializable{
    private String calendarName;            // 日历名称
    private String desc;                    // 详情
    private Date endTime;                   // 结束时间
    private Date finalFireTime;             // 最终触发时间
    private String fireInstanceId;          // 触发实体名称
    private String group;                   // 所属触发器组
    private Map<String,Object> jobDataMap;          // 所属任务参数
    private String jobGroup;                // 所属任务分组
    private String jobName;                 // 所属任务名称
    /**
     * 详见<code>{@link SimpleTrigger}</code>
     */
    private int misfireInstruction;
    private String name;                    // 名称
    private Date nextFireTime;              // 下次触发时间
    private Date previousFireTime;          // 上次触发时间
    private int priority;                   // 优先级
    private Date startTime;                 // 触发器开始时间

    public String getCalendarName() {
        return calendarName;
    }

    public void setCalendarName(String calendarName) {
        this.calendarName = calendarName;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getFinalFireTime() {
        return finalFireTime;
    }

    public void setFinalFireTime(Date finalFireTime) {
        this.finalFireTime = finalFireTime;
    }

    public String getFireInstanceId() {
        return fireInstanceId;
    }

    public void setFireInstanceId(String fireInstanceId) {
        this.fireInstanceId = fireInstanceId;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Map<String,Object> getJobDataMap() {
        return jobDataMap;
    }

    public void setJobDataMap(Map<String,Object> jobDataMap) {
        this.jobDataMap = jobDataMap;
    }

    public String getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public int getMisfireInstruction() {
        return misfireInstruction;
    }

    public void setMisfireInstruction(int misfireInstruction) {
        this.misfireInstruction = misfireInstruction;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getNextFireTime() {
        return nextFireTime;
    }

    public void setNextFireTime(Date nextFireTime) {
        this.nextFireTime = nextFireTime;
    }

    public Date getPreviousFireTime() {
        return previousFireTime;
    }

    public void setPreviousFireTime(Date previousFireTime) {
        this.previousFireTime = previousFireTime;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
}
