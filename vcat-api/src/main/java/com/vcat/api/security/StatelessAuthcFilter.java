package com.vcat.api.security;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vcat.common.constant.ApiMsgConstants;
import org.apache.shiro.web.filter.AccessControlFilter;

import com.alibaba.fastjson.JSONObject;
import com.vcat.module.core.entity.MsgEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatelessAuthcFilter extends AccessControlFilter {
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	protected boolean isAccessAllowed(ServletRequest request,
			ServletResponse response, Object mappedValue) throws Exception {
		return false;
	}
	@SuppressWarnings("unchecked")
	@Override
	protected boolean onAccessDenied(ServletRequest request,
			ServletResponse response) throws Exception {
		HttpServletRequest res = (HttpServletRequest)request;
		String uniqueToken = res.getHeader("token");
		Map<String, String[]> params = new HashMap<String, String[]>(
				request.getParameterMap());

		// 生成无状态Token
		StatelessToken stoken = new StatelessToken(uniqueToken, params);

		try {
			// 委托给Realm进行登录
			getSubject(request, response).login(stoken);
		} catch (Exception e) {
			logger.debug(e.getMessage(),e);
			onLoginFail(request,response); // 登录失败
			return false;
		}
		return true;
	}

	// 登录失败时默认返回401状态码
	private void onLoginFail(ServletRequest request,ServletResponse response) throws IOException {
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		response.setContentType("application/json;charset=UTF-8");
		//返回用户没有登录错误
		httpResponse.getWriter().write(JSONObject.toJSON(new MsgEntity(ApiMsgConstants.TOKEN_ILLEGAL,
			ApiMsgConstants.TOKEN_ILLEGAL_CODE)).toString());
	}
}
