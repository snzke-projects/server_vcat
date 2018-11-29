package com.vcat.module.ec.entity;

import com.vcat.module.core.entity.DataEntity;

/**
 * 反馈报告题目表
 */
public class ActivityStat extends DataEntity<ActivityStat> {
	private static final long serialVersionUID = 1L;
	private ActivityFeedBack activityFeedback;//活动反馈报告
	private Question question;//题目
	public Question getQuestion() {
		return question;
	}
	public void setQuestion(Question question) {
		this.question = question;
	}
	public ActivityFeedBack getActivityFeedback() {
		return activityFeedback;
	}
	public void setActivityFeedback(ActivityFeedBack activityFeedback) {
		this.activityFeedback = activityFeedback;
	}
}
