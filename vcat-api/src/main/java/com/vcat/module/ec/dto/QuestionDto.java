package com.vcat.module.ec.dto;

import java.util.ArrayList;

import com.vcat.common.utils.StringUtils;
import com.vcat.module.core.entity.DataEntity;
import com.vcat.module.ec.entity.ActivityStatAnswer;

/**
 * 问卷题目
 */
public class QuestionDto extends DataEntity<QuestionDto> {
	private static final long serialVersionUID = 1L;
    private String title;           // 题目标题
    private Integer type;           // 题目类型（1：单选 2：多选 3：判断 4：问答）
    private String optionValue;     // 备选值
    private Integer displayOrder;   // 排序
    private String answer;          // 答案
	private ArrayList<ActivityStatAnswer> statAnswerList = new ArrayList<>();
    private String activityStatId;//反馈报告题目id
    public String[] getOptions(){
        if((type == 1 || type == 2) && StringUtils.isNotEmpty(optionValue)){
            return optionValue.split(",");
        }else
        return null;
    }
	public String getActivityStatId() {
		return activityStatId;
	}
	public void setActivityStatId(String activityStatId) {
		this.activityStatId = activityStatId;
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
	public ArrayList<ActivityStatAnswer> getStatAnswerList() {
		return statAnswerList;
	}
	public void setStatAnswerList(ArrayList<ActivityStatAnswer> statAnswerList) {
		this.statAnswerList = statAnswerList;
	}
}
