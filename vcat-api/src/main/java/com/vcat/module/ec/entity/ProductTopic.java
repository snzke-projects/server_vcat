package com.vcat.module.ec.entity;


import java.util.Date;
import java.util.List;

import com.vcat.common.cloud.QCloudUtils;
import com.vcat.common.constant.ApiConstants;
import com.vcat.module.core.entity.DataEntity;
import com.vcat.module.ec.dto.ProductDto;
/**
 * 商品顶顶上的那个图片类的实体
 */
public class ProductTopic extends DataEntity<ProductTopic> {
	private static final long serialVersionUID = -356409326409962896L;
	private String parentId ;
    private String title ;
    private String titleImgUrl;
    private String listImgUrl;
    private Date startTime;
    private Date endTime;
    private Long displayOrder;
    private List<ProductTopic> children;
    private List<ProductDto> products;
    
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getTitleImgUrl() {
		return QCloudUtils.createOriginalDownloadUrl(titleImgUrl);
	}
	public void setTitleImgUrl(String titleImgUrl) {
		this.titleImgUrl = titleImgUrl;
	}
	public String getListImgUrl() {
		return QCloudUtils.createOriginalDownloadUrl(listImgUrl);
	}
	public void setListImgUrl(String listImgUrl) {
		this.listImgUrl = listImgUrl;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public Long getDisplayOrder() {
		return displayOrder;
	}
	public void setDisplayOrder(Long displayOrder) {
		this.displayOrder = displayOrder;
	}
	public List<ProductTopic> getChildren() {
		return children;
	}
	public void setChildren(List<ProductTopic> children) {
		this.children = children;
	}
	public List<ProductDto> getProducts() {
		return products;
	}
	public void setProducts(List<ProductDto> products) {
		this.products = products;
	}
}