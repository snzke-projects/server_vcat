package com.vcat.common;

import com.vcat.common.payment.AlipayUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ylin on 2015/10/19.
 */
public class AlipayUtilsTest {
    @Test
    public void closeTradeTest(){
        Assert.assertFalse(AlipayUtils.closeTrade("2015101600001000190064183527", "8102015101620555562"));
    }
}
