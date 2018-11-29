package com.vcat.common.quartz;

import com.google.common.collect.Maps;
import com.vcat.common.utils.StringUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * JMX_Quartz任务
 */
public class JMXJob implements Serializable{
    private String id;                      // 任务唯一ID
    private String schedulerName;           // 任务实例名称
    private String name;                    // 任务名称
    private String group;                   // 任务所属分组
    private String jobClass;                // 任务Class
    private String description;                    // 任务详情
    /**
     * 如果一个job是非持久的，当没有活跃的trigger与之关联的时候
     * 会被自动地从scheduler中删除。
     * 也就是说，非持久的job的生命期是由trigger的存在与否决定的；
     */
    private boolean durability;             // 是否持久化到数据库

    /**
     * 如果一个job是可恢复的，并且在其执行的时候
     * scheduler发生硬关闭（hard shutdown)（比如运行的进程崩溃了，或者关机了）
     * 则当scheduler重新启动的时候，该job会被重新执行。
     * 此时，该job的requestsRecovery() 返回true。
     */
    private boolean shouldRecover;          // 是否需要恢复
    private Map<String,String> jobDataMap;  // 任务参数
    private Date nextFireTime;              // 下次执行时间
    private int triggerCount;               // 所属触发器数量
    private String state;                   // 任务状态
    private JMXConnection jobConnection;    // 当前任务所属连接

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSchedulerName() {
        return schedulerName;
    }

    public void setSchedulerName(String schedulerName) {
        this.schedulerName = schedulerName;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getJobClass() {
        return jobClass;
    }

    public void setJobClass(String jobClass) {
        this.jobClass = jobClass;
    }

    public String getDescription() {
        if(StringUtils.isNotEmpty(description)){
            description = description.replaceAll("\\r\\n","</br>");
            description = description.replaceAll("\\n","</br>");
        }
        return description;
    }

    public void setDescription(String description) {
        if("null".equals(description)){
            description = "";
        }
        this.description = description;
    }

    public boolean getDurability() {
        return durability;
    }

    public void setDurability(boolean durability) {
        this.durability = durability;
    }

    public boolean getShouldRecover() {
        return shouldRecover;
    }

    public void setShouldRecover(boolean shouldRecover) {
        this.shouldRecover = shouldRecover;
    }

    public Map<String,String> getJobDataMap() {
        return jobDataMap;
    }

    public void setJobDataMap(Map<String,String> jobDataMap) {
        this.jobDataMap = jobDataMap;
    }

    public Date getNextFireTime() {
        return nextFireTime;
    }

    public void setNextFireTime(Date nextFireTime) {
        this.nextFireTime = nextFireTime;
    }

    public int getTriggerCount() {
        return triggerCount;
    }

    public void setTriggerCount(int triggerCount) {
        this.triggerCount = triggerCount;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public JMXConnection getJobConnection() {
        return jobConnection;
    }

    public void setJobConnection(JMXConnection jobConnection) {
        this.jobConnection = jobConnection;
    }

    /**
     * 将当前对象转换为Map
     * @return
     */
    public Map<String,Object> toMap(){
        Map<String,Object> result = Maps.newHashMap();

        result.put("name",name);
        result.put("group",group);
        if(StringUtils.isNotEmpty(description)){
            result.put("description",description);
        }
        result.put("jobClass",jobClass);
        result.put("jobDataMap",jobDataMap);
        result.put("durability",durability);
        result.put("requestsRecovery",shouldRecover);
        result.put("jobDetailClass","org.quartz.impl.JobDetailImpl");

        return result;
    }
}
