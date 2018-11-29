package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.entity.*;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

@MyBatisDao
public interface OrderDao extends CrudDao<Order> {
    Integer confirm(Order order);
	Integer completedByRefund(Order order);
    Integer delivery(Order order);
	BigDecimal getOrderEarning(String orderId);
    List<ShipOrder> findShipOrderList(ShipOrder shipOrder);
    /**
     * 批量确认订单
     * @param orderIds
     */
    int batchConfirm(@Param("orderIdArray")String[] orderIds);

    /**
     * 根据预售订单项完成订单
     * @param source
     * @return
     */
    Integer completedByReserve(Order source);

    /**
     * 根据虚拟商品订单项完成订单
     * @param source
     */
    Integer completedOrder(Order source);

    /**
     * 获取待结算分红的店铺集合
     * @return
     */
    List<Map<String,Object>> getPendingSettlementShopList();

    /**
     * 更新订单分红结算标识
     * @param orderIdSet
     */
    void settlementTeamBonusOrder(@Param(value = "orderIdSet") Set<String> orderIdSet);

    /**
     * 修改订单收货地址
     * @param address
     */
    void modifyAddress(Address address);

    /**
     * 查询庄园订单
     * @param farmOrder
     * @return
     */
    List<FarmOrder> findFarmOrderList(FarmOrder farmOrder);

    /**
     * 根据发货单查询订单
     * @param invoices
     * @return
     */
    Order getOrderByInvoices(Invoices invoices);

    /**
     * 更新订单佣金处理标识
     * @param id
     */
    void updateIsFund(String id);

    /**
     * 检查订单是否已发货
     * @param order
     * @return
     */
    boolean isToBeShipping(Order order);

    /**
     * 修改订单备注
     * @param order
     */
    void updateNote(Order order);
}
