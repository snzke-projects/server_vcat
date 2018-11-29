package com.vcat.api.web.v2;

import com.vcat.api.service.MessageService;
import com.vcat.api.service.OrderService;
import com.vcat.common.constant.ApiConstants;
import com.vcat.common.constant.ApiMsgConstants;
import com.vcat.common.persistence.Pager;
import com.vcat.common.utils.StringUtils;
import com.vcat.common.version.ApiVersion;
import com.vcat.common.web.rest.RestBaseController;
import com.vcat.module.core.entity.MsgEntity;
import com.vcat.module.ec.dto.OrderDto;
import com.vcat.module.ec.dto.ProductItemDto;
import com.vcat.module.ec.entity.Product;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.*;

/**
 * 消息接口
 * @author zfc
 *2015年5月28日 11:20:54
 */
@RestController
public class MessageController2 extends RestBaseController {
	@Autowired
	private MessageService messageService;
	@Autowired
	private OrderService   orderService;
	/**
	 * 当点击"消息"时,获取店铺最新5条消息
	 * @param token
	 * @return
	 */
	@ApiVersion(2)
	@RequiresRoles("seller")
	@RequestMapping("/api/getNewestMessage")
	public Object getNewestMessage(@RequestHeader(value = "token", defaultValue = "") String token) {
		if (StringUtils.isEmpty(token)) {
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG, ApiMsgConstants.FAILED_CODE);
		}
		// shopId和customerId一样
		String shopId = StringUtils.getCustomerIdByToken(token);
		try {
			Map<String, Object> map = messageService.getNewestMessage(shopId);
			if(map != null && !map.isEmpty()){
				map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
				map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
				if(map.get("remindTitle") == null){
                    map.put("remindTitle","还没有个人消息！");
                }
			}
			return map;
		} catch (Exception e) {
			logger.error("getNewestMessage ERROR:"+e.getMessage(),e);
			return new MsgEntity("获取店铺最新消息失败！", ApiMsgConstants.FAILED_CODE);
		}
	}

	/**
	 * 获取消息列表
	 * @param token
	 * @param messageType
	 * 1:V猫新动态
	 * 2:订单消息
	 * 3:财富消息
	 * 4:分享奖励消息
	 * 5.个人消息
	 * @param pageNo
	 * @return
	 */
	@ApiVersion(2)
	@RequiresRoles("seller")
	@RequestMapping("/api/message/getMessageList")
	public Object getMessageList(
			@RequestHeader(value = "token", defaultValue = "") String token,
			@RequestParam(value = "messageType", defaultValue = "") String messageType,
			@RequestParam(value = "pageNo", defaultValue = "1") String pageNo) {
		if (StringUtils.isEmpty(messageType)) {
			logger.debug("request params is illegal ");
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,	ApiMsgConstants.FAILED_CODE);
		}
		String shopId = StringUtils.getCustomerIdByToken(token);
		Pager page = new Pager();
		page.setPageNo(Integer.parseInt(pageNo));
		page.setPageSize(ApiConstants.DEFAULT_PAGE_SIZE);
		try {
			page = messageService.findPage(page, shopId, messageType, 2);
		} catch (Exception e) {
			logger.error("getMessageList ERROR:"+e.getMessage(),e);
			return new MsgEntity("获取消息列表失败！", ApiMsgConstants.FAILED_CODE);
		}
		Map<String, Object> map = new HashMap<>();
		map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
		map.put("page", page);
		map.put("list", page.getList());
		page.setList(null);
		return map;
	}

	/**
	 * 获取订单详情（卖家）
	 * @param token 令牌
	 * @param orderId 订单Id或订单Num任选一个即可
	 * @param orderNumber 订单Id或订单Num任选一个即可
	 * @return 返回正确(错误)代码,订单详情map
	 */
    // 未显示商品
	@ApiVersion(2)
	@RequiresRoles("seller")
	@RequestMapping("/api/message/getOrderInfo")
	public Object getOrderInfo(
			@RequestHeader(value = "token", defaultValue = "") String token,
			@RequestParam(value = "orderId", defaultValue = "", required = false) String orderId,
			@RequestParam(value = "orderNumber", defaultValue = "", required = false) String orderNumber) {
		if (StringUtils.isBlank(token) ) {
			return new MsgEntity(ApiMsgConstants.TOKEN_ILLEGAL,
					ApiMsgConstants.FAILED_CODE);
		}
		if (StringUtils.isBlank(orderId) && StringUtils.isBlank(orderNumber)) {
			return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
					ApiMsgConstants.FAILED_CODE);
		}
		Map<String, Object> map = new HashMap<>();
		OrderDto order = new OrderDto();
		order.setId(orderId);
		order.setOrderNum(orderNumber);
		try {
			map.put("order",orderService.getOrder(order,StringUtils.getCustomerIdByToken(token)));
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return new MsgEntity("获取订单详情失败：" + e.getMessage(), ApiMsgConstants.FAILED_CODE);
		}
		map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
		return map;
	}

	/**
	 * 获取"我的客户订单" 别的客户在我的小店购买的商品订单
	 * 状态:未付款,待处理,已完成,已关闭,退款
     * 需求:C 用户在A 和 B 店铺中购买同一供货商不同商品,则会生成一个订单,在A 客户订单中只能显示 C 在A 店铺中购买的订单
	 * @param token
	 * @param pageNo
	 * @param orderType
	 * @return
	 */
    @ApiVersion(2)
	@RequiresRoles("seller")
	@RequestMapping("/api/getMyCustomerOrderList")
	public Object getMyCustomerOrderList(@RequestHeader(value = "token", defaultValue = "") String token,
										 @RequestParam(value = "pageNo", defaultValue = "1") int pageNo,
										 @RequestParam(value = "orderType", defaultValue = "1") int orderType) {
		String shopId = StringUtils.getCustomerIdByToken(token);
		int count = orderService.countByShopId(shopId,orderType);
		// 组装分页信息
		Pager page = new Pager();
		page.setPageNo(pageNo);
		page.setPageSize(ApiConstants.DEFAULT_PAGE_SIZE);
		page.setRowCount(count);
		page.doPage();
		List<OrderDto> list = orderService.getOrderList(shopId,page,orderType);

        //删除同一个订单中不是本商店的订单项
        for (OrderDto orderDto : list) {
            Iterator<ProductItemDto> productItemDtoIterator = orderDto.getProductItems().iterator();
            while (productItemDtoIterator.hasNext()) {
                ProductItemDto productItemDto = productItemDtoIterator.next();
                if (productItemDto.getShopId() == null || !productItemDto.getShopId().equals(shopId)) {
                    productItemDtoIterator.remove();
                }
            }
        }
		Map<String, Object> map = new HashMap<>();
		map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
		map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
		map.put("list", list);
		map.put("page", page);
		map.put("orderType", orderType);
		return map;
	}

	/**
	 * 获取我的客户订单详情
	 * @param token
	 * @param orderId
	 * @param orderNumber
     * @return
     */
    @ApiVersion(2)
    @RequiresRoles("seller")
    @RequestMapping("/api/getMyCustomerOrderInfo") //todo 修改 ios 调用接口
    public Object getMyCustomerOrderInfo(
            @RequestHeader(value = "token", defaultValue = "") String token,
            @RequestParam(value = "orderId", defaultValue = "", required = false) String orderId,
            @RequestParam(value = "orderNumber", defaultValue = "", required = false) String orderNumber) {
        if (StringUtils.isBlank(orderId) && StringUtils.isBlank(orderNumber)) {
            return new MsgEntity(ApiMsgConstants.REQUEST_ERROR_MSG,
                    ApiMsgConstants.FAILED_CODE);
        }
        if (StringUtils.isBlank(token) ) {
            return new MsgEntity(ApiMsgConstants.TOKEN_ILLEGAL,
                    ApiMsgConstants.FAILED_CODE);
        }
        Map<String, Object> map = new HashMap<>();
        OrderDto order = new OrderDto();
        order.setId(orderId);
        order.setOrderNum(orderNumber);
        try {
            order = orderService.getOrder(order,StringUtils.getCustomerIdByToken(token));
            //商品总件数
            int totalProductNum = order.getTotalProductNum();
            //商品总价格
            BigDecimal totalPrice = order.getTotalPrice();
            Iterator<ProductItemDto> productItemDtoIterator = order.getProductItems().iterator();
            List<ProductItemDto> removeProductItemDtoList = new ArrayList<>();
            //删除同一个订单中不是本商店的订单项
            while (productItemDtoIterator.hasNext()) {
                ProductItemDto productItemDto = productItemDtoIterator.next();
                if (!productItemDto.getShopId().equals(StringUtils.getCustomerIdByToken(token))) {
                    //得到被删除的商品总数
                    totalProductNum -= productItemDto.getQuantity();
                    //计算被删除的商品总价
                    //              原来的总价  减去               商品单价            乘以                  商品总数
                    totalPrice = totalPrice.subtract(productItemDto.getItemPrice().multiply(BigDecimal.valueOf(productItemDto.getQuantity())));
                    removeProductItemDtoList.add(productItemDto);
                    productItemDtoIterator.remove();
                }
            }
            //删除后修改商品总件数
            order.setTotalProductNum(totalProductNum);
            //删除后修改商品总价
            order.setTotalPrice(totalPrice);
            map.put("order",order);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return new MsgEntity("获取订单详情失败：" + e.getMessage(), ApiMsgConstants.FAILED_CODE);
        }
        map.put(ApiConstants.CODE, ApiMsgConstants.SUCCESS_CODE);
        map.put(ApiConstants.MSG, ApiMsgConstants.SUCCESS_MSG);
        return map;
    }
}
