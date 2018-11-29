package com.vcat.module.ec.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.vcat.module.ec.dto.*;
import org.apache.ibatis.annotations.Param;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.Pager;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.Rank;
import com.vcat.module.ec.entity.Shop;
import sun.management.Agent;

@MyBatisDao
public interface ShopDao extends CrudDao<Shop> {
	//计算数量
	Integer count();
	//获取前100的月排行
	List<Rank> getMonthRankList();
	
	void insertProducts(String id);
	void insertBgImages(String shopId);
	int countBuyerList(Map<String, Object> map);
	List<BuyerInfoDto> getBuyerList(Map<String, Object> map);
	//通过销售额更新等级
	void updateLevel(@Param("shopId") String shopId,@Param("saledWith") int saledWith);
	
	int countChildsList(String shopId);
	
	List<ChildCustomerDto> getChildList(@Param("shopId")String shopId, @Param("page")Pager page);

	List<String> getFriendsById(String pid);
	void updateParentId(Shop shop);
    void setParentToNull(String id);
	Rank getRankByPhone(String phoneNum);
	Shop getShopByInviteCode(@Param("inviteCode") String inviteCode);
	String getInviteCode(@Param("id")String id);
	List<Map<String,Object>> getAllShopByPayment(@Param("paymentId")String paymentId);

    List<Shop> getAllShopByParentId(@Param("parentId")String parentId);
	Map<String,String> getProductItemsByPayment(@Param("paymentId")String paymentId);
	ProductItemDto getReserveProductItemByPayment(@Param("paymentId")String paymentId);
    Map<String,String> getReserveProductItemByItemId(@Param("orderItemId")String orderItemId);
	List<Map<String,Object>> getBuyersByPaymentId(@Param("paymentId")String paymentId, @Param("upgradeConditionId")String upgradeConditionId);
    Map<String,Object> isSpecialProduct(@Param("paymentId")String paymentId);
    Boolean isReach(@Param("shopId")String shopId, @Param("paymentDate")Date paymentDate);
    List<Map<String,Object>> getTotlePriceByRule(@Param("buyerId")String buyerId, @Param("paymentDate")Date paymentDate);
    Boolean addInvitation(@Param("superCustomerId")String superCustomerId, @Param("friendCustomerId")String friendCustomerId);
    List<Shop> getOtherShop();
    Boolean updateToVIP(Shop VIPShop);

	void updateAdvanced(@Param("shopId")String shopId,@Param("type")int type);
	Boolean isVipShop(@Param("orderId")String orderId);
	BigDecimal getTotalSale(@Param("shopId")String shopId);

	List<String> countChildsIsVIP(Map<String, Object> params);
	List<AgentShopDto> getChildListIsVIP(@Param("params") Map<String, Object> params, @Param("page") Pager page);
	List<String> countChildsNotVIP(Map<String, Object> params);
	List<AgentShopDto> getChildListNotVIP(@Param("params") Map<String, Object> params, @Param("page") Pager page);
	List<String> countGrandChilds(Map<String, Object> params);
	List<AgentShopDto> getGrandChildList(@Param("params") Map<String, Object> params, @Param("page") Pager page);
	AgentShopDto getVIPInfo(@Param("shopId") String shopId);
	AgentShopDto getNotVIPInfo(@Param("shopId") String shopId);
	AgentShopDto getGrandVIPInfo(@Param("shopId") String shopId);
	int getShopStatus(@Param("shopId") String shopId);
	AgentShopDto getShopInfo(@Param("shopId") String shopId);
}
