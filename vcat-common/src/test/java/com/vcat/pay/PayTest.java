package com.vcat.pay;

import com.tencent.WXPay;
import com.tencent.common.Configure;
import com.tencent.common.Util;
import com.tencent.protocol.pay_protocol.WebPayReqData;
import com.tencent.protocol.pay_protocol.WebPayResData;
import com.vcat.common.utils.DateUtils;
import org.junit.Test;

/**
 * Created by ylin on 2016/4/26.
 */
public class PayTest {
    @Test
    public void wapPay(){
        WXPay wxPay = new WXPay();
        WebPayReqData webPayReqData = null;
        Configure configure = wxPay.getConfigure();
        wxPay.initConfigurationForWap();
        String timeStart = DateUtils.getDate("yyyyMMddHHmmss");
        String timeExpire = DateUtils.getDate(1, "yyyyMMddHHmmss");
        String des = "34234234";
        String attach = "12312312312";
        String orderNum = "adfa12312312312";
        int totalPrice = 1;
        String remoteIp = "127.0.0.1";
        String notifyUrl = "http://www.v-cat.cn/";

        webPayReqData = new WebPayReqData(des, attach, orderNum,
                    totalPrice, "ext", remoteIp, timeStart, timeExpire, "",
                    notifyUrl, "WAP", "", "",configure);
        String resString = "";
        try {
            resString = wxPay.requestWebPayService(webPayReqData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        WebPayResData webPayResData = (WebPayResData) Util
                .getObjectFromXML(resString, WebPayResData.class);

    }
}
