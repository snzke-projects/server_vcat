package com.vcat.standalone;

import com.vcat.ApiApplication;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.common.utils.IdGen;
import com.vcat.common.utils.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

/**
 * Created by ylin on 2016/7/7.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApiApplication.class)
@WebIntegrationTest
public class updateInvitationCode {
    @Autowired
    private UpdateInvitationCodeMapper mapper;

    String[] ignCodes = new String[] {"TvL0vX","iui8d2","0H4NbQ","E9V427","3556OV","RusKyC","12345"};

    @Test
    public void updateCode(){
        List<Map<String,String>> invs = mapper.findIds();
        for(Map<String,String> inv : invs){
            String id = inv.get("id");
            String code = inv.get("code");
            if((StringUtils.isNumeric(code) && code.length()  >=5 )
                    || contain(code)){
                System.out.println("跳过"+code);
                continue;
            }
            String num = IdGen.getRandomNumber(6);
            String o = mapper.findCode(num);
            while(o != null) {
                System.out.println("重新生成code");
                num = IdGen.getRandomNumber(6);
                o = mapper.findCode(num);
            }
            mapper.updateCode(num,id);
        }
    }

    private boolean contain(String code) {
        for (String ign : ignCodes){
            if(ign.equals(code)){
                return true;
            }
        }
        return false;
    }
}

@MyBatisDao
interface UpdateInvitationCodeMapper {
    @Select("select id,code from ec_invitation_code")
    List<Map<String,String>> findIds();

    @Select("select code from ec_invitation_code where code = #{code}")
    String findCode(String code);

    @Select("update ec_invitation_code set code = #{code} where id = #{id}")
    void updateCode(@Param("code") String code, @Param("id")String id);
}
