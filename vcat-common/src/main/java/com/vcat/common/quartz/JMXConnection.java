package com.vcat.common.quartz;

import com.vcat.common.config.Global;
import com.vcat.common.persistence.Page;
import com.vcat.common.utils.StringUtils;

public class JMXConnection {
    private String host;
    private String port;
    private String username;
    private String password;
    private boolean isModify = false;

    private Page<JMXJob> page = new Page<>();

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean getIsModify() {
        return isModify;
    }

    public void setIsModify(boolean isModify) {
        this.isModify = isModify;
    }

    public Page<JMXJob> getPage() {
        return page;
    }

    public void setPage(Page<JMXJob> page) {
        this.page = page;
    }

    public boolean isEmpty(){
        return StringUtils.isEmpty(username)
                && StringUtils.isEmpty(password)
                && StringUtils.isEmpty(host)
                && StringUtils.isEmpty(port);
    }

    public boolean equals(JMXConnection connection){
        if(null == connection || connection.isEmpty()){
            return false;
        }

        if(null == connection.getHost()
                || null == connection.getPort()){
            return false;
        }

        if(!connection.getHost().equals(host)
                || !connection.getPort().equals(port)){
            return false;
        }

        if(!(connection.getUsername() + "").equals(username)
                || !(connection.getPassword() + "").equals(password)){
            return false;
        }

        return true;
    }

    public static JMXConnection getDefault(){
        JMXConnection jmxConnection = new JMXConnection();
        jmxConnection.setUsername(Global.getConfig("jmx.quartz.username"));
        jmxConnection.setPassword(Global.getConfig("jmx.quartz.password"));
        jmxConnection.setHost(Global.getConfig("jmx.quartz.host"));
        jmxConnection.setPort(Global.getConfig("jmx.quartz.port"));
        jmxConnection.setIsModify(true);
        return jmxConnection;
    }
}
