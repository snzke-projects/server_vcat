package com.vcat;

import com.vcat.config.CacheConfig;
import com.vcat.config.MybatisConfig;
import com.vcat.config.ShiroConfig;
import com.vcat.config.WebMvcConfig;
import com.vcat.config.mq.DelayMQConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.websocket.WebSocketAutoConfiguration;
import org.springframework.context.annotation.*;
/**
 * Created by ylin on 2016/5/3.
 */
@Configuration
@Import({CacheConfig.class, WebMvcConfig.class, ShiroConfig.class, MybatisConfig.class, DelayMQConfig.class})
@ComponentScan(basePackages = "com.vcat")
@ImportResource("classpath:quartz-context.xml")
@EnableAutoConfiguration(exclude = {WebSocketAutoConfiguration.class,FreeMarkerAutoConfiguration.class})
public class ApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }
}