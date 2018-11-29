package com.vcat.kuaidi100.pojo;

public class NoticeResponse {
	private Boolean result;
	private String returnCode;
	private String message;

	public Boolean getResult() {
		return result;
	}

	public void setResult(Boolean result) {
		this.result = result;
	}

	public String getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public static void main(String[] args) {
		NoticeResponse req = new NoticeResponse();
		req.setMessage("成功");
		req.setResult(true);
		req.setReturnCode("200");
	}
}
