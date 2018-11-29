package com.vcat.module.ec.entity;

import com.vcat.common.cloud.QCloudUtils;
import com.vcat.module.core.entity.DataEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 店铺等级
 */
public class Level extends DataEntity<Level> {
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this,
				ToStringStyle.JSON_STYLE);
	}

	private static final long serialVersionUID = 1L;
	private String name;	// 等级名称
	private Long minExp;	// 该等级所需最小经验值
	private Long maxExp;	// 该等级最大经验值
	private String url;		// 该等级所对应云图片ID
	public String getUrlPath(){
		return QCloudUtils.createOriginalDownloadUrl(url);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getMinExp() {
		return minExp;
	}
	public void setMinExp(Long minExp) {
		this.minExp = minExp;
	}
	public Long getMaxExp() {
		return maxExp;
	}
	public void setMaxExp(Long maxExp) {
		this.maxExp = maxExp;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
}
