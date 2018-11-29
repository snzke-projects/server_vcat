package com.vcat.api.security;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import com.vcat.api.service.CustomerRoleService;
import com.vcat.api.service.RedisService;
import com.vcat.common.constant.ApiConstants;
import com.vcat.common.utils.StringUtils;
import com.vcat.module.ec.entity.CustomerRole;

public class StatelessRealm extends AuthorizingRealm {
	@Autowired
	private RedisService redisService;
	@Autowired
	private CustomerRoleService customerRoleService;
	private static Logger logger = Logger.getLogger(StatelessRealm.class);
	@Override
	public boolean supports(AuthenticationToken token) {
		// 仅支持StatelessToken类型的Token
		return token instanceof StatelessToken;
	}

	//授权
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(
			PrincipalCollection principals) {
		// 根据uniqueToken查找角色，请根据需求实现
		String uniqueToken = (String) principals.getPrimaryPrincipal();
		//从token获取customerId
		String customerId = StringUtils.getCustomerIdByToken(uniqueToken);
		//通过customerId查询角色
		
		List<CustomerRole> roles = customerRoleService.findRoleList(customerId);
		SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
		//将所有角色放入权限验证器中
		if(roles!=null&&roles.size()!=0){
			for(CustomerRole role:roles){
				authorizationInfo.addRole(role.getRole().getRoleName());
			}
		}
		return authorizationInfo;
	}

	//登录
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(
			AuthenticationToken token) throws AuthenticationException {
		StatelessToken statelessToken = (StatelessToken) token;
		String uniqueToken = statelessToken.getUniqueToken();
		logger.debug("token = "+uniqueToken);
		//判断token是否合法
		String customerId = StringUtils.getCustomerIdByToken(uniqueToken);
		int role = StringUtils.getRoleByToken(uniqueToken);
		if(ApiConstants.BUYER_FLAG == role){
			return new SimpleAuthenticationInfo(uniqueToken, redisService.getBuyerToken(customerId), getName());
		}else {
			return new SimpleAuthenticationInfo(uniqueToken, redisService.getAccessToken(customerId), getName());
		}
	}
}
