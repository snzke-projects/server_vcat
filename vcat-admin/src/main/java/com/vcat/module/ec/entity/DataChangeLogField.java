package com.vcat.module.ec.entity;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注了此注解的属性可进行比较及存入日志
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DataChangeLogField{
    /**
     * 变更字段展示名称
     * @return 变更字段展示名称
     */
    String title() default "无";
}
