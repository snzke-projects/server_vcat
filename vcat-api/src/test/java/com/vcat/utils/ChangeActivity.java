package com.vcat.utils;

import com.vcat.ApiApplication;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.dto.ShopInfoDto;
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
 * Created by Code.Ai on 16/5/3.
 * Description:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApiApplication.class)
@WebIntegrationTest
public class ChangeActivity {
    @Autowired
    private ChangeActivityMapper changeActivityMapper;

    @Test
    public void fixBug(){
        List<ShopInfoDto> list = changeActivityMapper.findList();
        for (ShopInfoDto s : list){
            if(changeActivityMapper.findStatus(s.getShopId(),s.getProductId())){
                changeActivityMapper.updateStatus(s.getShopId(),s.getProductId(),1);
            }else
                changeActivityMapper.updateStatus(s.getShopId(),s.getProductId(),0);

        }

    }
}

@MyBatisDao
interface ChangeActivityMapper {
    // 查询ec_shop_info表中所有is_activity 为null的数据
    @Select("select id as id, shop_id as shopId, product_id as productId from ec_shop_info where is_activate is null")
    List<ShopInfoDto> findList();

    // 根据shopId productId 查询order的支付状态
    @Select("SELECT sum(eo.payment_status)\n" +
            "FROM ec_order AS eo\n" +
            "left join ec_order_item  as eoi on eo.id = eoi.order_id\n" +
            "left join ec_product as ep on eoi.product_id = ep.id\n" +
            "where eoi.shop_id = #{shopId}\n" +
            "and ep.id = #{productId}\n" +
            "and eo.payment_id is not null")
    Boolean findStatus(@Param("shopId")String shopId,@Param("productId")String productId);

    @Select("update ec_shop_info SET is_activate = #{status}\n" +
            "where shop_id = #{shopId}\n" +
            "AND product_id = #{productId}")
    void updateStatus(@Param("shopId")String shopId,@Param("productId")String productId,@Param("status")int status);
}