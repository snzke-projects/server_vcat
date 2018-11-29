package com.vcat.common.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

/**
 * Spring工具栏
 * @author zfc
 */
public class ApplicationContextHelper {
    private static ApplicationContext applicationContext;
    /**
     * 此方法可以把ApplicationContext对象inject到当前类中作为一个静态成员变量。 
     * @param context ApplicationContext 对象.
     * @throws BeansException
     * @author zfc
     */
    public static void setApplicationContext( ApplicationContext context ) throws BeansException {
        applicationContext = context;
    }

    /**
     * 获取ApplicationContext
     * @return ApplicationContext
     * @author zfc
     */
    public static ApplicationContext getApplicationContext(){
        return applicationContext;
    }

    /**
     * 这是一个便利的方法，帮助我们快速得到一个BEAN 
     * @param beanName bean的名字 
     * @return 返回一个bean对象
     * @author zfc
     */
    public static Object getBean( String beanName ) {
        if(null == applicationContext){
            return null;
        }
        return applicationContext.getBean( beanName );
    }
}