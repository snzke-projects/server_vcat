
package com.vcat.common.persistence;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * [简要描述]:
 * 
 * @author cw
 * @version 1.0
 */
public class Pager implements Serializable{
    private static final long serialVersionUID = -8773454829579892901L;

    private Integer pageNo = 1; // 当前页

    private Integer rowCount = 0; // 总行数

    private Integer pageSize = 10; // 页大小

    private Integer pageCount = 0; // 总页数

    private Integer pageOffset = 0;// 当前页起始记录

    private Integer pageTail = 0;// 当前页到达的记录

    private List<?> list;  // 当前分页的集合

    public void doPage() {
        this.pageCount = (this.rowCount - 1) / this.pageSize + 1;
        this.pageOffset = (this.pageNo - 1) * this.pageSize;
        this.pageTail = this.pageOffset + this.pageSize;
        if ((this.pageOffset + this.pageSize) > this.rowCount) {
            this.pageTail = this.rowCount;
        }
    }

    public List<?> getList() {
        return list;
    }

    public void setList(List<?> list) {
        this.list = list;
    }

    /**
     * 返回pageNo属性
     * 
     * @return pageNo属性
     */
    public Integer getPageNo() {
        return pageNo;
    }

    /**
     * 设置pageNo属性
     * 
     * @param pageNo pageNo属性
     */
    public void setPageNo(Integer pageNo) {
        this.pageNo = (pageNo == null || pageNo == 0 ? 1 : pageNo);
    }

    /**
     * 返回rowCount属性
     * 
     * @return rowCount属性
     */
    public Integer getRowCount() {
        return rowCount;
    }

    /**
     * 设置rowCount属性
     * 
     * @param rowCount rowCount属性
     */
    public void setRowCount(Integer rowCount) {
        this.rowCount = rowCount;
    }

    /**
     * 返回pageSize属性
     * 
     * @return pageSize属性
     */
    public Integer getPageSize() {
        return pageSize;
    }

    /**
     * 设置pageSize属性
     * 
     * @param pageSize pageSize属性
     */
    public void setPageSize(Integer pageSize) {
        this.pageSize = (pageSize == null || pageSize == 0 ? 10 : pageSize);
    }

    /**
     * 返回pageCount属性
     * 
     * @return pageCount属性
     */
    public Integer getPageCount() {
        return pageCount;
    }

    /**
     * 设置pageCount属性
     * 
     * @param pageCount pageCount属性
     */
    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    /**
     * 返回pageOffset属性
     * 
     * @return pageOffset属性
     */
    public Integer getPageOffset() {
        return pageOffset;
    }

    /**
     * 设置pageOffset属性
     * 
     * @param pageOffset pageOffset属性
     */
    public void setPageOffset(Integer pageOffset) {
        this.pageOffset = pageOffset;
    }

    /**
     * 返回pageTail属性
     * 
     * @return pageTail属性
     */
    public Integer getPageTail() {
        return pageTail;
    }

    /**
     * 设置pageTail属性
     * 
     * @param pageTail pageTail属性
     */
    public void setPageTail(Integer pageTail) {
        this.pageTail = pageTail;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this,
                ToStringStyle.JSON_STYLE);
    }
}
