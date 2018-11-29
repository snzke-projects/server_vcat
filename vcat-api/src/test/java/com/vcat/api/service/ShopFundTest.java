package com.vcat.api.service;

import com.vcat.ApiApplication;
import com.vcat.common.constant.ApiConstants;
import com.vcat.common.constant.ApiMsgConstants;
import com.vcat.common.utils.StringUtils;
import com.vcat.module.ec.entity.CouponFund;
import com.vcat.module.ec.entity.Shop;
import com.vcat.module.ec.entity.ShopFund;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Code.Ai on 16/6/19.
 * Description:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApiApplication.class)
@WebIntegrationTest
public class ShopFundTest {
    @Autowired
    private ShopFundService shopFundService;
    @Autowired
    private CouponFundService couponFundService;
    @Test
    public void test(){
        String   shopId = "";
        ShopFund fund   = shopFundService.getShopFund(shopId);
        // 构建返回对象
        Map<String, Object> map = new HashMap<>();
        map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
        map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
        if (fund == null) {
            map.put("fundInfo", new ShopFund(new BigDecimal(0)));

        }
        // 计算总收入、本月总收入
        if (fund.getTotalSale() == null) {
            fund.setTotalSale(new BigDecimal(0));
        }
        if (fund.getTotalLoadAward() == null) {
            fund.setTotalLoadAward(new BigDecimal(0));
        }
        //计算总收入
        fund.countTotalFund();
        //月总收入
        fund.countMonthTotalFund();
        //计算总可用收入
        fund.countTotalAvailableFund();
        //计算总已提现收入
        fund.countTotalUseDFund();
        //计算总提现中收入
        fund.countTotalProcessingFund();
        //获取购物卷
        Shop shop = new Shop();
        shop.setId(shopId);
        CouponFund cf = new CouponFund();
        cf.setShop(shop);
        CouponFund couponFund  = couponFundService.get(cf);
        if (couponFund != null) {
            fund.setCouponUsedFund(couponFund.getUsedFund());
            fund.setCouponAvailableFund(couponFund.getAvailableFund());
            fund.setCouponTotalFund(couponFund.getUsedFund().add(
                    couponFund.getAvailableFund()));
        }
        map.put("fundInfo", fund);
        System.out.println(fund.toString());
    }
}
