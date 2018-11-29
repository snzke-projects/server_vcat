package com.vcat.module.ec.web;

import com.vcat.common.beanvalidator.BeanValidators;
import com.vcat.common.easyui.DataGrid;
import com.vcat.common.persistence.Page;
import com.vcat.common.utils.DateUtils;
import com.vcat.common.utils.StringUtils;
import com.vcat.common.web.BaseController;
import com.vcat.kuaidi100.pojo.NoticeRequest;
import com.vcat.module.core.utils.UserUtils;
import com.vcat.module.core.utils.excel.ExportExcel;
import com.vcat.module.core.utils.excel.ImportExcel;
import com.vcat.module.ec.entity.*;
import com.vcat.module.ec.service.*;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import java.util.List;

/**
 * 订单Controller
 */
@Controller
@RequestMapping(value = "${adminPath}/ec/order")
public class OrderController extends BaseController {
    private final String listOrder = "listOrder";

    @Autowired
   	private OrderService orderService;

    @Autowired
    private OrderItemService orderItemService;

	@Autowired
	private ExpressService expressService;

	@Autowired
	private RefundService refundService;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private DistributionService distributionService;

    @Autowired
    private OrderReserveService orderReserveService;

    @Autowired
    private OrderClearInventoryService orderClearInventoryService;

	@RequestMapping(value = "list")
	public String list(Order order, HttpServletRequest request, Model model) {
        String paymentStatus = order.getPaymentStatus();
        String orderStatus = order.getOrderStatus();
        String shippingStatus = order.getShippingStatus();
        String type = order.getSqlMap().get("type");
        if(StringUtils.isBlank(type)){
            order.setPaymentStatus("1");
            order.setOrderStatus("0");
            order.setShippingStatus("0");
            order.getSqlMap().put("type","toHandler");
        }

        if("1".equals(request.getParameter("useHisOrderParam"))){
            order = (Order)request.getSession().getAttribute(listOrder);
            String paramType = order.getSqlMap().get("type");
            if(StringUtils.isNotBlank(type) && StringUtils.isNotBlank(paramType) && !type.equals(paramType)){
                order.getSqlMap().put("type", type);
            }
            if(StringUtils.isBlank(type) && StringUtils.isNotBlank(paramType) && !paramType.equals(type)){
                order.getSqlMap().put("type", paramType);
            }
            if(StringUtils.isNotBlank(paymentStatus) && !paymentStatus.equals(order.getPaymentStatus())){
                order.setPaymentStatus(paymentStatus);
            }
            if(StringUtils.isNotBlank(orderStatus) && !orderStatus.equals(order.getOrderStatus())){
                order.setOrderStatus(orderStatus);
            }
            if(StringUtils.isNotBlank(shippingStatus) && !shippingStatus.equals(order.getShippingStatus())){
                order.setShippingStatus(shippingStatus);
            }
        }
        model.addAttribute("useHisOrderParam", request.getParameter("useHisOrderParam"));
        model.addAttribute("pageNo", request.getParameter("pageNo"));
        model.addAttribute("pageSize", request.getParameter("pageSize"));
        model.addAttribute("order",order);
        model.addAttribute("distributionList",distributionService.findList(new Distribution()));
        model.addAttribute("supplierList",supplierService.findList(new Supplier()));
		return "module/ec/order/list";
	}

    @ResponseBody
    @RequestMapping(value = "listData")
    public DataGrid listData(Order order, HttpServletRequest request, HttpServletResponse response) {
        if("1".equals(request.getParameter("useHisOrderParam"))){
            order = (Order)request.getSession().getAttribute(listOrder);
        }
        request.getSession().setAttribute(listOrder,order);

        return DataGrid.parse(orderService.findPage(new Page<>(request, response), order));
    }

    @RequestMapping(value = "exportOrderList")
    public String exportCustomerList(Order order, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        try {
            Page<Order> page = orderService.findPage(new Page<>(request, response, -1), order);

            String fileName = "订单列表.xlsx";
            new ExportExcel("订单列表", Order.class).setDataList(page.getList()).write(response, fileName).dispose();
            return null;
        } catch (Exception e) {
            addMessage(redirectAttributes, "导出失败！失败信息："+e.getMessage());
        }
        return "redirect:" + adminPath + "/ec/order/list";
    }

    @RequestMapping(value = "shipList")
    public String shipList(ShipOrder shipOrder, Model model) {
        model.addAttribute("shipOrder",shipOrder);
        model.addAttribute("distributionList",distributionService.findList(new Distribution()));
        return "module/ec/order/shipList";
    }

    @ResponseBody
    @RequestMapping(value = "getShipData")
    public DataGrid getShipData(ShipOrder shipOrder, HttpServletRequest request, HttpServletResponse response) {
        return DataGrid.parse(orderService.findShipOrderList(new Page<>(request, response), shipOrder));
    }

    @RequiresPermissions("ec:order:shipList:export")
    @RequestMapping(value = "exportShipList", method= RequestMethod.POST)
    public String exportShipList(ShipOrder shipOrder, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        try {
            String orderIds = shipOrder.getSqlMap().get("orderIds");
            if(StringUtils.isNotBlank(orderIds)){
                shipOrder.setOrderIdArray(orderIds.split("\\|"));
            }
            String fileName = shipOrder.getDistributionName()+"待发货列表"+ DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<ShipOrder> page = orderService.findShipOrderList(new Page<>(request, response, -1), shipOrder);
            new ExportExcel(shipOrder.getDistributionName()+"待发货列表", ShipOrder.class).setDataList(page.getList()).write(response, fileName).dispose();
            return null;
        } catch (Exception e) {
            logger.error("导出" + shipOrder.getDistributionName() + "待发货列表失败！失败信息："+e.getMessage(), e);
            addMessage(redirectAttributes, "导出" + shipOrder.getDistributionName() + "待发货列表失败！失败信息："+e.getMessage());
        }
        return "redirect:" + adminPath + "/ec/order/shipList";
    }

	@RequestMapping(value = "form")
	public String form(Order order, boolean isView, boolean banBack, Model model) {
		if(StringUtils.isNotEmpty(order.getId()) || StringUtils.isNotEmpty(order.getOrderNumber())){
			order = orderService.get(order);
		}
		model.addAttribute("expressList", expressService.findList(new Express()));
		model.addAttribute("order", order);
        model.addAttribute("isView", isView);   // 是否为预览页面
        model.addAttribute("banBack", banBack); // 是否禁止返回按钮
		return "module/ec/order/form";
	}

    @RequestMapping(value = "returnList")
    public String returnList(Integer pageNo,Integer pageSize){
        return "redirect:" + adminPath + "/ec/order/list?useHisOrderParam=1&pageNo="+pageNo+"&pageSize="+pageSize;
    }

	@RequiresPermissions("ec:order:confirm")
	@RequestMapping(value = "confirm")
	public String confirm(Order order, @RequestParam(value="from",required = false)String from, RedirectAttributes redirectAttributes) {
		Integer i = orderService.confirm(order,null);
		String msg = 1 == i ? "确认订单'" + order.getOrderNumber() + "'成功" : "确认订单失败，该订单已确认或订单状态不正确";
		addMessage(redirectAttributes, msg);
		from = StringUtils.isEmpty(from) ? "list?type=toHandler&paymentStatus=1&orderStatus=0&shippingStatus=0" : "form?id="+order.getId();
		return "redirect:" + adminPath + "/ec/order/"+from;
	}

    @ResponseBody
    @RequiresPermissions("ec:order:confirm")
    @RequestMapping(value = "batchConfirmOrder")
    public void batchConfirmOrder(@RequestParam(value = "orderId")String[]orderIds) {
        orderService.batchConfirm(orderIds);
    }

	@RequiresPermissions("ec:order:delivery")
	@RequestMapping(value = "delivery")
	public String delivery(Order order, RedirectAttributes redirectAttributes) {
		String msg = 1 == orderService.delivery(order, UserUtils.getUser().getName() + "确认发货成功") ? "确认发货成功" : "确认发货失败，该订单已发货或订单状态不正确";
		addMessage(redirectAttributes, msg);
		return "redirect:" + adminPath + "/ec/order/form?id="+order.getId();
	}

	@RequestMapping(value = "save")
	public String save(Order order, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, order)){
			return form(order, false, false, model);
		}
		orderService.save(order);
		addMessage(redirectAttributes, "保存订单'" + StringUtils.abbr(order.getOrderNumber(),100) + "'成功");
		return "redirect:" + adminPath + "/ec/order/list";
	}

	@ResponseBody
	@RequiresPermissions("ec:order:queryExpress")
	@RequestMapping(value = "queryExpress")
	public NoticeRequest queryExpress(@RequestParam(value="com")String com, @RequestParam(value="number")String number){
		return expressService.queryExpress(com,number);
	}

	@ResponseBody
	@RequestMapping(value = "queryRefund")
	public Refund queryRefund(Refund refund){
		return refundService.get(refund);
	}

	@ResponseBody
	@RequiresPermissions("ec:order:verifyReturn")
	@RequestMapping(value = "verifyReturn")
	public void verifyReturn(Refund refund, @RequestParam(value="shippingStatus" ,required = false)String shippingStatus){
		String result = "驳回退货申请";
		if(Refund.RETURN_STATUS_CHECK_SUCC.equals(refund.getReturnStatus())){
			// 如果订单发货状态为未发货，则直接完成退货流程
			if(Order.SHIPPING_STATUS_TO_BE_SHIPPED.equals(shippingStatus) || (null != refund.getIsReceipt() && refund.getIsReceipt() == 0)){
				refund.setReturnStatus(Refund.RETURN_STATUS_COMPLETED);
			}
			result = "同意退货申请";
		}
		refund.getSqlMap().put("verifyNote",refund.getNote());
		refund.setNote(UserUtils.getUser().getName() + " " + result + " ：" + refund.getNote());
		refundService.verifyReturn(refund);
	}

	@ResponseBody
	@RequiresPermissions("ec:order:confirmReturn")
	@RequestMapping(value = "confirmReturn")
	public void confirmReturn(Refund refund){
		String result = "未收到退货";
		if(Refund.RETURN_STATUS_COMPLETED.equals(refund.getReturnStatus())){
			result = "确认收到退货";
		}
		refund.getSqlMap().put("verifyNote",refund.getNote());
		refund.setNote(UserUtils.getUser().getName() + " " + result + " ：" + refund.getNote());
		refundService.confirmReturn(refund);
	}

    @ResponseBody
    @RequestMapping(value = "getOrder")
    public Order getOrder(String id){
        return orderService.get(id);
    }

    @ResponseBody
    @RequestMapping(value = "updateShipping")
    public void updateShipping(Order order){
        orderService.updateShipping(order);
    }

    @RequiresPermissions("ec:order:confirm")
    @RequestMapping(value = "toHandleReserve")
    public String toHandleReserve(Order order, Model model){
        OrderReserve orderReserve = new OrderReserve();
        orderReserve.setOrderId(order.getId());
        model.addAttribute("orderReserve",orderReserveService.get(orderReserve));
        model.addAttribute("expressList",expressService.findList(new Express()));
        return "module/ec/order/handleReserve";
    }

    @ResponseBody
    @RequestMapping(value = "clearInventoryLogData")
    public DataGrid clearInventoryLogData(Order order, HttpServletRequest request, HttpServletResponse response){
        OrderClearInventory orderClearInventory = new OrderClearInventory();
        orderClearInventory.setOrderId(order.getId());
        return DataGrid.parse(orderClearInventoryService.findPage(new Page<>(request, response), orderClearInventory));
    }

    @RequiresPermissions("ec:order:confirm")
    @RequestMapping(value = "saveClearInventory")
    public String saveClearInventory(OrderClearInventory orderClearInventory){
        orderClearInventoryService.save(orderClearInventory);
        return "redirect:" + adminPath + "/ec/order/toHandleReserve?id=" + orderClearInventory.getOrderId();
    }

    @RequestMapping(value = "modifyAddress")
    public String modifyAddress(Address address, RedirectAttributes redirectAttributes){
        orderService.modifyAddress(address);
        addMessage(redirectAttributes, "收货信息修改成功！");
        return "redirect:" + adminPath + "/ec/order/form?id="+address.getId();
    }


    @RequestMapping(value = "farmOrderList")
    public String farmOrderList(FarmOrder farmOrder, Model model) {
        model.addAttribute("farmOrder",farmOrder);
        return "module/ec/order/farmOrderList";
    }

    @ResponseBody
    @RequestMapping(value = "farmOrderListData")
    public DataGrid farmOrderListData(FarmOrder farmOrder, HttpServletRequest request, HttpServletResponse response) {
        return DataGrid.parse(orderService.findFarmOrderList(new Page<>(request, response), farmOrder));
    }

    @RequestMapping(value = "exportFarmOrderList", method= RequestMethod.POST)
    public String exportFarmOrderList(Order order, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        try {
            FarmOrder farmOrder = new FarmOrder();
            farmOrder.setProductIds(order.getSqlMap().get("productIds"));
            farmOrder.getSqlMap().putAll(order.getSqlMap());
            farmOrder.setOrderNumber(order.getSqlMap().get("keyWord"));
            String fileName = "水果发货列表" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
            Page<FarmOrder> page = orderService.findFarmOrderList(new Page<>(request, response, -1), farmOrder);
            new ExportExcel("水果发货列表", FarmOrder.class).setDataList(page.getList()).write(response, fileName).dispose();
            return null;
        } catch (Exception e) {
            addMessage(redirectAttributes, "导出水果发货列表失败！失败信息："+e.getMessage());
        }
        return "redirect:" + adminPath + "/ec/order/farmOrderList";
    }

    /**
     * 导入发货单
     * @param file
     * @param redirectAttributes
     * @return
     */
    @RequiresPermissions("ec:order:importInvoices")
    @RequestMapping(value = "importInvoices", method= RequestMethod.POST)
    public String importInvoices(MultipartFile file, RedirectAttributes redirectAttributes) {
        try {
            int successNum = 0;
            int failureNum = 0;
            int rowIndex = 2;
            StringBuilder failureMsg = new StringBuilder();
            ImportExcel ei = new ImportExcel(file, 1, 0);
            List<Invoices> list = ei.getDataList(Invoices.class);
            for (Invoices invoices : list){
                rowIndex++;
                try{
                    if(invoices.isEmpty()){continue;}
                    BeanValidators.validateWithException(validator, invoices);
                    Assert.isTrue(invoices.getShippingNo().indexOf(".") < 0,"运单号[" + invoices.getShippingNo() + "]格式不正确！");
                    orderService.deliveryByImportInvoices(invoices);
                    successNum++;
                }catch(ConstraintViolationException ex){
                    failureMsg.append("<br/>第[" + rowIndex + "]行数据导入失败：");
                    List<String> messageList = BeanValidators.extractPropertyAndMessageAsList(ex, ": ");
                    for (String message : messageList){
                        failureMsg.append(message + "; ");
                    }
                    failureNum++;
                    logger.error("第[" + rowIndex + "]行数据导入失败：" + ex.getMessage());
                }catch (Exception ex) {
                    failureMsg.append("<br/>第[" + rowIndex + "]行数据导入失败：" + ex.getMessage());
                    failureNum++;
                    logger.error("第[" + rowIndex + "]行数据导入失败：" + ex.getMessage());
                }
            }
            if (failureNum>0){
                failureMsg.insert(0, "，失败 " + failureNum + " 条数据，导入信息如下：");
            }
            addMessage(redirectAttributes, "已成功导入 " + successNum + " 条数据" + failureMsg.toString().replaceAll("[\\t\\n\\r]", "<br/>"));
            logger.info("已成功导入 " + successNum + " 条数据" + failureMsg);
        } catch (Exception e) {
            addMessage(redirectAttributes, "导入失败！失败信息：" + e.getMessage().replaceAll("[\\t\\n\\r]", "<br/>"));
            logger.error("导入失败！失败信息：" + e.getMessage());
        }
        return "redirect:" + adminPath + "/ec/order/shipList";
    }

    /**
     * 下载导入数据模板
     * @param response
     * @param redirectAttributes
     * @return
     */
    @RequiresPermissions("ec:order:importInvoices")
    @RequestMapping(value = "importInvoices/template")
    public String importInvoicesTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
        try {
            String fileName = "发货单导入模板.xlsx";
            new ExportExcel("发货单导入模板", Invoices.class, 2).write(response, fileName).dispose();
            return null;
        } catch (Exception e) {
            addMessage(redirectAttributes, "发货单导入模板下载失败！失败信息："+e.getMessage());
        }
        return "redirect:" + adminPath + "/ec/order/shipList";
    }

    @ResponseBody
    @RequestMapping(value = "confirmAllOrder")
    public void confirmAllOrder(Order order){
        orderService.confirmAllOrder(order);
    }

    @RequestMapping(value = "createRefund")
    @ResponseBody
    public void createRefund(Refund refund) {
        refundService.createRefund(refund);
    }

    @RequestMapping(value = "revocationRefund")
    @ResponseBody
    public void revocationRefund(Refund refund) {
        refundService.revocation(refund);
    }

    @RequestMapping(value = "itemListData")
    @ResponseBody
    public DataGrid itemListData(OrderItem orderItem, HttpServletRequest request, HttpServletResponse response) {
        return DataGrid.parse(orderItemService.findPage(new Page(request, response), orderItem));
    }
    @RequestMapping(value = "updateNote")
    @ResponseBody
    public void updateNote(Order order) {
        orderService.updateNote(order);
    }

}

