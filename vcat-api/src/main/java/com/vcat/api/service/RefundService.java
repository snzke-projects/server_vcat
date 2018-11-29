package com.vcat.api.service;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.service.CrudService;
import com.vcat.module.ec.dao.RefundDao;
import com.vcat.module.ec.dto.RefundDto;
import com.vcat.module.ec.entity.Order;
import com.vcat.module.ec.entity.Refund;
import com.vcat.module.ec.entity.RefundLog;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class RefundService extends CrudService<Refund> {
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderItemService orderItemService;
	@Autowired
	private RefundDao refundDao;
	@Autowired
	private RefundLogService refundLogService;
	@Override
	protected CrudDao<Refund> getDao() {
		return refundDao;
	}
	private static Logger logger = Logger.getLogger(RefundService.class);
	@Transactional(readOnly = false)
	public void save(Refund refund) {
		refundDao.insert(refund);
		RefundLog log = new RefundLog();
		log.preInsert();
		log.setRefund(refund);
		log.setStatusNote("您的退款申请创建成功，请等待后台人员审核");
		refundLogService.insert(log);
	}
	@Transactional(readOnly = false)
	public void updateReason(Refund refund) {
		refundDao.updateReason(refund);
	}
	@Transactional(readOnly = false)
	public void updateRefund(Refund refund) {
		refundDao.updateRefund(refund);
	}
	//添加退货地址
	@Transactional(readOnly = false)
	public void saveExpress(Refund refund) {
		refundDao.addExpress(refund);
		//插入日志
		RefundLog log = new RefundLog();
		log.preInsert();
		log.setRefund(refund);
		log.setStatusNote("您的退货单已添加，请等待后台人员退款");
		refundLogService.insert(log);
	}
	//取消退款
	@Transactional(readOnly = false)
	public void cancelRefund(Refund refund) {
		refund.setNote("您已撤销退款!");
		int index = refundDao.updateHasFreightPriceRefundAmount(refund.getId());
		if(index>0){
			logger.info("更新订单退款邮费成功！当前退款单："+refund.getId());
		}
		refundDao.cancelRefund(refund);
		//添加日志
		RefundLog log = new RefundLog();
		log.preInsert();
		log.setRefund(refund);
		log.setStatusNote("您已撤销退款!");
		refundLogService.insert(log);
	}

	//取消退款
	@Transactional(readOnly = false)
	public void sellerCancelRefund(Refund refund) {
		refund.setNote("您已撤销退款!");
		refundDao.cancelRefund(refund);
		//添加日志
		RefundLog log = new RefundLog();
		log.preInsert();
		log.setRefund(refund);
		log.setStatusNote("您已撤销退款!");
		refundLogService.insert(log);
	}
	// 获取退款单详情
	public RefundDto getRefund(String refundId) {
		return refundDao.getRefund(refundId);
	}
	public RefundDto getRefundInfo(String orderItemId) {
		return refundDao.getRefundInfo(orderItemId);
	}
	@Transactional(readOnly = false)
	public void deleteRefund(String orderItemId,String refundId) {
		refundDao.deleteRefund(orderItemId,refundId);
	}
	//获取退款单金额
	public Refund getRefundAmount(String orderItemId) {
		return refundDao.getRefundAmount(orderItemId);
	}

    /**
     * 创建退款单
     * @param orderId
     */
    @Transactional(readOnly = false)
    public void createReturn(String orderId, String reason){
        Order order = orderService.get(orderId);

        refundDao.createReturnByOrderId(orderId, reason);

        refundLogService.initByOrderId(orderId);

        if("0".equals(order.getShippingStatus())){

            refundLogService.addApprovalLog(orderId);

            refundDao.autoApprovalByOrderId(orderId, "系统自动审批拼团失败退款单");
        }
    }
}
