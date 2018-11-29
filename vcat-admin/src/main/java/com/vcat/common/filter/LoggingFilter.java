package com.vcat.common.filter;

import com.vcat.common.utils.DateUtils;
import com.vcat.common.utils.IdGen;
import com.vcat.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.NamedThreadLocal;
import org.springframework.web.filter.AbstractRequestLoggingFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class LoggingFilter extends AbstractRequestLoggingFilter {
    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);
    private final static String DEFAULT_CID = "1234567890";
    private static final ThreadLocal<Long> startTimeThreadLocal = new NamedThreadLocal<Long>(
            "ThreadLocal StartTime");

    private boolean shouldLog = false;

    @Override
    protected boolean shouldLog(HttpServletRequest request) {
        return shouldLog;
    }

    private Map<String, String> getTypesafeRequestMap(HttpServletRequest request) {
        Map<String, String> typesafeRequestMap = new HashMap();
        Enumeration<?> requestParamNames = request.getParameterNames();
        while (requestParamNames.hasMoreElements()) {
            String requestParamName = (String)requestParamNames.nextElement();
            String requestParamValue = request.getParameter(requestParamName);
            typesafeRequestMap.put(requestParamName, requestParamValue);
        }
        return typesafeRequestMap;
    }
    public LoggingFilter(){
        setMaxPayloadLength(2048);
        setIncludePayload(false);
        setIncludeQueryString(false);
        setAfterMessagePrefix("");
        setAfterMessageSuffix("");
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        //记录body
        if("POST".equalsIgnoreCase(request.getMethod()) && "application/json".equalsIgnoreCase(request.getContentType())){
            shouldLog=true;
            setIncludePayload(true);
        }else{
            shouldLog=false;
            setIncludePayload(false);
        }

        String uniqueToken = request.getHeader("token");
        String customerId = StringUtils.getCustomerIdByToken(uniqueToken);
        int role = StringUtils.getRoleByToken(uniqueToken);
        String ip = request.getHeader("X-Real-IP");
        String host = request.getHeader("Host");

        MDC.put("request_id",(StringUtils.isEmpty(customerId) ? DEFAULT_CID : customerId) + "@" + IdGen.getRandomNumber(10));
        logger.debug("请求开始: host-ip:{}-{} token:{}  customer-role: {}-{}  URI: {}", host, ip, uniqueToken,
                customerId, role, request.getRequestURI());
        long beginTime = System.currentTimeMillis();// 1、开始时间
        startTimeThreadLocal.set(beginTime); // 线程绑定变量（该数据只有当前请求的线程可见）
        logger.debug("开始计时: {}  URI: {}", new SimpleDateFormat(
                "hh:mm:ss.SSS").format(beginTime), request.getRequestURI());

        ResponseLoggingWrapper responseLoggingWrapper = new ResponseLoggingWrapper(response);
        super.doFilterInternal(request, responseLoggingWrapper, filterChain);

        logger.debug("请求结束:  token:{}  customer-role: {}-{}  URI: {}", uniqueToken,
                customerId, role, request.getRequestURI());
        long endTime = System.currentTimeMillis(); // 2、结束时间
        logger.debug(
                "计时结束：{}  耗时：{}  URI: {}  最大内存: {}m  已分配内存: {}m  已分配内存中的剩余空间: {}m  最大可用内存: {}m",
                new SimpleDateFormat("hh:mm:ss.SSS").format(endTime),
                DateUtils.formatDateTime(endTime - startTimeThreadLocal.get()), request
                        .getRequestURI(),
                Runtime.getRuntime().maxMemory() / 1024 / 1024, Runtime
                        .getRuntime().totalMemory() / 1024 / 1024, Runtime
                        .getRuntime().freeMemory() / 1024 / 1024, (Runtime
                        .getRuntime().maxMemory()
                        - Runtime.getRuntime().totalMemory() + Runtime
                        .getRuntime().freeMemory()) / 1024 / 1024);
        MDC.remove("request_id");
    }

    @Override
    protected void beforeRequest(HttpServletRequest request, String message) {

    }

    @Override
    protected void afterRequest(HttpServletRequest request, String message) {
        logger.debug("HTTP请求: " + message);
        logger.debug("GET/POST参数:"+getTypesafeRequestMap(request));
    }
}
