package com.vcat.module.ec.entity;

import com.vcat.common.cloud.QCloudUtils;
import com.vcat.common.constant.ApiConstants;
import com.vcat.module.core.entity.DataEntity;

/**
 * 分享文案
 * @author cw
 *
 */
public class ShareCopywrite extends DataEntity<ShareCopywrite> {
	private static final long serialVersionUID = 1L;
	private String title;//分享标题
	private String content;//分享内容
	private String type;//分享类型  1分享小店文案 2 邀请战队文案  3分享app 4分享商品
	private String icon;//分享图片地址
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getIcon() {
		return QCloudUtils.createThumbDownloadUrl(icon, ApiConstants.DEFAULT_AVA_THUMBSTYLE);
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
}
