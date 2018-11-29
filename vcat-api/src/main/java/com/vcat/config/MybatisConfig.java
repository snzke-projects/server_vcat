package com.vcat.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created by ylin on 2016/5/6.
 */
@Configuration
@MapperScan(value = "com.vcat.common.persistence.annotation.MyBatisDao",
        basePackages = {"com.vcat"})
public class MybatisConfig {
}
