package com.vcat.module.ec.dto;

import java.io.Serializable;
import java.util.Date;


public class UpgradeRequestDto implements Serializable {
    private static final long serialVersionUID = -2675015513633046908L;
    private String id;
    private String shopId;
    private String shopName;
    private Date   statusUpdate;

}
