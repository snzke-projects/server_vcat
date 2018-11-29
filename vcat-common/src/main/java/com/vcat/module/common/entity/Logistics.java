package com.vcat.module.common.entity;

import com.vcat.common.cloud.QCloudUtils;
import com.vcat.common.constant.ApiConstants;
import com.vcat.common.utils.DateUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 物流信息
 */
public class Logistics implements Serializable {
    private static final long serialVersionUID = 1L;
    private String com;                 // 物流公司编码
    private String ischeck;             // 是否签收
    private String condition;           // 未知参数
    private List<LogisticsInfo> data;   // 物流详情
    private String nu;                  // 运单号
    /**
     * 0：在途，即货物处于运输过程中；
     * 1：揽件，货物已由快递公司揽收并且产生了第一条跟踪信息；
     * 2：疑难，货物寄送过程出了问题；
     * 3：签收，收件人已签收；
     * 4：退签，即货物由于用户拒签、超区等原因退回，而且发件人已经签收；
     * 5：派件，即快递正在进行同城派件；
     * 6：退回，货物正处于退回发件人的途中；
     */
    private String state;               // 物流状态
    private String message;             // 物流消息
    private Date updatetime;            // 最后一次更新时间
    private String status;              // 访问请求状态
    private String ExpressName;        //物流名字
    private String logoUrl;

    public String getCom() {
        return com;
    }

    public void setCom(String com) {
        this.com = com;
    }

    public String getIscheck() {
        return ischeck;
    }

    public void setIscheck(String ischeck) {
        this.ischeck = ischeck;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public List<LogisticsInfo> getData() {
        return data;
    }

    public void setData(List<LogisticsInfo> data) {
        this.data = data;
    }

    public String getNu() {
        return nu;
    }

    public void setNu(String nu) {
        this.nu = nu;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = DateUtils.parseDate(updatetime);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

	public String getExpressName() {
		return ExpressName;
	}

	public void setExpressName(String expressName) {
		ExpressName = expressName;
	}

	public String getLogoUrl() {
		return QCloudUtils.createThumbDownloadUrl(logoUrl, ApiConstants.DEFAULT_AVA_THUMBSTYLE);
	}

	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
