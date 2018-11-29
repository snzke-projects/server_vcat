package com.vcat.module.ec.entity;

import com.vcat.module.core.entity.DataEntity;
import com.vcat.module.core.utils.DictUtils;

import java.math.BigDecimal;

/**
 * 订单项
 */
public class OrderItem extends DataEntity<OrderItem> {
	private static final long serialVersionUID = 1L;
    private Order order;				// 所属订单
    private Shop shop;					// 店铺
    private Shop parent;				// 所属店铺上级店铺
    private Shop grandfather;			// 所属店铺上上级店铺
    private int quantity;				// 数量
    private int promotionQuantity;	    //  优惠数量
    private BigDecimal itemPrice;		// 订单项总价
    private BigDecimal purchasePrice;   // 订单项进价
    private BigDecimal saleEarning;		// 当前订单项的销售奖励
    private BigDecimal bonusEarning;	// 当前订单项的分红奖励
    private BigDecimal firstBonusEarning;   // 当前订单项的一级团队分红
    private BigDecimal secondBonusEarning;  // 当前订单项的二级团队分红
    private BigDecimal point;           // 平台扣点比例
    private ProductItem productItem;	// 商品规格
    private Refund refund;				// 退款单
    private ReconciliationDetail reconciliation;// 结算单



    /**
     * 订单类型：
     * 1.普通
     * 2.拿样
     * 3.全额抵扣
     * 4.部分抵扣
     * 5.活动订单
     */
    private Integer orderType;
    private BigDecimal singleCoupon;    // 订单项使用卷金额
    private Integer refundCount;        // 退款次数

	public OrderItem(){}
	public OrderItem(String orderItemId) {
		this.id = orderItemId;
	}

    public int getPromotionQuantity() {
        return promotionQuantity;
    }

    public void setPromotionQuantity(int promotionQuantity) {
        this.promotionQuantity = promotionQuantity;
    }

    public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

	public ProductItem getProductItem() {
		return productItem;
	}

	public void setProductItem(ProductItem productItem) {
		this.productItem = productItem;
	}

	public BigDecimal getItemPrice() {
		return itemPrice;
	}
	public void setItemPrice(BigDecimal itemPrice) {
		this.itemPrice = itemPrice;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public Refund getRefund() {
		return refund;
	}

	public void setRefund(Refund refund) {
		this.refund = refund;
	}

	public BigDecimal getSaleEarning() {
		return saleEarning;
	}

	public void setSaleEarning(BigDecimal saleEarning) {
		this.saleEarning = saleEarning;
	}

    public BigDecimal getBonusEarning() {
        return bonusEarning;
    }

    public void setBonusEarning(BigDecimal bonusEarning) {
        this.bonusEarning = bonusEarning;
    }

    public BigDecimal getSecondBonusEarning() {
        return secondBonusEarning;
    }

    public void setSecondBonusEarning(BigDecimal secondBonusEarning) {
        this.secondBonusEarning = secondBonusEarning;
    }

    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public Integer getOrderType() {
        return orderType;
    }
    public String getOrderTypeLabel() {
        return DictUtils.getDictLabel(orderType+"","ec_order_type","未知类型");
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public BigDecimal getSingleCoupon() {
        return singleCoupon;
    }

    public void setSingleCoupon(BigDecimal singleCoupon) {
        this.singleCoupon = singleCoupon;
    }

    public BigDecimal getPoint() {
        return point;
    }

    public void setPoint(BigDecimal point) {
        this.point = point;
    }

    public Shop getParent() {
        return parent;
    }

    public void setParent(Shop parent) {
        this.parent = parent;
    }

    public Shop getGrandfather() {
        return grandfather;
    }

    public void setGrandfather(Shop grandfather) {
        this.grandfather = grandfather;
    }

    public ReconciliationDetail getReconciliation() {
        return reconciliation;
    }

    public void setReconciliation(ReconciliationDetail reconciliation) {
        this.reconciliation = reconciliation;
    }

    public Integer getRefundCount() {
        return refundCount;
    }

    public void setRefundCount(Integer refundCount) {
        this.refundCount = refundCount;
    }
    public BigDecimal getFirstBonusEarning() {
        return firstBonusEarning;
    }

    public void setFirstBonusEarning(BigDecimal firstBonusEarning) {
        this.firstBonusEarning = firstBonusEarning;
    }
}

