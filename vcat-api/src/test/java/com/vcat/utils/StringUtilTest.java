package com.vcat.utils;

import com.vcat.common.utils.StringUtils;
import org.junit.Test;

/**
 * Created by Code.Ai on 16/6/3.
 * Description:
 */
public class StringUtilTest {
    @Test
    public void test1(){
        String productName = StringUtils.StringFilter("【微卤】卤味熟食肉类零食,小吃美食特色下酒菜卤肉吃货特产店排骨");
        System.out.println(productName);
    }
}
