package com.vcat.common.easyui;

import com.google.common.collect.Lists;
import com.vcat.common.persistence.Page;

import java.io.Serializable;
import java.util.List;

public class DataGrid implements Serializable {
    private long total;  // 数据总数
    private List rows = Lists.newArrayList();      // 数据
    private List footer = Lists.newArrayList();    // 页脚
    public static DataGrid parse(Page page){
        DataGrid dataGrid = new DataGrid();
        dataGrid.setTotal(page.getCount());
        dataGrid.setRows(page.getList());
        return dataGrid;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List getRows() {
        return rows;
    }

    public void setRows(List rows) {
        this.rows = rows;
    }

    public List getFooter() {
        return footer;
    }

    public void setFooter(List footer) {
        this.footer = footer;
    }
}
