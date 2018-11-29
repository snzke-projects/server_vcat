package com.vcat.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(value = "com.vcat.common.persistence.annotation.MyBatisDao",
        basePackages = {"com.vcat"})
public class AdminMybatisConfig {
//    @Autowired
//    private DataSource dataSource;
//    @Bean
//    public SqlSessionFactoryBean sqlSessionFactoryBean(){
//        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
//        sqlSessionFactoryBean.setDataSource(dataSource);
//        sqlSessionFactoryBean.setTypeAliasesPackage("com.vcat");
//        sqlSessionFactoryBean.setTypeAliasesSuperType(Entity.class);
//        sqlSessionFactoryBean.setMapperLocations(new Resource[]{new ClassPathResource("/mappings/**/*.xml")});
//        sqlSessionFactoryBean.setConfigLocation(new ClassPathResource("/mybatis-config.xml"));
//        return sqlSessionFactoryBean;
//    }
}
