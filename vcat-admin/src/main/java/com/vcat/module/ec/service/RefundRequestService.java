package com.vcat.module.ec.service;

import com.alipay.config.AlipayConfig;
import com.alipay.util.AlipaySubmit;
import com.google.common.collect.Maps;
import com.tencent.common.RandomStringGenerator;
import com.vcat.common.config.Global;
import com.vcat.common.utils.DateUtils;
import com.vcat.common.utils.StringUtils;
import com.vcat.module.core.service.CrudService;
import com.vcat.module.core.service.ServiceException;
import com.vcat.module.ec.dao.RefundDao;
import com.vcat.module.ec.entity.Refund;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 退款单Service
 */
@Service
@Transactional(readOnly = true)
public class RefundRequestService extends CrudService<RefundDao, Refund> {
    @Autowired
    private RefundService refundService;
    private String alipayNotifyUrl = Global.getConfig("alipay.notify.refund.url");    // 回调链接
    private String alipaySellerEmail = Global.getConfig("alipay.seller.email");       // 商户帐号
    private static final String ALIPAY_REFUND_SERVICE_NAME = "refund_fastpay_by_platform_pwd";

    /**
     * 执行支付宝退款
     * @param refund
     */
    @Transactional(readOnly = false)
    public String refundRequestByAlipay(Refund refund) {
        refund = refundService.get(refund);
        //退款当天日期
        String refund_date = DateUtils.getDate("yyyy-MM-dd HH:mm:ss");
        //必填，格式：年[4位]-月[2位]-日[2位] 小时[2位 24小时制]:分[2位]:秒[2位]，如：2007-10-01 13:13:13

        //批次号
        String batch_no = DateUtils.getDate("yyyyMMdd") + RandomStringGenerator.getRandomNumberByLength(10);
        //必填，格式：当天日期[8位]+序列号[3至24位]，如：201008010000001

        //退款笔数
        String batch_num = "1";
        //必填，参数detail_data的值中，“#”字符出现的数量加1，最大支持1000笔（即“#”字符出现的数量999个）

        //退款详细数据
        String detail_data = getAlipayDetailData(refund);
        //必填，具体格式请参见接口技术文档

        //////////////////////////////////////////////////////////////////////////////////

        //把请求参数打包成数组
        Map<String, String> sParaTemp = Maps.newHashMap();
        sParaTemp.put("service", ALIPAY_REFUND_SERVICE_NAME);
        sParaTemp.put("partner", AlipayConfig.partner);
        sParaTemp.put("_input_charset", AlipayConfig.input_charset);
        sParaTemp.put("notify_url", alipayNotifyUrl);
        sParaTemp.put("seller_email", alipaySellerEmail);
        sParaTemp.put("refund_date", refund_date);
        sParaTemp.put("batch_no", batch_no);
        sParaTemp.put("batch_num", batch_num);
        sParaTemp.put("detail_data", detail_data);

        refundService.insertInterfaceDataByAlipay(refund, sParaTemp);

        //建立请求
        return AlipaySubmit.buildRequest(sParaTemp,"get","确认");
    }

    /**
     * 根据退款单获取支付宝退款详细数据
     * @param refund
     * @return
     */
    private String getAlipayDetailData(Refund refund){
        if(null == refund
                || null == refund.getPayment()
                || StringUtils.isBlank(refund.getPayment().getTransactionNo())
                || null == refund.getAmount()
                || StringUtils.isBlank(refund.getReturnReason())){
            throw new ServiceException("支付宝退款参数不完整[refund=" + refund + "]！");
        }
        return refund.getPayment().getTransactionNo() + "^" + refund.getAmount() + "^" + refund.getReturnReason();
    }
}
