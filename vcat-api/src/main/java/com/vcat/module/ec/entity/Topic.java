package com.vcat.module.ec.entity;

import com.vcat.common.cloud.QCloudUtils;
import com.vcat.module.core.entity.LRTreeEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;
import java.util.List;

/**
 * 活动专题
 */
public class Topic extends LRTreeEntity<Topic> {
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this,
                ToStringStyle.JSON_STYLE);
    }

	private static final long serialVersionUID = 1L;
    private final String TABLE_NAME = "ec_topic";

	private String title;					// 分类名称
	private String titleImgUrl;				// 说明
	private String listImgUrl;              // 图标
    private Date startTime;                 // 开始时间
    private Date endTime;                   // 结束时间
    private Integer isActivate;             // 是否激活
    private List<Product> productList;      // 商品集合
	public Topic() {}

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    public static void sortList(List<Topic> list, List<Topic> sourcelist,
                                String parentId) {
		for (int i = 0; i < sourcelist.size(); i++) {
			Topic e = sourcelist.get(i);
			if (e.getParent() != null && e.getParent().getId() != null
					&& e.getParent().getId().equals(parentId)) {
				list.add(e);
				// 判断是否还有子节点, 有则继续获取子节点
				for (int j = 0; j < sourcelist.size(); j++) {
					Topic child = sourcelist.get(j);
					if (child.getParent() != null
							&& child.getParent().getId() != null
							&& child.getParent().getId().equals(e.getId())) {
						sortList(list, sourcelist, e.getId());
						break;
					}
				}
			}
		}
	}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitleImgUrl() {
        return titleImgUrl;
    }

    public String getTitleImgUrlPath() {
        return QCloudUtils.createOriginalDownloadUrl(titleImgUrl);
    }

    public void setTitleImgUrl(String titleImgUrl) {
        this.titleImgUrl = titleImgUrl;
    }

    public String getListImgUrl() {
        return listImgUrl;
    }

    public String getListImgUrlPath() {
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

    public Integer getIsActivate() {
        return isActivate;
    }

    public void setIsActivate(Integer isActivate) {
        this.isActivate = isActivate;
    }

    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    @Override
    public Topic getParent() {
        return parent;
    }

    @Override
    public void setParent(Topic parent) {
        super.parent = parent;
    }
}
