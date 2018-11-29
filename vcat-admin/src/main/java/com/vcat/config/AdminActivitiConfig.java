package com.vcat.config;

import com.vcat.common.utils.IdGen;
import com.vcat.module.activiti.service.ext.ActGroupEntityServiceFactory;
import com.vcat.module.activiti.service.ext.ActUserEntityServiceFactory;
import org.activiti.engine.*;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.util.Arrays;

@Configuration
public class AdminActivitiConfig {
    @Value("${activiti.diagram.activityFontName}")
    private String activityFontName;
    @Value("${activiti.diagram.labelFontName}")
    private String labelFontName;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private DataSourceTransactionManager transactionManager;

    private ProcessEngine processEngine;


    @Bean
    public SpringProcessEngineConfiguration processEngineConfiguration(){
        SpringProcessEngineConfiguration spec = new SpringProcessEngineConfiguration();
        spec.setDataSource(dataSource);
        spec.setTransactionManager(transactionManager);
        spec.setDatabaseSchemaUpdate("true");
        spec.setJobExecutorActivate(false);
        spec.setHistory("full");
        spec.setProcessDefinitionCacheLimit(10);

        // UUID作为主键生成策略
        spec.setIdGenerator(new IdGen());

        // 生成流程图的字体
        spec.setActivityFontName(activityFontName);
        spec.setLabelFontName(labelFontName);

        // 自定义用户权限
        spec.setCustomSessionFactories(Arrays.asList(new ActUserEntityServiceFactory(),new ActGroupEntityServiceFactory()));
        return spec;
    }

    @Bean(name = "processEngine")
    public ProcessEngineFactoryBean processEngine(){
        ProcessEngineFactoryBean pefb = new ProcessEngineFactoryBean();
        pefb.setProcessEngineConfiguration(processEngineConfiguration());
        try {
            processEngine = pefb.getObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pefb;
    }

    @Bean
    @DependsOn("processEngine")
    public RepositoryService getRepositoryService(){
        return processEngine.getRepositoryService();
    }

    @Bean
    @DependsOn("processEngine")
    public RuntimeService getRuntimeService(){
        return processEngine.getRuntimeService();
    }

    @Bean
    @DependsOn("processEngine")
    public FormService getFormService(){
        return processEngine.getFormService();
    }

    @Bean
    @DependsOn("processEngine")
    public TaskService getTaskService(){
        return processEngine.getTaskService();
    }

    @Bean
    @DependsOn("processEngine")
    public HistoryService getHistoryService(){
        return processEngine.getHistoryService();
    }

    @Bean
    @DependsOn("processEngine")
    public IdentityService getIdentityService(){
        return processEngine.getIdentityService();
    }

    @Bean
    @DependsOn("processEngine")
    public ManagementService getManagementService(){
        return processEngine.getManagementService();
    }
}
