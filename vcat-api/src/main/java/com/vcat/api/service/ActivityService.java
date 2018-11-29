package com.vcat.api.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vcat.common.cloud.QCloudUtils;
import com.vcat.common.constant.ApiConstants;
import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.Page;
import com.vcat.common.persistence.Pager;
import com.vcat.common.service.CrudService;
import com.vcat.common.utils.StringUtils;
import com.vcat.module.core.utils.DictUtils;
import com.vcat.module.ec.dao.*;
import com.vcat.module.ec.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class ActivityService extends CrudService<Activity> {
	@Autowired
	private ActivityDao activityDao;
    @Autowired
    private CustomerAddressDao customerAddressDao;
    @Autowired
    private PaymentDao paymentDao;
    @Autowired
    private PaymentLogDao paymentLogDao;
    @Autowired
    private GatewayDao gatewayDao;
    @Autowired
    private CustomerActivityService customerActivityService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private ProductItemService productItemService;
	@Override
	protected CrudDao<Activity> getDao() {
		return activityDao;
	}

	public Pager getPager(Pager pager,String shopId){
		Page page = new Page();
		page.setPageNo(pager.getPageNo());
		page.setPageSize(pager.getPageSize());
		List list;
		Integer rowCount;

		// 封装查询实体
		Activity activity = new Activity();
		activity.setPage(page);
		activity.getSqlMap().put("shopId",shopId);

		list = activityDao.findAppList(activity);

		for (int i = 0;i<list.size();i++){
			Object res = list.get(i);
			if(res instanceof Map){
				Map<String,Object> row = (Map)res;
				row.put("imgUrl", QCloudUtils.createOriginalDownloadUrl(row.get("imgUrl") + ""));
				row.put("templateUrl", ApiConstants.VCAT_DOMAIN+"/buyer/views/activities.html");
			}
		}

		if(null != list && !list.isEmpty()){
			rowCount = Integer.parseInt(activity.getPage().getCount() + "");
			pager.setList(list);
			pager.setRowCount(rowCount);
			pager.doPage();
		}
		if (pager.getPageNo() > pager.getPageCount()) {
			pager.setList(new ArrayList<>());
		}
		return pager;
	}

	public Pager getActivityOfMy(Pager pager,String shopId){
		Page page = new Page();
		page.setPageNo(pager.getPageNo());
		page.setPageSize(pager.getPageSize());
		List list;
		Integer rowCount;

		// 封装查询实体
		Activity activity = new Activity();
		activity.setPage(page);
		activity.getSqlMap().put("shopId",shopId);

		list = activityDao.getActivityOfMy(activity);
		if(null != list && !list.isEmpty()){
			for (int i = 0;i<list.size();i++){
				Object res = list.get(i);
				if(res instanceof Map){
					Map<String,Object> row = (Map)res;
					String activityId = (String) row.get("activityId");
					String templateUrl = (String) row.get("templateUrl");
					if(!StringUtils.isBlank(templateUrl)){
						if(templateUrl.contains("?")){
							templateUrl = templateUrl+"&activityId="+activityId;
						}else{
							templateUrl = templateUrl+"?activityId="+activityId;
						}
						row.put("templateUrl", templateUrl);
					}
				}
			}
		}

		if(null != list && !list.isEmpty()){
			rowCount = Integer.parseInt(activity.getPage().getCount() + "");
			pager.setList(list);
			pager.setRowCount(rowCount);
			pager.doPage();
		}
		if (pager.getPageNo() > pager.getPageCount()) {
			pager.setList(new ArrayList<>());
		}
		return pager;
	}

	public String getSeatStatus(String activityId) {
		
		return activityDao.getSeatStatus(activityId);
	}

    @Transactional(readOnly = false)
    public Map<String, Object> joinActivity(CustomerActivity ca,Address address,String shopId){
        customerActivityService.insert(ca);
        return getOrderDetail(ca.getActivity().getId(), address, shopId);
    }

    /**
     * 根据活动参与信息下单
     * @param activityId
     * @param address
     * @param customerId
     * @return
     */
    @Transactional(readOnly = false)
    private Map<String, Object> getOrderDetail(String activityId, Address address, String customerId) {
        Customer customer;
        Activity activity = get(activityId);
        JSONArray list = new JSONArray();
        JSONObject object = new JSONObject();
        JSONArray cartList = new JSONArray();
        JSONObject cart = new JSONObject();

        String productItemId = DictUtils.getDictValue(ApiConstants.POSTAGE_PRODUCT_ITEM_ID_KEY, "");
        ProductItem productItem = productItemService.get(productItemId);
        cart.put("productItemId", productItem.getId());
        cart.put("quantity", activity.getFee().divide(productItem.getRetailPrice()).intValue());
        cart.put("productType", Order.ORDER_TYPE_POSTAGE);
        cartList.add(cart);
        object.put("productType", Order.ORDER_TYPE_POSTAGE);
        object.put("cartList", cartList);
        object.put("shopId", "");
        list.add(object);
        customer = customerService.get(customerId);
        CustomerAddress customerAddress = new CustomerAddress();
        customerAddress.setCustomer(customer);
        customerAddress.setAddress(address);
        customerAddress = customerAddressDao.getByCustomerAndAddress(customerAddress);
        Map<String, Object> map = orderService.checkOrders(customer,list,customerAddress,null,"","","","");//生成支付单,普通订单
        activityDao.insertActivityOrder(activityId,customerId,map.get("paymentId")+"");//生成活动订单

        // 如果活动费用小于等于0，则自动支付订单
        if(null != activity && null != activity.getFee() && activity.getFee().compareTo(BigDecimal.ZERO) < 1){
            paidFreeActivityOrder(customer,activityId);
        }
        return map;
    }

    /**
     * 自动支付免费活动订单
     * @param customer
     * @param activityId
     */
    @Transactional(readOnly = false)
    public void paidFreeActivityOrder(Customer customer,String activityId){
        Order order = activityDao.getActivityOrder(activityId,customer.getId());
        order.getPayment().setTransactionNo("AUTO_PAID_BY_SYSTEM"); // 设置平台交易号
        Gateway systemGateway = gatewayDao.getByCode("system"); // 获取系统支付类型
        order.getPayment().setGateway(systemGateway);
        paymentDao.update(order.getPayment());
        orderDao.paymented(order.getPayment().getId());
        PaymentLog paymentLog = new PaymentLog();
        paymentLog.preInsert();
        paymentLog.setPayment(order.getPayment());
        paymentLog.setCustomer(customer);
        paymentLog.setAmount(BigDecimal.ZERO);
        paymentLog.setTransactionSuccess("1");
        paymentLog.setNote("自动支付0元活动费用订单");
        paymentLog.setGatewayCode(systemGateway.getCode());
        paymentLogDao.insertBypayment(paymentLog);
        logger.info("免费活动订单[" + order.getOrderNumber() + "]自动支付成功！");
    }

    public Order getActivityOrder(String activityId,String customerId){
        return activityDao.getActivityOrder(activityId, customerId);
    }

    @Transactional(readOnly = false)
    public void deleteActivityCustomer(String activityId,String customerId){
        activityDao.deleteActivityCustomer(activityId, customerId);
    }

    public Boolean getIsParticipate(String activityId, String customerId) {
        return activityDao.getIsParticipate(activityId, customerId);
    }

    public String getActivityIdByPayment(String paymentId){
        return activityDao.getActivityIdByPayment(paymentId);
    }

    public Map<String,Object> findTopActivity(){
        return activityDao.findTopActivity();
    }

}
