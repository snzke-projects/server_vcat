package com.vcat.module.ec.entity;

import java.util.List;

import com.vcat.module.core.entity.DataEntity;
import com.vcat.module.ec.dto.QuestionDto;

/**
 * 活动报告反馈
 */
public class ActivityFeedBack extends DataEntity<ActivityFeedBack> {
	private static final long serialVersionUID = 1L;
    private Activity activity;      // 所属活动
	private String title;           // 标题
    private String productName;     // 商品名称
    private String categoryName;    // 商品类别
    private String conclusion;      // 反馈结论
    private int reportCount;        // 参与报告人数
    private int isActivate;         // 是否激活
    private List<Question> questionList;    //反馈问题列表
    private List<QuestionDto> questionlist;    //反馈问题列表

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getConclusion() {
        return conclusion;
    }

    public void setConclusion(String conclusion) {
        this.conclusion = conclusion;
    }

    public int getReportCount() {
        return reportCount;
    }

    public void setReportCount(int reportCount) {
        this.reportCount = reportCount;
    }

    public int getIsActivate() {
        return isActivate;
    }

    public void setIsActivate(int isActivate) {
        this.isActivate = isActivate;
    }

    public List<Question> getQuestionList() {
        return questionList;
    }

    public void setQuestionList(List<Question> questionList) {
        this.questionList = questionList;
    }

	public List<QuestionDto> getQuestionlist() {
		return questionlist;
	}

	public void setQuestionlist(List<QuestionDto> questionlist) {
		this.questionlist = questionlist;
	}
}
