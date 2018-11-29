package com.vcat.module.common.entity;

import com.vcat.common.utils.DateUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.io.Serializable;
import java.util.Date;

/**
 * 物流详情
 */
public class LogisticsInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private Date ftime;
    private String context;
    private String location;
    private Date time;

    public String getFtime() {
        return DateUtils.formatDate(ftime,"yyyy-MM-dd HH:mm:ss");
    }

    public void setFtime(String ftime) {
        this.ftime = DateUtils.parseDate(ftime);
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTime() {
        return DateUtils.formatDate(time,"yyyy-MM-dd HH:mm:ss");
    }

    public void setTime(String time) {
        this.time = DateUtils.parseDate(time);
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
