package com.vcat.module.ec.entity;

import com.vcat.common.utils.IdGen;
import com.vcat.module.core.entity.DataEntity;

import java.util.Date;

public class DataChangeLog extends DataEntity<DataChangeLog> {
	private static final long serialVersionUID = 1L;
    private String tableName;       // 表名
    private String entityName;      // 实体名
    private String associationId;   // 关联ID
    private String changeNo;        // 变更批次号
    private String fieldName;       // 字段名称
    private String fieldTitle;      // 字段显示名称
    private String fieldType;       // 字段类型
    private Object beforeValue;     // 变更之前的值
    private Object afterValue;      // 变更之后的值
    private String changeSource;    // 变更来源
    private String changeBy;        // 变更人
    private Date changeDate;        // 变更日期
    private String changeContent;   // 变更详情
    private DataChangeLog(){}

    public static DataChangeLog create(String tableName, String entityName, String associationId, String changeNo, String fieldName, String fieldTitle, String fieldType, Object beforeValue, Object afterValue, String changeSource, String changeBy, Date changeDate){
        DataChangeLog log = new DataChangeLog();
        log.setId(IdGen.uuid());
        log.tableName = tableName;
        log.entityName = entityName;
        log.associationId = associationId;
        log.changeNo = changeNo;
        log.fieldName = fieldName;
        log.fieldTitle = fieldTitle;
        log.fieldType = fieldType;
        log.beforeValue = beforeValue;
        log.afterValue = afterValue;
        log.changeSource = changeSource;
        log.changeBy = changeBy;
        log.changeDate = changeDate;
        return log;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getAssociationId() {
        return associationId;
    }

    public void setAssociationId(String associationId) {
        this.associationId = associationId;
    }

    public String getChangeNo() {
        return changeNo;
    }

    public void setChangeNo(String changeNo) {
        this.changeNo = changeNo;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldTitle() {
        return fieldTitle;
    }

    public void setFieldTitle(String fieldTitle) {
        this.fieldTitle = fieldTitle;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public Object getBeforeValue() {
        return beforeValue;
    }

    public void setBeforeValue(Object beforeValue) {
        this.beforeValue = beforeValue;
    }

    public Object getAfterValue() {
        return afterValue;
    }

    public void setAfterValue(Object afterValue) {
        this.afterValue = afterValue;
    }

    public String getChangeSource() {
        return changeSource;
    }

    public void setChangeSource(String changeSource) {
        this.changeSource = changeSource;
    }

    public String getChangeBy() {
        return changeBy;
    }

    public void setChangeBy(String changeBy) {
        this.changeBy = changeBy;
    }

    public Date getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(Date changeDate) {
        this.changeDate = changeDate;
    }

    public String getChangeContent() {
        return changeContent;
    }

    public void setChangeContent(String changeContent) {
        this.changeContent = changeContent;
    }
}
