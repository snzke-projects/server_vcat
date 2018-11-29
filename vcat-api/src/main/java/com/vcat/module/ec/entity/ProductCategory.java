package com.vcat.module.ec.entity;

import com.vcat.common.cloud.QCloudUtils;
import com.vcat.module.core.entity.LRTreeEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

/**
 * 商品分类Entity
 */
public class ProductCategory extends LRTreeEntity<ProductCategory> {
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this,
				ToStringStyle.JSON_STYLE);
	}

	private static final long serialVersionUID = 1L;
    private final String TABLE_NAME = "ec_category";
	
	private String name;					// 分类名称
	private String description;				// 说明
	private String icon;                    // 图标
	private List<ProductProperty> propertyList;	// 属性集合
    private Integer isActivate;             // 是否激活
	public ProductCategory() {}

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ProductCategory getParent() {
		return parent;
	}
	public void setParent(ProductCategory parent) {
		this.parent = parent;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<ProductProperty> getPropertyList() {
		return propertyList;
	}

	public void setPropertyList(List<ProductProperty> propertyList) {
		this.propertyList = propertyList;
	}

	public static void sortList(List<ProductCategory> list, List<ProductCategory> sourcelist,
			String parentId) {
		for (int i = 0; i < sourcelist.size(); i++) {
			ProductCategory e = sourcelist.get(i);
			if (e.getParent() != null && e.getParent().getId() != null
					&& e.getParent().getId().equals(parentId)) {
				list.add(e);
				// 判断是否还有子节点, 有则继续获取子节点
				for (int j = 0; j < sourcelist.size(); j++) {
					ProductCategory child = sourcelist.get(j);
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

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getIconPath(){
		return QCloudUtils.createOriginalDownloadUrl(icon);
	}

    public Integer getIsActivate() {
        return isActivate;
    }

    public void setIsActivate(Integer isActivate) {
        this.isActivate = isActivate;
    }
}
