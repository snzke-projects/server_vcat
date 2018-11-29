package com.vcat.module.ec.dto;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.vcat.common.cloud.QCloudUtils;
import com.vcat.common.constant.ApiConstants;
import com.vcat.module.ec.entity.CopywriteImage;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by Code.Ai on 16/4/12.
 * Description:
 */
public class CopywriteDto implements Serializable {
    private String               id;
    private String               productId;
    private String               title;
    private String               content;
    private String               activateTimeToString;
    private String               logo;
    private Boolean              defaultCopywrite;
    private String imageList;  //文案配图

    public String getActivateTimeToString() {
        return activateTimeToString;
    }

    public void setActivateTimeToString(String activateTimeToString) {
        this.activateTimeToString = activateTimeToString;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLogo() {
        return QCloudUtils.createThumbDownloadUrl(logo, ApiConstants.DEFAULT_PRODUCT_THUMBSTYLE);
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public Boolean getDefaultCopywrite() {
        return defaultCopywrite;
    }

    public void setDefaultCopywrite(Boolean defaultCopywrite) {
        this.defaultCopywrite = defaultCopywrite;
    }

    public ArrayNode getImageList() {
        ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
        if(imageList != null){
            String[] urls = imageList.split(",");
            for(String url : urls){
                arrayNode.add( QCloudUtils.createThumbDownloadUrl(url, 400));
            }
        }
        return arrayNode;
    }
    public void setImageList(String imageList) {
        this.imageList = imageList;
    }

    public String getImageListOfString(){
        return imageList;
    }
}
