package com.vcat.config;

import com.vcat.common.utils.IdGen;
import com.vcat.module.core.security.FormAuthenticationFilter;
import com.vcat.module.core.security.SystemAuthorizingRealm;
import com.vcat.module.core.security.shiro.session.CacheSessionDAO;
import com.vcat.module.core.security.shiro.session.SessionDAO;
import com.vcat.module.core.security.shiro.session.SessionManager;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.cas.CasFilter;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class AdminShiroConfig{
    private String adminPath = "/a";
    private long sessionTimeout = 1800000;
    private long sessionTimeoutClean = 120000;
    private Resource ehCfg = new ClassPathResource("cache/ehcache-local.xml");

    @Bean
    public FilterRegistrationBean delegatingFilterProxy(){
        DelegatingFilterProxy delegatingFilterProxy = new DelegatingFilterProxy();
        delegatingFilterProxy.setTargetBeanName("shiroFilter");
        delegatingFilterProxy.setTargetFilterLifecycle(true);
        FilterRegistrationBean shiroFRB = new FilterRegistrationBean(delegatingFilterProxy);
        shiroFRB.addUrlPatterns("/*");
        return shiroFRB;
    }

    /**
     * 安全认证过滤器
     * @return
     */
    @Bean(name="shiroFilter")
    public ShiroFilterFactoryBean shiroFilter(WebSecurityManager webSecurityManager){
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(webSecurityManager);
        shiroFilterFactoryBean.setLoginUrl(adminPath + "/login");
        shiroFilterFactoryBean.setSuccessUrl(adminPath + "?login");

        Map<String, Filter> filters = new HashMap<>();
        filters.put("cas", casFilter());
        filters.put("authc", new FormAuthenticationFilter());
        shiroFilterFactoryBean.setFilters(filters);

        // Shiro权限过滤过滤器定义
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        filterChainDefinitionMap.put("/static/**","anon");
        filterChainDefinitionMap.put("/userfiles/**","anon");
        filterChainDefinitionMap.put(adminPath + "/cas","cas");
        filterChainDefinitionMap.put(adminPath + "/login","authc");
        filterChainDefinitionMap.put(adminPath + "/logout","logout");
        filterChainDefinitionMap.put(adminPath + "/**","user");
        filterChainDefinitionMap.put("/act/rest/service/editor/**","perms[act:model:edit]");
        filterChainDefinitionMap.put("/act/rest/service/model/**","perms[act:model:edit]");
        filterChainDefinitionMap.put("/act/rest/service/**","user");
        filterChainDefinitionMap.put("/ReportServer/**","user");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);

        return shiroFilterFactoryBean;
    }

    /**
     * Shiro安全管理配置
     * @return
     */
    @Bean
    public WebSecurityManager securityManager(CacheManager shiroCacheManager,SessionManager sessionManager){
        DefaultWebSecurityManager defaultWebSecurityManager = new DefaultWebSecurityManager();
        defaultWebSecurityManager.setRealm(systemAuthorizingRealm());
        defaultWebSecurityManager.setSessionManager(sessionManager);
        defaultWebSecurityManager.setCacheManager(shiroCacheManager);
        return defaultWebSecurityManager;
    }

    @Bean
    public SystemAuthorizingRealm systemAuthorizingRealm(){
        return new SystemAuthorizingRealm();
    }

    /**
     * 授权缓存管理器
     * @return
     */
    @Bean
    public CacheManager shiroCacheManager(EhCacheManagerFactoryBean cacheManager){
        EhCacheManager ehCacheManager = new EhCacheManager();
        ehCacheManager.setCacheManager(cacheManager.getObject());
        return ehCacheManager;
    }


//
//    /**
//     * 缓存管理器
//     * @return
//     */
//    @Bean(name = "cacheManager")
//    public net.sf.ehcache.CacheManager cacheManager() {
//        EhCacheManagerFactoryBean ehCacheManagerFactoryBean = new EhCacheManagerFactoryBean();
//        ehCacheManagerFactoryBean.setConfigLocation(ehCfg);
//        return ehCacheManagerFactoryBean.getObject();
//    }

    /**
     * 自定义会话管理配置
     * @return
     */
    @Bean
    public SessionManager sessionManager(SessionDAO sessionDAO){
        SessionManager defaultSessionManager = new SessionManager();
        defaultSessionManager.setSessionDAO(sessionDAO);
        defaultSessionManager.setGlobalSessionTimeout(sessionTimeout);
        defaultSessionManager.setSessionValidationInterval(sessionTimeoutClean);
        defaultSessionManager.setSessionValidationSchedulerEnabled(true);
        /*
            指定本系统SESSIONID, 默认为: JSESSIONID 问题: 与SERVLET容器名冲突, 如JETTY, TOMCAT 等默认JSESSIONID,
            当跳出SHIRO SERVLET时如ERROR-PAGE容器会为JSESSIONID重新分配值导致登录会话丢失!
         */
        defaultSessionManager.setSessionIdCookie(new SimpleCookie("vcat.session.id"));
        defaultSessionManager.setSessionIdCookieEnabled(true);
        return defaultSessionManager;
    }

    /**
     * 自定义Session存储容器
     * @return
     */
    @Bean
    public SessionDAO sessionDAO(CacheManager shiroCacheManager){
        CacheSessionDAO sessionDAO = new CacheSessionDAO();
        sessionDAO.setSessionIdGenerator(new IdGen());
        sessionDAO.setActiveSessionsCacheName("activeSessionsCache");
        sessionDAO.setCacheManager(shiroCacheManager);
        return sessionDAO;
    }

    /**
     * CAS认证过滤器
     * @return
     */
    @Bean
    public CasFilter casFilter(){
        CasFilter casFilter = new CasFilter();
        casFilter.setFailureUrl(adminPath + "/login");
        return casFilter;
    }

    /**
     * 保证实现了Shiro内部lifecycle函数的bean执行
     * @return
     */
    @Bean(name = "lifecycleBeanPostProcessor")
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor(){
        return new LifecycleBeanPostProcessor();
    }

    /**
     * AOP式方法级权限检查
     * @return
     */
    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator(){
        DefaultAdvisorAutoProxyCreator daapc = new DefaultAdvisorAutoProxyCreator();
        daapc.setProxyTargetClass(true);
        return daapc;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(WebSecurityManager webSecurityManager){
        AuthorizationAttributeSourceAdvisor aasa = new AuthorizationAttributeSourceAdvisor();
        aasa.setSecurityManager(webSecurityManager);
        return aasa;
    }

}
