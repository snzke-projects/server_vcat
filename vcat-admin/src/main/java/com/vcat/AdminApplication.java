package com.vcat;

import com.vcat.config.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.websocket.WebSocketAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

@Configuration
@Import({CacheConfig.class,AdminConfig.class, AdminWebMvcConfig.class, AdminMybatisConfig.class,
        AdminShiroConfig.class,
        AdminActivitiConfig.class})
@ComponentScan(basePackages = {"com.vcat"})
@ImportResource("classpath:quartz-context.xml")
@EnableAutoConfiguration(exclude = {WebSocketAutoConfiguration.class,FreeMarkerAutoConfiguration.class})
public class AdminApplication extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(AdminApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
    }
}