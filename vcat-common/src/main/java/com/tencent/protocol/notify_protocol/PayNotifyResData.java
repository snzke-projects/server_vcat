package com.tencent.protocol.notify_protocol;

import java.io.Serializable;



/**
 * 
 * @author cw
 *
 */
public class PayNotifyResData implements Serializable{

	private static final long serialVersionUID = -187837341758737076L;
	//协议层
    private String return_code = "";
    private String return_msg = "";
	public String getReturn_code() {
		return return_code;
	}
	public void setReturn_code(String return_code) {
		this.return_code = return_code;
	}
	public String getReturn_msg() {
		return return_msg;
	}
	public void setReturn_msg(String return_msg) {
		this.return_msg = return_msg;
	}
    
}
