package com.easemob.server.common;

import com.vcat.common.config.Global;

/**
 * Constants
 * 
 * @author Lynch 2014-09-15
 *
 */
public interface Constants {
	// API_HTTP_SCHEMA
	public static String API_HTTP_SCHEMA = "https";
	// API_SERVER_HOST
	public static String API_SERVER_HOST = "a1.easemob.com";
	// APPKEY
	public static String APPKEY = Global.getConfig("huan.app.key");//"vcat#vcat";//"chen2991101#mytest";//"easemob-playground#test1";
	// APP_CLIENT_ID
	public static String APP_CLIENT_ID = Global.getConfig("huan.app.client.id");//"YXA6DMVskFbBEeWQPFVp5JRAVQ";//"YXA6-dLmID1KEeSckol0pj9MVw";//"YXA6wDs-MARqEeSO0VcBzaqg5A";
	// APP_CLIENT_SECRET
	public static String APP_CLIENT_SECRET = Global.getConfig("huan.app.client.secret");//"YXA6fJptTuPs_RlhmZDSa1pSz3x3TSg";//"YXA6c1SklalxejqpSMLL4Bg8nmuMg-Y";//"YXA6JOMWlLap_YbI_ucz77j-4-mI0JA";
	// DEFAULT_PASSWORD
	public static String DEFAULT_PASSWORD = "vcat_default_@@##$$";//"123456";
}
