package com.vcat.module.ec.entity;

import com.vcat.module.core.entity.DataEntity;

/**
 * 反馈报告题目表
 */
public class ActivityStatAnswer extends DataEntity<ActivityStatAnswer> {
	private static final long serialVersionUID = 1L;
	private ActivityStat activityStat;//报告题目
	private String answerRange;//答案选项
	private Integer personRange;//选项人数
	private Integer totalPerson;//总人数
	private Integer  percent;
	public String getAnswerRange() {
		return answerRange;
	}
	public void setAnswerRange(String answerRange) {
		this.answerRange = answerRange;
	}
	public Integer getPersonRange() {
		return personRange;
	}
	public void setPersonRange(Integer personRange) {
		this.personRange = personRange;
	}
	public ActivityStat getActivityStat() {
		return activityStat;
	}
	public void setActivityStat(ActivityStat activityStat) {
		this.activityStat = activityStat;
	}
	public Integer getTotalPerson() {
		return totalPerson;
	}
	public void setTotalPerson(Integer totalPerson) {
		this.totalPerson = totalPerson;
	}
	public Integer getPercent() {
		return percent;
	}
	public void setPercent(Integer percent) {
		this.percent = percent;
	}
}
