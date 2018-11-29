package com.vcat.module.ec.entity;

import com.vcat.common.utils.StringUtils;
import com.vcat.module.core.entity.DataEntity;

/**
 * 问卷题目
 */
public class Question extends DataEntity<Question> {
	private static final long serialVersionUID = 1L;
    private String title;           // 题目标题
    private Integer type;           // 题目类型（1：单选 2：多选 3：判断 4：问答）
    private String optionValue;     // 备选值
    private Integer displayOrder;   // 排序
    private String answer;          // 答案

    public String[] getOptions(){
        if((type == 1 || type == 2) && StringUtils.isNotEmpty(optionValue)){
            return optionValue.split(",");
        }
        return null;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getOptionValue() {
        return optionValue;
    }

    public void setOptionValue(String optionValue) {
        this.optionValue = optionValue;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}
}
