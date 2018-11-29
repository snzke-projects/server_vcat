package com.vcat.utils;

import com.vcat.ApiApplication;
import com.vcat.common.persistence.annotation.MyBatisDao;
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
 * Created by ylin on 2016/4/26.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApiApplication.class)
@WebIntegrationTest
public class DuplicateTest {
    @Autowired
    private DuplicateMapper duplicateMapper;

    @Test
    public void fixBug(){
        List<String> shopIds = duplicateMapper.findShopId();
        for(String id : shopIds){
            String o = duplicateMapper.findDupByShop(id);
            if(o != null){
                duplicateMapper.delDup(o);
            }
        }
    }
}

@MyBatisDao
interface DuplicateMapper {
    @Select("select id from ec_shop_product where shop_id = #{shopId}  group by product_id having count(product_id) > 1")
    String findDupByShop(@Param("shopId") String shopId);

    @Select("delete from ec_shop_product where id = #{id}")
    void delDup(String id);

    @Select("select id from ec_shop where shop_num is not null and id in (select shop_id from ec_shop_product)")
    List<String> findShopId();
}


