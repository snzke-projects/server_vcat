package com.vcat.common;

import com.vcat.common.thirdparty.WeixinClient;
import org.junit.Test;

/**
 * Created by ylin on 2016/6/14.
 */
public class WeixinTest {
    @Test
    public void getOpenId(){
        String ret = WeixinClient.getOpenId("041FENna23MmuB0Gg0pa2DcIna2FENna");
        System.out.println(ret);
    }

    /**
     *
     * {"access_token":"Ec7E8RT-VD9GuX6BZKl33se2xK97lcfRJkp_K_ptEFyHa8rl19grToFA8DbpEegt1a7nle-BDo9Vl9mY39F9omlkcTgf1quCON92q4Me2tk",
     * "expires_in":7200,
     * "refresh_token":"-Z_ckesLEu8KN029rH-EPjlFM97Ex663Mx-d6UZyxPVE6cD38ErO0jTR9p0QYKDXvPzuyAoCzNRm4mv2ITKiQTOulFyZj1Kiv_ahpemeX9Y",
     * "openid":"ov4RDswMFhNIsf9A_b3pIIeLaGiw",
     * "scope":"snsapi_userinfo",
     * "unionid":"oOrJMwM8hB21kzPj0lItvMubnsts"}
     *
     *
     *
     *
     *
     * {"openid":"ov4RDswMFhNIsf9A_b3pIIeLaGiw","nickname":"lin","sex":1,"language":"zh_CN","city":"成都","province":"四川","country":"中国","headimgurl":"http:\/\/wx.qlogo.cn\/mmopen\/ajNVdqHZLLDcSf4hsTiclWyMibYF32HOP4LtFyt5VeewozCwjjrxT5dPibWw5lu1icLia2qTYIKPrSKibORFpsJqic8dg\/0","privilege":[],"unionid":"oOrJMwM8hB21kzPj0lItvMubnsts"}
     */
    @Test
    public void getUserInfo(){
        String ret = WeixinClient.getUserInfo("Ec7E8RT-VD9GuX6BZKl33se2xK97lcfRJkp_K_ptEFyHa8rl19grToFA8DbpEegt1a7nle-BDo9Vl9mY39F9omlkcTgf1quCON92q4Me2tk",
                "ov4RDswMFhNIsf9A_b3pIIeLaGiw");
        System.out.println(ret);
    }

    @Test
    public void getAccessTokenTest(){
        String ret = WeixinClient.getAccessToken();
        System.out.println(ret);
    }

    @Test
    public void sendOrderSucessMsgTest(){
        String ret = WeixinClient.sendOrderSuccessMsg("ov4RDswMFhNIsf9A_b3pIIeLaGiw","芒果","3e358feb25af4b92aceecd15c0a9307d","33.34");
        System.out.println(ret);
    }

    @Test
    public void sendGroupBuyMsgTest(){
        String ret = WeixinClient.sendGroupBuySuccessMsg("ov4RDswMFhNIsf9A_b3pIIeLaGiw","芒果","3e358feb25af4b92aceecd15c0a9307d","2312312312312","1231");
        System.out.println(ret);
    }
}
