package com.vcat.common.quartz;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.vcat.common.persistence.Page;
import com.vcat.common.utils.IdGen;
import com.vcat.common.utils.QuartzUtils;
import com.vcat.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.openmbean.*;
import javax.management.remote.*;
import java.net.MalformedURLException;
import java.util.*;

/**
 * 通过JMX方式修改定时任务
 */
public class JMXQuartzService {
    private static JMXConnection jmxConnection;         // 当前连接属性
    private static MBeanServerConnection connection;    // 当前连接对象
    private static Set<ObjectName> objectNameSet;       // 对象名
    private static Map<String,ObjectName> objectNameMap = Maps.newLinkedHashMap();  // SchedulerObjectNameMap
    private static List<JMXJob> jobList = Lists.newArrayList();     // 当前连接所有任务List
    private static Set<JMXTrigger> triggerSet = Sets.newHashSet();  // 当前连接所有触发器
    private static Map<String,JMXJob> jobClassMap = Maps.newLinkedHashMap();        // 当前连接所有任务类Map
    private static Logger LOG = (Logger) LoggerFactory.getLogger(JMXQuartzService.class);

    public static void reconnect(){
        reconnect(null);
    }

    /**
     * 刷新连接实体
     */
    public static void reconnect(JMXConnection conn){
        clearConnection();
        if(conn != null){
            jmxConnection = conn;
        }else if(jmxConnection == null){
            jmxConnection = JMXConnection.getDefault();
        }
        connect();
    }

    /**
     * 创建JMX连接
     */
    private static void connect(){
        try {
            Map<String, String[]> env = Maps.newHashMap();
            env.put(JMXConnector.CREDENTIALS, new String[]{jmxConnection.getUsername(), jmxConnection.getPassword()});
            JMXServiceURL jmxServiceURL = createQuartzInstanceConnection();
            JMXConnector connector = JMXConnectorFactory.connect(jmxServiceURL, env);
            connection = connector.getMBeanServerConnection();
            refreshJob();
        } catch (Exception e) {
            LOG.error("初始化JMX连接失败：" + e.getMessage());
            jmxConnection = null;
            throw new RuntimeException("初始化JMX连接失败：" + e.getMessage());
        }
    }

    /**
     * 刷新当前连接实体
     */
    private static void updateObjectNameSet(){
        try {
            ObjectName mBName = new ObjectName("quartz:type=QuartzScheduler,*");
            objectNameSet = connection.queryNames(mBName, null);
        } catch (Exception e) {
            LOG.error("刷新连接实体失败：" + e.getMessage());
            throw new RuntimeException("刷新连接实体失败：" + e.getMessage());
        }
    }

    /**
     * 刷新连接中的任务数据
     * @throws Exception
     */
    private static void refreshJob(){
        updateObjectNameSet();
        try {
            for (ObjectName objectName : objectNameSet){
                String schedulerName= connection.getAttribute(objectName, "SchedulerName") + "";
                objectNameMap.put(schedulerName,objectName);
                jobList.addAll(getJobListBySchedulerName(schedulerName));
            }
        } catch (Exception e) {
            LOG.error("刷新任务数据失败：" + e.getMessage());
            throw new RuntimeException("刷新任务数据失败：" + e.getMessage());
        }
    }

    /**
     * 根据IP和端口配置创建JMX连接
     * @return
     * @throws MalformedURLException
     */
    private static JMXServiceURL createQuartzInstanceConnection() throws MalformedURLException {
        JMXServiceURL jmxServiceURL = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://"+ jmxConnection.getHost() +":"+ jmxConnection.getPort() +"/jmxrmi");
        return jmxServiceURL;
    }

    /**
     * 获取JMX属性
     * @param schedulerName
     * @param attributeName
     * @return
     */
    public static Object getAttributeData(String schedulerName,String attributeName){
        try {
            return connection.getAttribute(objectNameMap.get(schedulerName), attributeName);
        } catch (Exception e) {
            LOG.error("获取Scheduler[" + schedulerName + "]属性[" + attributeName + "]失败：" + e.getMessage());
            throw new RuntimeException("获取Scheduler[" + schedulerName + "]属性[" + attributeName + "]失败：" + e.getMessage());
        }
    }

    /**
     * 获取任务集合
     * @param schedulerName
     * @return
     */
    public static List<JMXJob> getJobListBySchedulerName(String schedulerName){
        List<JMXJob> list = Lists.newArrayList();

        TabularDataSupport tabularDataSupport = (TabularDataSupport)getAttributeData(schedulerName,"AllJobDetails");

        Iterator<Object> iterator = tabularDataSupport.values().iterator();

        while (iterator.hasNext()){
            Object valueObject = iterator.next();
            CompositeDataSupport data = null;
            if(valueObject instanceof CompositeDataSupport){
                data = (CompositeDataSupport)valueObject;
            }

            list.add(getJobByData(schedulerName,data));
        }
        return list;
    }

    /**
     * 获取任务对象
     * @param schedulerName
     * @param name
     * @param group
     * @return
     */
    private static JMXJob getJob(String schedulerName,String name,String group){
        JMXJob job = new JMXJob();

        CompositeDataSupport data;
        try {
            data = (CompositeDataSupport)connection.invoke(objectNameMap.get(schedulerName),"getJobDetail",new Object[]{name,group},new String[]{String.class.getName(),String.class.getName()});
            if(null != data){
                job = getJobByData(schedulerName,data);
            }
        } catch (Exception e) {
            LOG.error("获取Scheduler[" + schedulerName + "]任务[" + name + "]失败：" + e.getMessage());
            throw new RuntimeException("获取Scheduler[" + schedulerName + "]任务[" + name + "]失败：" + e.getMessage());
        }

        return job;
    }

    /**
     * 根据JMX数据获取任务对象
     * @param schedulerName
     * @param compositeDataSupport
     * @return
     */
    private static JMXJob getJobByData(String schedulerName,CompositeDataSupport compositeDataSupport){
        JMXJob job = new JMXJob();

        job.setId(IdGen.uuid());
        job.setSchedulerName(schedulerName);
        job.setName(compositeDataSupport.get("name") + "");
        job.setGroup(compositeDataSupport.get("group") + "");
        job.setDescription(compositeDataSupport.get("description") + "");
        job.setDurability(Boolean.parseBoolean(compositeDataSupport.get("durability") + ""));
        job.setShouldRecover(Boolean.parseBoolean(compositeDataSupport.get("shouldRecover") + ""));
        job.setJobClass(compositeDataSupport.get("jobClass") + "");

        List<JMXTrigger> triggerList = getTriggerForJob(schedulerName,job.getName(),job.getGroup());

        if(triggerList.size() > 0){
            job.setState(getTriggerState(schedulerName,triggerList.get(0).getName(),triggerList.get(0).getGroup()));
        }
        job.setNextFireTime(getFirstTriggerFireTime(triggerList));
        job.setTriggerCount(triggerList.size());

        Map<String,String> jobDataMap = toMap((TabularDataSupport) compositeDataSupport.get("jobDataMap"));

        job.setJobDataMap(jobDataMap);

        job.setJobConnection(jmxConnection);

        if(!jobClassMap.containsKey(job.getJobClass())){
            jobClassMap.put(job.getJobClass(),job);
        }

        return job;
    }

    /**
     * 将JobDataMap[TabularDataSupport]转换为Map<String, String>
     * @param dataSupport
     * @return
     */
    private static Map<String, String> toMap(TabularDataSupport dataSupport) {
        Map<String,String> result = Maps.newTreeMap();
        Set keySet = dataSupport.keySet();
        Iterator iterator = keySet.iterator();
        while (iterator.hasNext()){
            Collection key = (Collection)iterator.next();
            Object value = dataSupport.get(key.toArray());
            CompositeDataSupport compositeDataSupport = null;
            if(value instanceof CompositeDataSupport){
                compositeDataSupport = (CompositeDataSupport)value;
            }
            Collection c = compositeDataSupport.values();
            if(null != c && c.size() > 0){
                Iterator i = c.iterator();
                i.next();
                result.put(key.toArray()[0].toString(), i.next().toString());
            }
        }

        return result;
    }

    /**
     * 获取任务所属触发器集合
     * @param schedulerName
     * @param jobName
     * @param group
     * @return
     */
    private static List<JMXTrigger> getTriggerForJob(String schedulerName,String jobName,String group){
        List<JMXTrigger> list = Lists.newArrayList();

        try {
            List<CompositeDataSupport> dataSupportList = (List<CompositeDataSupport>) connection.invoke(objectNameMap.get(schedulerName), "getTriggersOfJob", new Object[]{jobName, group}, new String[]{String.class.getName(),String.class.getName()});

            if(null != dataSupportList && !dataSupportList.isEmpty()){
                for (CompositeDataSupport data : dataSupportList){
                    JMXTrigger trigger = new JMXTrigger();
                    trigger.setCalendarName(data.get("calendarName") + "");
                    trigger.setDesc(data.get("description") + "");
                    trigger.setEndTime((Date) data.get("endTime"));
                    trigger.setFinalFireTime((Date) data.get("finalFireTime"));
                    trigger.setFireInstanceId(data.get("fireInstanceId") + "");
                    trigger.setGroup(data.get("group") + "");
                    trigger.setJobDataMap((Map) data.get("jobDataMap"));
                    trigger.setJobGroup(data.get("jobGroup") + "");
                    trigger.setJobName(data.get("jobName") + "");
                    trigger.setMisfireInstruction((Integer) data.get("misfireInstruction"));
                    trigger.setName(data.get("name") + "");
                    trigger.setNextFireTime((Date) data.get("nextFireTime"));
                    trigger.setPreviousFireTime((Date) data.get("previousFireTime"));
                    trigger.setPriority((Integer) data.get("priority"));
                    trigger.setStartTime((Date) data.get("startTime"));
                    list.add(trigger);
                    if(!triggerSet.contains(trigger)){
                        triggerSet.add(trigger);
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("获取scheduler[" + schedulerName + "]JMXTrigger[" + jobName + " , " + group + "]失败：" + e.getMessage());
            throw new RuntimeException("获取scheduler[" + schedulerName + "]JMXTrigger[" + jobName + " , " + group + "]失败：" + e.getMessage());
        }

        return list;
    }

    /**
     * 获取触发器状态
     * @param schedulerName
     * @param triggerName
     * @param triggerGroup
     * @return
     */
    private static String getTriggerState(String schedulerName,String triggerName,String triggerGroup){
        String state = "NONE";
        try {
            Object stateResult = connection.invoke(objectNameMap.get(schedulerName), "getTriggerState", new Object[]{triggerName, triggerGroup}, new String[]{String.class.getName(),String.class.getName()});
            if(stateResult != null){
                state = stateResult.toString();
            }
        } catch (Exception e) {
            LOG.error("获取scheduler[" + schedulerName + "]JMXTriggerState[" + triggerName + " , " + triggerGroup + "]失败：" + e.getMessage());
            throw new RuntimeException("获取scheduler[" + schedulerName + "]JMXTriggerState[" + triggerName + " , " + triggerGroup + "]失败：" + e.getMessage());
        }
        return state;
    }

    /**
     * 获取触发器集合中第一次触发时间
     * @param list
     * @return
     */
    private static Date getFirstTriggerFireTime(List<JMXTrigger> list){
        Date nextDate = null;

        if(list.size() > 0){
            for (JMXTrigger trigger : list){
                Date tempFireTime = trigger.getNextFireTime();
                if(null == tempFireTime){
                    continue;
                }
                if(null == nextDate){
                    nextDate = tempFireTime;
                }
                if(tempFireTime.before(nextDate)){
                    nextDate = tempFireTime;
                }
            }
        }

        return nextDate;
    }

    /**
     * 获取所有任务
     * @return
     */
    public static List<JMXJob> getAllJob(JMXConnection jmxConnection){
        if(jmxConnection.getIsModify() && !jmxConnection.isEmpty()){
            reconnect(jmxConnection);
        }
        if(null == connection){
            connect();
        }
        return jobList;
    }

    /**
     * 获取所有任务
     * @return
     */
    public static Page<JMXJob> findAll(Page<JMXJob> page,JMXConnection jmxConnection){
        List<JMXJob> list = JMXQuartzService.getAllJob(jmxConnection);

        page.setCount(list.size());

        List<JMXJob> currentPage = Lists.newArrayList();

        int currentSize = 0;
        int pageSize = page.getPageSize();
        int index = pageSize * (page.getPageNo() - 1);
        if(index < list.size()){
            for (int i = index; i < list.size(); i++) {
                if(currentSize == pageSize){
                    break;
                }
                currentPage.add(list.get(i));
                currentSize++;
            }
        }

        page.setList(currentPage);
        return page;
    }

    /**
     * 获取立即执行的任务触发器
     * @param name
     * @param group
     * @param jobData
     * @return
     */
    private static Map<String,Object> toTriggerMap(String name,String group,Map<String,String> jobData,Date fireTime){
        Map<String,Object> result = Maps.newHashMap();

        if(null == jobData && jobData.isEmpty()){
            return result;
        }

        String triggerName = "JMX_T_" + name;
        triggerName = StringUtils.abbr(triggerName,200);
        String triggerGroup = "JMX_T_GROUP";
        result.put("name",triggerName);
        result.put("group", triggerGroup);
        result.put("description","JMX后台立即执行任务["+name+"]");
        result.put("jobName", name);
        result.put("jobGroup", group);
        result.put("jobDataMap", jobData);
        if(null != fireTime){
            result.put("cronExpression", QuartzUtils.getCronExpression(fireTime));
            result.put("triggerClass", "org.quartz.impl.triggers.CronTriggerImpl");
        }else{
            result.put("triggerClass", "org.quartz.impl.triggers.SimpleTriggerImpl");
        }

        return result;
    }

    /**
     * 立即执行任务
     * @param schedulerName
     * @param name
     * @param group
     * @param jobData
     */
    public static void invoke(String schedulerName,String name,String group,Map<String,String> jobData){
        try {
            connection.invoke(objectNameMap.get(schedulerName), "scheduleJob", new Object[]{name, group, toTriggerMap(name, group, jobData,null)}, new String[]{String.class.getName(), String.class.getName(), Map.class.getName()});
            LOG.error("JMX立即执行scheduler[" + schedulerName + "]JMXJob[" + name + " , " + group + "]jobData[" + jobData + "]成功");
        } catch (Exception e) {
            LOG.error("JMX立即执行scheduler[" + schedulerName + "]JMXJob[" + name + " , " + group + "]jobData[" + jobData + "]失败：" + e.getMessage());
            throw new RuntimeException("执行任务失败：" + e.getMessage());
        }
    }

    /**
     * 立即执行任务
     * @param job
     */
    public static void invoke(JMXJob job){
        String name = job.getName();
        String group = job.getGroup();
        String schedulerName = job.getSchedulerName();
        Map<String,String> jobData = job.getJobDataMap();
        invoke(schedulerName, name, group, jobData);
    }

    /**
     * 设置任务
     * @param job
     */
    public static void add(JMXJob job){
        if(null == job){
            LOG.error("JMX添加scheduler任务失败：Job任务为空！");
            return;
        }
        String name = job.getName();
        String group = job.getGroup();
        String schedulerName = job.getSchedulerName();
        Map<String,String> jobData = job.getJobDataMap();
        try {
            connection.invoke(objectNameMap.get(schedulerName), "addJob", new Object[]{toJobDetailData(job), true}, new String[]{CompositeData.class.getName(), boolean.class.getName()});
        } catch (Exception e) {
            LOG.error("JMX添加scheduler[" + schedulerName + "]JMXJob[" + name + " , " + group + "]jobData[" + jobData + "]失败：" + e.getMessage());
            throw new RuntimeException("JMX添加scheduler任务失败：" + e.getMessage());
        }
        try {
            // 添加执行触发器到任务中
            // 添加执行触发器到任务中,执行后删除
            connection.invoke(objectNameMap.get(schedulerName), "scheduleJob", new Object[]{job.toMap(), toTriggerMap(name, group, jobData, job.getNextFireTime())}, new String[]{Map.class.getName(), Map.class.getName()});
        } catch (Exception e) {
            delete(schedulerName,name,group);
            LOG.error("JMX添加scheduler[" + schedulerName + "]JMXJob[" + name + " , " + group + "]jobData[" + jobData + "]失败：" + e.getMessage());
            throw new RuntimeException("JMX添加scheduler任务失败：" + e.getMessage());
        }
    }

    /**
     * 将Job对象转换为MBean可识别的CompositeData实例
     * @param job
     * @return
     */
    private static CompositeData toJobDetailData(JMXJob job){
        if(null == job){
            LOG.error("JMX添加scheduler任务失败：Job任务为空！");
            return null;
        }
        String name = job.getName();
        String group = job.getGroup();
        String schedulerName = job.getSchedulerName();
        Map<String,String> jobData = job.getJobDataMap();
        try {
            Map<String, Object> jobDetailMap = Maps.newHashMap();

            TabularDataSupport jobDataMap = toTabularData(job.getJobDataMap());
            TabularType tabularType = jobDataMap.getTabularType();

            jobDetailMap.put("name", job.getName());
            jobDetailMap.put("group", job.getGroup());
            jobDetailMap.put("description", job.getDescription());
            jobDetailMap.put("jobClass", job.getJobClass());
            jobDetailMap.put("jobDataMap", jobDataMap);
            jobDetailMap.put("durability", job.getDurability());
            jobDetailMap.put("shouldRecover", job.getShouldRecover());

            CompositeType compositeType = new CompositeType("JOB_DETAIL", "任务实例",
                    new String[]{"name", "group", "description", "jobClass", "jobDataMap", "durability", "shouldRecover"},
                    new String[]{"name", "group", "description", "jobClass", "jobDataMap", "durability", "shouldRecover"},
                    new OpenType[]{SimpleType.STRING, SimpleType.STRING, SimpleType.STRING, SimpleType.STRING, tabularType, SimpleType.BOOLEAN, SimpleType.BOOLEAN});
            return new CompositeDataSupport(compositeType, jobDetailMap);
        }catch(Exception e){
            LOG.error("JMX添加scheduler[" + schedulerName + "]JMXJob[" + name + " , " + group + "]jobData[" + jobData + "]失败：" + e.getMessage());
            throw new RuntimeException("JMX添加scheduler任务失败：" + e.getMessage());
        }
    }

    /**
     * 将MAP转换为MBean可识别的TabularData实例
     * @param map
     * @return
     */
    private static TabularDataSupport toTabularData(Map<String,String> map){
        if(null == map){
            map = Maps.newHashMap();
        }
        TabularType tabularType;
        TabularDataSupport tabularData = null;
        Map<String,CompositeData> compositeDataMap = Maps.newLinkedHashMap();

        Set<String> keySet = map.keySet();
        Iterator<String> iterator = keySet.iterator();

        List<String> keyList = Lists.newArrayList();

        String [] valueParams = new String[]{"key","value"};

        try {
            CompositeType valueType = new CompositeType("java.lang.String","java.lang.String",valueParams,valueParams,new OpenType[]{SimpleType.STRING,SimpleType.STRING});
            while(iterator.hasNext()){
                String key = iterator.next();
                CompositeData value = new CompositeDataSupport(valueType,valueParams,new Object[]{key,map.get(key)});
                compositeDataMap.put(key, value);
                keyList.add(key);
            }
            String typeName = "java.util.Map<java.lang.String, javax.lang.String>";
            tabularType = new TabularType(typeName, typeName, valueType, valueParams);
            tabularData = new TabularDataSupport(tabularType);
        } catch (Exception e) {
            e.printStackTrace();
        }

        tabularData.putAll(compositeDataMap);

        return tabularData;
    }

    /**
     * 暂停任务
     * @param schedulerName
     * @param name
     * @param group
     */
    public static void pause(String schedulerName,String name,String group){
        try {
            connection.invoke(objectNameMap.get(schedulerName), "pauseJob", new Object[]{name, group}, new String[]{String.class.getName(), String.class.getName()});
            LOG.error("JMX暂停scheduler[" + schedulerName + "]JMXJob[" + name + " , " + group + "]成功");
        } catch (Exception e) {
            LOG.error("JMX暂停scheduler[" + schedulerName + "]JMXJob[" + name + " , " + group + "]失败：" + e.getMessage());
            throw new RuntimeException("暂停任务失败：" + e.getMessage());
        }
    }

    /**
     * 恢复任务
     * @param schedulerName
     * @param name
     * @param group
     */
    public static void resume(String schedulerName,String name,String group){
        try {
            connection.invoke(objectNameMap.get(schedulerName), "resumeJob", new Object[]{name, group}, new String[]{String.class.getName(), String.class.getName()});
            LOG.error("JMX恢复scheduler[" + schedulerName + "]JMXJob[" + name + " , " + group + "]成功");
        } catch (Exception e) {
            LOG.error("JMX恢复scheduler[" + schedulerName + "]JMXJob[" + name + " , " + group + "]失败：" + e.getMessage());
            throw new RuntimeException("恢复任务失败：" + e.getMessage());
        }
    }

    /**
     * 删除任务
     * @param schedulerName
     * @param name
     * @param group
     */
    public static void delete(String schedulerName,String name,String group){
        try {
            connection.invoke(objectNameMap.get(schedulerName), "deleteJob", new Object[]{name, group}, new String[]{String.class.getName(), String.class.getName()});
            LOG.error("JMX删除scheduler[" + schedulerName + "]JMXJob[" + name + " , " + group + "]成功");
        } catch (Exception e) {
            LOG.error("JMX删除scheduler[" + schedulerName + "]JMXJob[" + name + " , " + group + "]失败：" + e.getMessage());
            throw new RuntimeException("删除任务失败：" + e.getMessage());
        }
    }

    /**
     * 获取以缓存的类集合
     * @return
     */
    public static Set<String> getJobClassSet(){
        return jobClassMap.keySet();
    }

    /**
     * 根据类全名获取任务详情
     * @param className
     * @return
     */
    public static JMXJob getJobByClassName(String className){
        if(StringUtils.isEmpty(className)){
            return null;
        }
        return jobClassMap.get(className);
    }

    /**
     * 获取当前连接参数
     * @return
     */
    public static JMXConnection getCurrentConnection(){
        return jmxConnection;
    }

    /**
     * 获取当前连接的任务目标集合
     * @return
     */
    public static Set<String> getSchedulerNameSet(){
        if(null != objectNameMap && !objectNameMap.isEmpty()){
            return objectNameMap.keySet();
        }
        return Sets.newHashSet();
    }

    /**
     * 清空当前连接
     */
    public static void clearConnection(){
        jmxConnection = null;
        connection = null;
        objectNameMap.clear();
        jobList.clear();
        triggerSet.clear();
        jobClassMap.clear();
    }
}
