package com.vcat.module.ec.entity;

import java.util.Date;

/**
 * Created by Code.Ai on 16/4/11.
 * Description:
 */
public class CopywriteImage extends Image<CopywriteImage> {

    private static final long serialVersionUID = -7633148852983638071L;
    private Integer displayOrder;// 图片排序
    private Date    createDate;
}
