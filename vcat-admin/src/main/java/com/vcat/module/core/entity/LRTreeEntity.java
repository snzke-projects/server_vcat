package com.vcat.module.core.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.vcat.common.utils.Reflections;
import com.vcat.common.utils.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 左右节点树级Entity类
 */
public abstract class LRTreeEntity<T> extends DataEntity<T> {
	private static final long serialVersionUID = 1L;

	protected T parent;	            // 父级对象
    protected boolean isRoot;       // 是否为根节点
    protected Integer lft;          // 左坐标
    protected Integer rgt;          // 右坐标
	protected Integer displayOrder;	// 排序
    protected List<T> childs;	    // 子级对象集合

	public LRTreeEntity() {
		super();
	}

	public LRTreeEntity(String id) {
		super(id);
	}

    /**
     * 获取该实体对应的表名
     * @return
     */
    public abstract String getTableName();

	/**
	 * 父对象，只能通过子类实现，父类实现mybatis无法读取
	 * @return
	 */
	@JsonBackReference
	@NotNull
	public abstract T getParent();

	/**
	 * 父对象，只能通过子类实现，父类实现mybatis无法读取
	 * @return
	 */
	public abstract void setParent(T parent);

	public String getParentId() {
		String id = null;
		if (parent != null){
			id = (String)Reflections.getFieldValue(parent, "id");
		}
		return StringUtils.isNotBlank(id) ? id : "0";
	}

    public boolean getIsRoot() {
        return isRoot;
    }

    public void setIsRoot(boolean root) {
        isRoot = root;
    }

    public Integer getLft() {
        return lft;
    }

    public void setLft(Integer lft) {
        this.lft = lft;
    }

    public Integer getRgt() {
        return rgt;
    }

    public void setRgt(Integer rgt) {
        this.rgt = rgt;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public List<T> getChilds() {
        return childs;
    }

    public void setChilds(List<T> childs) {
        this.childs = childs;
    }
}
