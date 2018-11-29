package com.vcat.config;

import com.opensymphony.sitemesh.webapp.SiteMeshFilter;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.vcat.common.mapper.JsonMapper;
import com.vcat.common.persistence.entity.Entity;
import com.vcat.common.servlet.ValidateCodeServlet;
import com.vcat.common.supcan.treelist.TreeList;
import com.vcat.common.supcan.treelist.cols.Col;
import com.vcat.common.supcan.treelist.cols.Group;
import com.vcat.module.core.interceptor.LogInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MarshallingHttpMessageConverter;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.web.accept.ContentNegotiationManagerFactoryBean;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@Configuration
@ComponentScan(basePackages = {"com.vcat"})
public class AdminWebMvcConfig extends DelegatingWebMvcConfiguration {
    @Value("${adminPath}")
    private String adminPath;
    @Value("${web.view.prefix}")
    private String webViewPrefix;
    @Value("${web.view.suffix}")
    private String webViewSuffix;

    @Override
    protected void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/","/a");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/static/**")
                .addResourceLocations("/static/").setCachePeriod(31536000);
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Bean
    public FilterRegistrationBean siteMeshFilter() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setName("sitemesh");
        filterRegistrationBean.setFilter(new SiteMeshFilter());
        filterRegistrationBean.addUrlPatterns("/a/*","/f/*");
        return filterRegistrationBean;
    }

    @Bean
    public ServletRegistrationBean servletRegistrationBean(){
        return new ServletRegistrationBean(new ValidateCodeServlet(),"/servlet/validateCodeServlet");
    }

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        // 添加日志拦截器
        registry.addInterceptor(new LogInterceptor())
                .addPathPatterns(adminPath + "/**")
                .excludePathPatterns(adminPath + "/"
                        , adminPath + "/login"
                        , adminPath + "/sys/menu/tree"
                        , adminPath + "/sys/menu/treeData"
                        , adminPath + "/oa/oaNotify/self/count");
    }


    @Override
    protected void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 将StringHttpMessageConverter的默认编码设为UTF-8
        converters.add(new StringHttpMessageConverter(Charset.forName("UTF-8")));

        // 将Jackson2HttpMessageConverter的默认格式化输出为false
        MappingJackson2HttpMessageConverter mj2hmc = new MappingJackson2HttpMessageConverter();
        mj2hmc.setSupportedMediaTypes(Arrays.asList(new MediaType("application", "json"),new MediaType("charset", "UTF-8")));
        mj2hmc.setPrefixJson(false);
        mj2hmc.setObjectMapper(new JsonMapper());
        converters.add(mj2hmc);

        // 使用XML格式输出数据
        XStreamMarshaller xsm = new XStreamMarshaller();
        xsm.setStreamDriver(new StaxDriver());
        xsm.setAnnotatedClasses(Entity.class, TreeList.class, Col.class, Group.class);
        MarshallingHttpMessageConverter mhmc = new MarshallingHttpMessageConverter(xsm);
        mhmc.setSupportedMediaTypes(Arrays.asList(new MediaType("application", "xml")));
        converters.add(mhmc);
    }

    /**
     * REST中根据URL后缀自动判定Content-Type及相应的View
     * @return
     */
    @Bean(name="contentNegotiationManager")
    @Primary
    public ContentNegotiationManagerFactoryBean contentNegotiationManager(){
        ContentNegotiationManagerFactoryBean cnmfb = new ContentNegotiationManagerFactoryBean();
        Properties properties = new Properties();
        properties.put("xml","application/xml");
        properties.put("json","application/json");
        cnmfb.setMediaTypes(properties);
        cnmfb.setIgnoreAcceptHeader(true);
        cnmfb.setFavorPathExtension(true);
        return cnmfb;
    }

    /**
     * 定义视图文件解析
     * @return
     */
    @Bean
    public InternalResourceViewResolver internalResourceViewResolver(){
        InternalResourceViewResolver irvr = new InternalResourceViewResolver();
        irvr.setPrefix(webViewPrefix);
        irvr.setSuffix(webViewSuffix);
        return irvr;
    }

    /**
     * 错误拦截
     * @return
     */
    @Bean
    public HandlerExceptionResolver simpleMappingExceptionResolver(){
        SimpleMappingExceptionResolver smer = new SimpleMappingExceptionResolver();
        Properties properties = new Properties();
        properties.put("org.apache.shiro.authz.UnauthorizedException","error/403");
        properties.put("java.lang.Throwable","error/500");
        smer.setExceptionMappings(properties);
        return smer;
    }
}
