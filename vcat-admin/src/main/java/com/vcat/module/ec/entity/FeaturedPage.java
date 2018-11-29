package com.vcat.module.ec.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vcat.common.cloud.QCloudUtils;
import com.vcat.module.core.entity.DataEntity;

/**
 * 每日精选
 */
public class FeaturedPage extends DataEntity<FeaturedPage> {
	private static final long serialVersionUID = 1L;
	private String name;        // 名字
	private String type;        // 类型
	private String displayOrder;
	private Integer isActivate;
	private String url;         // 图片地址
	private String code;        // 值
	private Integer height;     // 图片高度
	private Integer width;      // 图片宽度

    public String getActivateLabel(){
        if (ACTIVATED.equals(isActivate)){
            return "已激活";
        }else if(NOT_ACTIVATED.equals(isActivate)){
            return "未激活";
        }else{
            return "未知状态";
        }
    }

    public String getActivateColor(){
        if (ACTIVATED.equals(isActivate)){
            return "green";
        }else if(NOT_ACTIVATED.equals(isActivate)){
            return "red";
        }else{
            return "black";
        }
    }
    public void setCodeByMap(){
        if(!getSqlMap().isEmpty()){
            code = JSON.toJSONString(getSqlMap());
        }
    }
    public String getCategoryId(){
        if(type.equals("category")){
            JSONObject json = JSON.parseObject(code);
            if(null != json && !json.isEmpty()){
                return json.getString("categoryId");
            }
        }
        return null;
    }

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDisplayOrder() {
		return displayOrder;
	}
	public void setDisplayOrder(String displayOrder) {
		this.displayOrder = displayOrder;
	}
	public Integer getIsActivate() {
		return isActivate;
	}
	public void setIsActivate(Integer isActivate) {
		this.isActivate = isActivate;
	}
	public String getUrl() {
		return QCloudUtils.createOriginalDownloadUrl(url);
	}
	public void setUrl(String url) {
		this.url = url;
	}
    public String getUrlCode(){return url;}
	public Integer getHeight() {
		return height;
	}
	public void setHeight(Integer height) {
		this.height = height;
	}
	public Integer getWidth() {
		return width;
	}
	public void setWidth(Integer width) {
		this.width = width;
	}
	public Object getCode() {
		return JSON.parseObject(code);
	}
    public Object getCodeString() {
        return code;
    }
	public void setCode(String code) {
		this.code = code;
	}

}
