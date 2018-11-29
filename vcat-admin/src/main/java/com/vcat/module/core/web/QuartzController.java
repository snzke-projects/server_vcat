package com.vcat.module.core.web;

import com.google.common.collect.Maps;
import com.vcat.common.persistence.Page;
import com.vcat.common.quartz.JMXConnection;
import com.vcat.common.quartz.JMXJob;
import com.vcat.common.quartz.JMXQuartzService;
import com.vcat.common.utils.DateUtils;
import com.vcat.common.web.BaseController;
import com.vcat.module.core.utils.UserUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 定时任务
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/quartz")
public class QuartzController extends BaseController {
    private final String paramPrefix = "quartz_p_";

	@RequiresPermissions("sys:quartz:list")
	@RequestMapping(value = "list")
	public String list(JMXConnection connection,boolean needReconnect,HttpServletRequest request,HttpServletResponse response,Model model) {
        if(null == connection || connection.isEmpty()){
            if(null == JMXQuartzService.getCurrentConnection()){
                connection = JMXConnection.getDefault();
            }else{
                connection = JMXQuartzService.getCurrentConnection();
            }
        }
        if(needReconnect){
            JMXQuartzService.reconnect();
        }
        try {
            model.addAttribute("page", JMXQuartzService.findAll(new Page<>(request, response),connection));
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage().replaceAll("\r", "</br>").replaceAll("\n","</br>").replaceAll("\r\n","</br>"));
        }
        connection.setIsModify(false);
        model.addAttribute("connection", connection);
        model.addAttribute("paramPrefix", paramPrefix);
		return "module/sys/quartzList";
	}

    @ResponseBody
    @RequiresPermissions("sys:quartz:invoke")
    @RequestMapping(value = "invoke")
    public void invoke(@RequestParam(value = "schedulerName")String schedulerName,@RequestParam(value = "name")String name
            ,@RequestParam(value = "groupName")String groupName,HttpServletRequest request) {
        JMXQuartzService.invoke(schedulerName, name, groupName, getQuartzParameter(request));
    }

    /**
     * 获取定时任务参数
     * @param request
     * @return
     */
    private Map<String,String> getQuartzParameter(HttpServletRequest request){
        Map<String,String[]> parameter = request.getParameterMap();

        Map<String,String> map = Maps.newHashMap();

        Set<String> ketSet = parameter.keySet();
        Iterator<String> iterator = ketSet.iterator();
        while(iterator.hasNext()){
            String key = iterator.next();
            Object value = parameter.get(key);
            if(key.indexOf(paramPrefix) == 0 && value instanceof Object[] && ((Object[])value).length == 1){
                String[] values = (String[])value;
                map.put(key.replace(paramPrefix,""),values[0]);
            }
        }

        return map;
    }

    @ResponseBody
    @RequiresPermissions("sys:quartz:pause")
    @RequestMapping(value = "pause")
    public void pause(@RequestParam(value = "schedulerName")String schedulerName,@RequestParam(value = "name")String name,@RequestParam(value = "groupName")String groupName) {
        JMXQuartzService.pause(schedulerName, name, groupName);
        JMXQuartzService.reconnect();
    }

    @ResponseBody
    @RequiresPermissions("sys:quartz:resume")
    @RequestMapping(value = "resume")
    public void resume(@RequestParam(value = "schedulerName")String schedulerName,@RequestParam(value = "name")String name,@RequestParam(value = "groupName")String groupName){
        JMXQuartzService.resume(schedulerName, name, groupName);
        JMXQuartzService.reconnect();
    }

    @ResponseBody
    @RequiresPermissions("sys:quartz:delete")
    @RequestMapping(value = "delete")
    public void delete(@RequestParam(value = "schedulerName")String schedulerName,@RequestParam(value = "name")String name,@RequestParam(value = "groupName")String groupName){
        JMXQuartzService.delete(schedulerName, name, groupName);
        JMXQuartzService.reconnect();
    }

    @ResponseBody
    @RequestMapping(value = "reconnect")
    public Map<String,Object> reconnect(JMXConnection connection){
        if(null == connection || connection.isEmpty()){
            JMXQuartzService.reconnect();
        }else{
            JMXQuartzService.reconnect(connection);
        }
        Map<String,Object> result = Maps.newHashMap();
        result.put("schedulerNameArray",JMXQuartzService.getSchedulerNameSet());
        result.put("classSet", JMXQuartzService.getJobClassSet());
        return result;
    }

    @RequestMapping(value = "add")
    @RequiresPermissions("sys:quartz:add")
    public String add(Model model){
        model.addAttribute("classSet", JMXQuartzService.getJobClassSet());
        JMXConnection currentConnection = JMXQuartzService.getCurrentConnection();
        if(null != currentConnection && !currentConnection.isEmpty()){
            model.addAttribute("currentConnection", JMXQuartzService.getCurrentConnection());
        }
        model.addAttribute("paramPrefix", paramPrefix);
        model.addAttribute("schedulerNameSet", JMXQuartzService.getSchedulerNameSet());
        return "module/sys/quartzForm";
    }

    @ResponseBody
    @RequestMapping(value = "setJob")
    @RequiresPermissions("sys:quartz:add")
    public void setJob(JMXConnection connection,JMXJob job,HttpServletRequest request){
        Map<String,String> jobDataMap = getQuartzParameter(request);
        Date nextFireTime = job.getNextFireTime();
        String schedulerName = job.getSchedulerName();

        if(null != connection && !connection.isEmpty() && !JMXQuartzService.getCurrentConnection().equals(connection)){
            JMXQuartzService.reconnect(connection);
        }

        job = JMXQuartzService.getJobByClassName(job.getJobClass());

        if(null == job){
            job = new JMXJob();
            job.setSchedulerName(schedulerName);
            job.setJobClass(request.getParameter("jobClass"));
            job.setName(request.getParameter("jobName"));
            job.setGroup("JAVA_CUSTOM_JOB_GROUP");
            job.setJobDataMap(jobDataMap);
            job.setDurability(true);
            nextFireTime = null == nextFireTime ? new Date() : nextFireTime;
            job.setNextFireTime(nextFireTime);
            job.setDescription("用户[" + UserUtils.getUser().getName() + "]在[" + DateUtils.getDateTime() + "]设置任务[" + job.getName() + "]于" + DateUtils.formatDateTime(job.getNextFireTime()) + "]执行");

            JMXQuartzService.add(job);
        }else{
            job.setSchedulerName(schedulerName);
            job.setJobDataMap(jobDataMap);
            JMXQuartzService.invoke(job);
        }
    }

    @ResponseBody
    @RequiresPermissions("sys:quartz:add")
    @RequestMapping(value = "getJobDetail")
    public JMXJob getJobDetail(String className){
        return JMXQuartzService.getJobByClassName(className);
    }
}
