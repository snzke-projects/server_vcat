package com.vcat.api.web.v2;

import com.vcat.api.service.CustomerService;
import com.vcat.api.service.RedisService;
import com.vcat.api.service.ShopService;
import com.vcat.api.web.validation.ValidateParams;
import com.vcat.common.constant.ApiConstants;
import com.vcat.common.constant.ApiMsgConstants;
import com.vcat.common.utils.StringUtils;
import com.vcat.common.version.ApiVersion;
import com.vcat.module.ec.entity.Customer;
import com.vcat.module.ec.entity.Shop;
import com.vcat.module.json.customer.UpToSellerRequest;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@ApiVersion(2)
public class CustomerController2 {
    @Autowired
    private RedisService    redisService;
    @Autowired
    private ShopService     shopService;
    @Autowired
    private CustomerService customerService;
    /**
     * 买家升级为卖家接口
     * 给买家创建店铺
     * 修改用户名
     * 用户电话
     * 密码
     * parentShopId
     * @return
     */
    @ValidateParams
    @RequestMapping(value = "/anon/upGradeToSeller",method = RequestMethod.POST)
    @ResponseBody
    public Object upGradeToSeller(@Valid @RequestBody UpToSellerRequest params){
        String   phoneNum     = params.getPhoneNum();
        String   passWord     = params.getPassWord();
        String   parentShopId = params.getParentShopId();
        Customer customer     = new Customer();
        customer.setPhoneNumber(phoneNum);
        customer.setPassword(passWord);
        Shop parentShop = new Shop();
        parentShop.setId(parentShopId);
        parentShop = shopService.get(parentShop);
        customer = customerService.saveRoleSeller(customer,parentShop);

        String token = StringUtils.produceSellerToken(customer.getId());
        redisService.putAccessToken(customer.getId(), token);
        //生成买家token
        String buyerToken = StringUtils.produceBuyerToken(customer.getId());
        buyerToken = redisService.putBuyerToken(customer.getId(), buyerToken);
        // 查询小店的信息，并返回
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
        map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
        map.put("token", token);
        map.put("huanid",StringUtils.getCustomerIdByToken(token));
        map.put("huanId",StringUtils.getCustomerIdByToken(token));
        map.put("buyerToken", buyerToken);
        map.put("status", ApiMsgConstants.SUCCESS_CODE_SUB);
        //添加客户级别
        Shop shop = shopService.get(customer.getId());
        map.put("isVIP",shop.getAdvancedShop());
        return map;
    }
}
