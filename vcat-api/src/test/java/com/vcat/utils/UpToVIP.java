package com.vcat.utils;

import com.vcat.ApiApplication;
import com.vcat.common.persistence.annotation.MyBatisDao;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by Code.Ai on 16/4/27.
 * Description: 手动升级为白金小店
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApiApplication.class)
@WebIntegrationTest
public class UpToVIP {
    @Autowired
    private UpToVIPMapper upToVIPMapper;
    @Test
    public void toVIP(){
        String shopId = upToVIPMapper.findShopByPhone("13880741516");
        //String phones = "13880611079,15182212030,18740584616,13990381036,15928199158,18577992529,13628041955,15196226350,18228062545,18328159562,13550525137,18683955117,15838322710,13416668989,18602106148,18140465530,15996919423,18349221187,13718859192,13044888819,18625133603,13875985343,15910271345,15181856352,13880109561,13357571535,18772421244,18320429694,13518144358";
        //String[] phs = phones.split(",");
        //String shopId = "";
        //for(String phone: phs){
        //    shopId = upToVIPMapper.findShopByPhone(phone);
        //    System.out.println(shopId);
        //    String invitationId = upToVIPMapper.findInvitationCode();
        //    System.out.println(invitationId);
        //    upToVIPMapper.updateVIP(shopId,invitationId);
        //}
        //String shopId = upToVIPMapper.findShopByShopNum("24343");
        System.out.println(shopId);
        String invitationId = upToVIPMapper.findInvitationCode();
        System.out.println(invitationId);
        upToVIPMapper.updateVIP(shopId,invitationId);
    }
}


@MyBatisDao
interface UpToVIPMapper {
    @Select("select id from ec_shop where shop_num = #{shopNum}")
    String findShopByShopNum(@Param("shopNum") String shopNum);

    @Select("select id from ec_customer where phone_number = #{phoneNum}")
    String findShopByPhone(@Param("phoneNum") String phoneNum);

    @Update("update ec_shop as es, ec_invitation_code as eic\n" +
            "set\n" +
            "  es.advanced_shop = 1,\n" +
            "  es.my_invitation_code_id = #{invitationId},\n" +
            "  eic.status = 1\n" +
            "where es.id = #{shopId}\n" +
            "and eic.id = #{invitationId}")
    void updateVIP(@Param("shopId")String shopId, @Param("invitationId")String invitationId);

    @Select("select id from ec_invitation_code\n" +
            "where status = 0\n" +
            "limit 1")
    String findInvitationCode();
}
