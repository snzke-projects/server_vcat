package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.Pager;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.dto.*;
import com.vcat.module.ec.entity.*;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@MyBatisDao
public interface OrderDao extends CrudDao<Order> {
	Integer confirm(Order order);
	Integer completedByRefund(Order order);
	Integer delivery(Order order);
	int countByShopId(Map<String, Object> map);
	/**
	 * 获取店铺订单列表和订单详情
	 * @param map orderType shopId (orderNumber)
	 * @return
	 */
	List<OrderDto> getOrderList(Map<String, Object> map);
	void sellerDelete(String orderId);
	BigDecimal getOrderEarning(String orderId);
	void paymented(@Param("paymentId") String paymentId);
	/**
	 * 获取买家订单列表和订单详情
	 * @param map buyerId orderType (orderId)
	 * @return
	 */
	List<OrderDto> getBuyerOrderList(Map<String, Object> map);
	int countByBuyerId(Map<String, Object> map);
	void cancelOrder(String orderId);
	void buyerDelete(String orderId);
	void checkShipping(@Param("orderId")String orderId);
	//获取店铺的上架奖励
	BigDecimal getShopSaled(@Param("shopId")String shopId, @Param("productId") String productId);
	BigDecimal getProductLoadEarning(String orderId);
	void paymentedByOrderId(@Param("orderId") String orderId, @Param("paymentId") String paymentId);
	List<String> getOrderListBydealTime(int i);
	/**
	 * 获取可自动收货的订单号集合
	 * @return
	 */
	List<Map<String,Object>> getAutoCheckShippingList(Integer dayLimit);
	//获取所有订单的钱，并返回其中一个商品名字
	Map<String, Object> getTotalPriceByPaymentId(String paymentId);
	//根据paymentId查询取消的订单
	List<String> checkAllPayment(String paymentId);
	//获取总收入
	BigDecimal getTotalSaled(String shopId);
	//获取当前支付单的商品数量列表
	List<ProductItemDto> getProductsBypaymentId(String id);
	//获取当前订单的商品数量列表
	List<ProductItemDto> getProductsByOrderId(String orderId);
	List<ProductItemDto> findProductItemList(String orderId);
	//获取当前上架奖励
	LoadEarning getLoadEarningSpe(@Param("productId") String productId, @Param("productSaled")BigDecimal productSaled,
								  @Param("saled")BigDecimal saled);
	List<ShipOrder> findShipOrderList(ShipOrder shipOrder);
	List<CouponOperLog> getCouponByPaymentId(String paymentId);
	//int countCouponProduct(@Param("productItemId")String productItemId, @Param("buyerId")String buyerId,
	//					   @Param("productType") String productType);
	int countGroupBuyProduct(@Param("productItemId")String productItemId, @Param("buyerId")String buyerId,
						   @Param("productType") String productType);
	Integer countPurchasedProduct(@Param("productItemId")String productItemId, @Param("buyerId")String buyerId,
								  @Param("productTypes") List<String> productTypes, @Param("interval") int interval);
	Integer countPurchasedProductWithoutInterval(@Param("productItemId")String productItemId, @Param("buyerId")String buyerId,
												 @Param("productTypes") List<String> productTypes);
	Map<String, Object> getProductLimit(@Param("productItemId")String productItemId);
	void insertOrderAddress(String orderId);
	/**
	 * 根据支付单ID获取订单ID集合
	 * @param paymentId
	 * @return
	 */
	List<String> getOrderIdsByPaymentId(String paymentId);
	/**
	 * 删除订单步骤1 删除订单活动关联
	 * @param orderId
	 */
	void deleteOrderActivity(String orderId);
	/**
	 * 删除订单步骤2 删除订单收货地址
	 * @param orderId
	 */
	void deleteOrderAddress(String orderId);
	/**
	 * 删除订单步骤3 删除订单项
	 * @param orderId
	 */
	void deleteOrderItem(String orderId);
	/**
	 * 删除订单步骤4 删除订单日志
	 * @param orderId
	 */
	void deleteOrderLog(String orderId);
	/**
	 * 删除订单步骤5 删除订单
	 * @param orderId
	 */
	void deleteOrder(String orderId);
	/**
	 * 删除订单步骤6 删除支付单日志
	 * @param paymentId
	 */
	void deletePaymentLog(String paymentId);
	/**
	 * 删除订单步骤7 删除支付单
	 * @param paymentId
	 */
	void deletePayment(String paymentId);
	//通过支付单获取订单类型
	Map<String,Object> getOrderType(String paymentNum);
	/**
	 * 批量确认订单
	 * @param orderIds
	 */
	int batchConfirm(@Param("orderIdArray")String[] orderIds);
	/**
	 * 获取订单评价
	 * @param orderId
	 * @return
	 */
	List<ReviewDto> getReviewProductItemList(String orderId);
	/**
	 * @param orderItemId
	 * @return
	 */
	Map<String,Object> getOrderByItemId(String orderItemId);
	List<Map<String, Object>> getCheckShippingEarningList(@Param("dayLimit")int dayLimit);
	void updateIsFund(String orderId);
	//int countCouponOrderItem(String orderId);
	/**
	 * 获取全额抵扣取消的订单
	 * @param min
	 * @return
	 */
	//List<String> getCouponOrderListBydealTime(int min);
	/**
	 * 更新邮费
	 * @param orderId
	 * @param newFreight
	 */
	void updateFreight(@Param("id")String orderId, @Param("freightPrice")BigDecimal newFreight);
	/**
	 * 全额抵扣更新订单金额
	 * @param orderId
	 * @param itemPrice
	 */
	//void updatePrice(@Param("id")String orderId, @Param("price")BigDecimal itemPrice);
	/**
	 * 根据ID获取订单号
	 * @param idArray
	 * @return
	 */
	List<Order> getOrderNumberList(@Param("idArray")String[]idArray);
	boolean isProductInLimit(String productItemId);

	// 店主购买预售商品支付成功后,修改订单状态
	void updateOrderStatus(Order order);
	// 根据支付单ID查询订单项是否为预售拿货类型
	boolean isSuperReserve(@Param("paymentId")String paymentId);
	List<OrderDto> getOrderInfoByPayment(Payment payment);
	// 预售订单管理
	List<OrderReserveLogDto> getOrderReserveLogs(@Param("shopId")String shopId, @Param("productItemId")String productItemId);
	Boolean checkGroupBuyOrder(@Param("paymentId")String paymentId);
	Map<String,Integer> getMyOrderCount(@Param("customerId")String customerId);

	/********************** 2016.05.16  订单 ***********************/
	List<OrderDto> getSellerOrderList(Map<String, Object> map);
	int countBySellerId(Map<String, Object> map);
	List<ProductItemDto> getOrderItemsInfo(String orderId);
	int countSellerRefundOrder(@Param("customerId")String customerId);
	List<ProductItemDto> getSellerRefundOrderList(@Param("customerId")String customerId, @Param("page")Pager page);
	Map<String,Object> getRefundCount(@Param("orderId")String orderId);
	void extendReceiptDate(@Param("orderId")String orderId);
	Map<String,Object> isGroupBuyProductNormalBuy(@Param("orderItemId")String orderItemId);
	List<Map<String,Object>> getWeixinInfoByPayment(Payment payment);
	void changeOrderAddress(Map<String,Object> map);
}
