package com.vcat.common.payment;

import com.alipay.config.AlipayConfig;
import com.alipay.util.AlipaySubmit;
import org.dom4j.*;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ylin on 2015/10/19.
 */
public class AlipayUtils {
    private static Logger logger = LoggerFactory.getLogger(AlipayUtils.class);

    /**
     * 关闭交易接口
     * @param trade_no        支付宝交易号
     * @param out_order_no    商户网站唯一订单号
     */
    public static boolean closeTrade(String trade_no, String out_order_no){
        Map<String, String> params = new HashMap<String, String>();
        params.put("service", "close_trade");
        params.put("partner", AlipayConfig.partner);
        params.put("_input_charset", AlipayConfig.input_charset);
        params.put("trade_no", trade_no);
        params.put("out_order_no", out_order_no);

        //建立请求
        try {
            String returnString = AlipaySubmit.buildRequest("", "", params);
            logger.info("接口返回:"+returnString);
            return getResult(returnString);
        } catch (Exception ex) {
            logger.error("关闭交易异常:"+ex.getMessage(),ex);
        }
        return false;
    }

    private static boolean getResult(String returnString) throws DocumentException {
        Document document = DocumentHelper.parseText(returnString);
        Element rootElement = document.getRootElement();
        Element element = rootElement.element("is_success");
        return "T".equalsIgnoreCase(element.getText());
    }
    public static void main(String[] args){
    	
    	System.out.println(closeTrade("2015110600001000400027040845","0232015110616000032"));
    }
}
