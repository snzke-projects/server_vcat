package com.vcat.kuaidi100.pojo;

public class NoticeRequest {
	/**
	 * 轮询状态:
	 * polling		：轮询中
	 * shutdown		：结束
	 * abort		：中止
	 * updateall	：重新推送
	 * 其中当快递单为已签收时status=shutdown
	 * 当message为“3天查询无记录”或“60天无变化时”status= abort
	 * 对于stuatus=abort的状度，需要增加额外的处理逻辑
	 */
	private String status = "";
	/**
	 * 包括got、sending、check三个状态
	 * 意义不大，已弃用，可忽略
	 */
	private String billstatus = "";
	private String message = "";
	private Result lastResult = new Result();

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getBillstatus() {
		return billstatus;
	}

	public void setBillstatus(String billstatus) {
		this.billstatus = billstatus;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Result getLastResult() {
		return lastResult;
	}

	public void setLastResult(Result lastResult) {
		this.lastResult = lastResult;
	}

}
