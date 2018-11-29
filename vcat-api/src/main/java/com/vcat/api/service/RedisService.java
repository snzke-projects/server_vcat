package com.vcat.api.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vcat.common.constant.ApiConstants;
import com.vcat.common.utils.JedisUtils;
import com.vcat.module.ec.entity.Shop;


/**
 * token处理类
 * @author cw
 *
 */
@Service
public class RedisService {

	/**
	 * 登录成功后，生成accesstoken，并放验证码到redis服务器
	 */
	public String putAccessToken(String userName,String token){
		JedisUtils.set(userName+"_"+ApiConstants.SELLERTOKEN, token,ApiConstants.DEFAULT_TOKEN_TIMEOUT);
		return token;
	}
	/**
	 * 验证时，根据用户名，获取redis里面的accesstoken
	 * @return
	 */
	public String getAccessToken(String userName){
		return JedisUtils.get(userName+"_"+ApiConstants.SELLERTOKEN);
	}
	
	/**
	 * 登录成功后，生成Buyertoken，并放验证码到redis服务器
	 */
	public String putBuyerToken(String userName,String token){
		String oldtoken = JedisUtils.get(userName+"_"+ApiConstants.BUYERTOKEN);
		if(com.vcat.common.utils.StringUtils.isBlank(oldtoken)){
			JedisUtils.set(userName+"_"+ApiConstants.BUYERTOKEN, token,ApiConstants.DEFAULT_TOKEN_TIMEOUT);
			oldtoken = token;
		}
		return oldtoken;
	}
	/**
	 * 验证时，根据用户名，获取redis里面的Buyertoken
	 * @return
	 */
	public String getBuyerToken(String userName){
		
		return JedisUtils.get(userName+"_"+ApiConstants.BUYERTOKEN);
	}
	
	/**
	 * 生成验证码，并将验证码存入redis
	 * @param phoneNum
	 * @return
	 */
	public String putIdentifyingCode(String phoneNum,String code){
		JedisUtils.set(phoneNum+"_"+ApiConstants.CODE, code,ApiConstants.DEFAULT_IDCODE_TIMEOUT);
		return code;
	}
	/**
	 * 从redis获取验证码
	 * @param phoneNum
	 * @return
	 */
	public String getIdentifyingCode(String phoneNum){
		return JedisUtils.get(phoneNum+"_"+ApiConstants.CODE);
	}
	/**
	 * 从redis获取列表
	 * @param <T>
	 * @param key
	 * @return
	 */
	public <T> List<T> getList(String key) {
		return (List<T>) JedisUtils.getObject(key);
	}
	/**
	 * 把list存入redis
	 * @param <T>
	 * @param key
	 * @param List
	 */
	public <T> void setList(String key,List<T> list){
		JedisUtils.setObject(key, (List<Object>) list, 0);
	}
	/**
	 * 获取收入排行
	 * @param pageNo
	 * @param rankType
	 * @return
	 */
	public List<Shop> getRankList(Integer pageNo, String rankType) {
		String key = pageNo+"_"+rankType+"_rank";
		return (List<Shop>) JedisUtils.getObject(key);
	}
	/**
	 * 设置收入排行
	 * @param pageNo
	 * @param rankType
	 * @param list
	 */
	public void setRankList(Integer pageNo, String rankType, List<Shop> list) {
		String key = pageNo+"_"+rankType+"_rank";
		JedisUtils.setObject(key,  list, 1*60);
	}

	public String getMyMonthRank(String shopId) {
		String key = shopId+"_myMothRank";
		return JedisUtils.get(key);
	}

	public void setMyMonthRank(String shopId, String rank) {
		String key = shopId+"_myMothRank";
		JedisUtils.set(key, rank,1*60);
	}
	public void deleteToken(String customerId) {
		deleteSellerToken(customerId);
		JedisUtils.del(customerId+"_"+ApiConstants.BUYERTOKEN);
	}
	public void deleteSellerToken(String customerId) {
		JedisUtils.del(customerId+"_"+ApiConstants.SELLERTOKEN);
	}
}
