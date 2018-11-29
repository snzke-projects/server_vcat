package com.vcat.utils;

import com.vcat.ApiApplication;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.Shop;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * Created by ylin on 2016/4/29.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApiApplication.class)
@WebIntegrationTest
public class SQLTest {
    @Autowired
    private SQLMapper sqlMapper;

    @Test
    public void bugTest() {
        String paymentId = "b8f57a7a6c8c4b62a7f3c4f531059af8";
        List<Shop> shops = sqlMapper.find(paymentId);
        if (shops != null && !shops.isEmpty()) {
            Shop shop1 = shops.get(0);
            for (Shop shop : shops) {
                System.out.println(shop);
            }
        }
    }
}

@MyBatisDao
interface SQLMapper {
    @Select("select es.id as \"id\",es.name as \"name\", es.advanced_shop as \"isAdvance\n" +
            "dShop\" from ec_order as eo left join ec_order_item as eoi on eo.id = eoi.order_id left join ec_shop as es ON eoi.shop_id = es.id where eo.payment_id = #{paymentId} and es.id is not null")
    List<Shop> find(@Param("paymentId") String paymentId);
}


