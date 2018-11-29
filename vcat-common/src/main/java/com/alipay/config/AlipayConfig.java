package com.alipay.config;

/* *
 *类名：AlipayConfig
 *功能：基础配置类
 *详细：设置帐户有关信息及返回路径
 *版本：3.3
 *日期：2012-08-10
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
	
 *提示：如何获取安全校验码和合作身份者ID
 *1.用您的签约支付宝账号登录支付宝网站(www.alipay.com)
 *2.点击“商家服务”(https://b.alipay.com/order/myOrder.htm)
 *3.点击“查询合作者身份(PID)”、“查询安全校验码(Key)”

 *安全校验码查看时，输入支付密码后，页面呈灰色的现象，怎么办？
 *解决方法：
 *1、检查浏览器配置，不让浏览器做弹框屏蔽设置
 *2、更换浏览器或电脑，重新登录查询。
 */

public class AlipayConfig {
	
	//↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
	// 合作身份者ID，以2088开头由16位纯数字组成的字符串
	public static String partner = "2088911732236291";
	
	// 收款支付宝账号，以2088开头由16位纯数字组成的字符串
	public static String seller_id = partner;
	// 商户的私钥
	public static String private_key = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALgYie+8JJaZt0Eq"+
			"fBCNqMuNkqgLFRWD9uT1R0PkSi9EOija8/sFX3O9snHutYwP1puiLxpZ0tRrarkK"+
			"M2PyxUBydxFdhv7zuyr+fhRIlH9DQPwiF7JI7zVmZpb2mN3RfvQ0CbZ8TjwMEUrI"+
			"Xrjl/SIMbuqx9w2cdgTqCMbn5u6zAgMBAAECgYEAkYFbrK9BIANY01nX+2Fr847v"+
			"RoCT5pCKGQdAj4mllNxRN69vPY1Ol3Vs0B5DQfPWxOYYYrRyLdDE5oIIgkwE2Ico"+
			"/3xuqDoyOwPWB7P92ZNHcMd3ZgiYQ6iyTXD5gcQj/CSNkX5aeayXM3vdvviEgOcB"+
			"1V7MfspD9eODgB4/WckCQQDcjLb6dh+/XqrRQNY/H2vhBuBw6gOC+8hLhLtXOvTO"+
			"OO4Sbr/KRbHO9GX80S0R0Z6mEdBvnrB9insXwmXpjsTnAkEA1a/OCCiitUGODJD1"+
			"z9LmdCWyWZjhjd7Yf7AnWaXTPUGkzqRyjUOBSH2Jpq7giBFkS9NSlkYhD0T4pwJX"+
			"eeRCVQJAG0n59gGZqgcfoaahG8xaf8xnBvGdn55hyR5SQ9fSpJWct4Emj/ORrabF"+
			"28NSDUtfkvLPVJBRDCePVYBDNePpbwJBAMdUna7CqTTHmAhk2Mgqhcrmtz4Y/J5o"+
			"uDA0VCCuLSTBLUmN4UWhKOJEKLmR++UxxAtvb2S6unAR+Jlh99O+d5kCQGAOO641"+
			"cZEvR+MyFIKvP7QQ1pH2j7XMzMzYGOeIyFMGDJeT+UhC6llUGqF2cUfBM67UxhIA"+
			"ZIkok7/p/gOdZ00=";
	
	// 支付宝的公钥，无需修改该值
	public static String ali_public_key  = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";

	//↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑
	//↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑
	

	// 调试用，创建TXT日志文件夹路径
	public static String log_path = "/home2/logs/alipay";

	// 字符编码格式 目前支持 gbk 或 utf-8
	public static String input_charset = "utf-8";
	
	// 签名方式 不需修改
	public static String sign_type = "RSA";

}
