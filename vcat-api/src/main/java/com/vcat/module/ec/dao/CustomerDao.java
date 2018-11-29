package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.Customer;
import com.vcat.module.ec.entity.Shop;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@MyBatisDao
public interface CustomerDao extends CrudDao<Customer> {
    List<String> findCustomerPushToken(Map<String, Object> param);

    String getToken(String id);

    /**
     * 获取该店铺的所有队员
     * @param shop
     * @return
     */
    List<Map<String,Object>> findTeam(Shop shop);
    String getBuyerByPaymentId(String paymentId);

    List<Customer> getSellerWithPhone();
    Customer getCustomerByPhone(@Param("customer")Customer customer);
    Customer getCustomerById(String id);
    Customer getCustomerByPhoneNumber(@Param("phoneNumber")String phoneNumber);
    /**
     * 设置推荐
     * @param shop
     */
    void setRecommend(Shop shop);

    /**
     * 删除推荐店铺设置
     * @param shop
     */
    void clearRecommend(Shop shop);

    /**
     * 更新店铺推荐文章
     * @param shop
     */
    void updateRecommend(Shop shop);

    /**
     * 保存推荐店铺排序
     * @param shopId
     * @param recommendOrder
     */
    void saveRecommendOrder(@Param(value = "id")String shopId,@Param(value = "recommendOrder")String recommendOrder);

    /**
     * 获取随机用户ID
     * @param customer
     * @return
     */
    String getRandomId(Customer customer);

    List<Map<String, Object>> getAvatars(String keyword);
    void updateAvatarById(@Param("avatar_url") String avatarUrl, @Param("cid") String cid);

    //boolean isExsitCustomer(Customer customer);


}
