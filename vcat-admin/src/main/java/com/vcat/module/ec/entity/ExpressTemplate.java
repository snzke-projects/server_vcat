package com.vcat.module.ec.entity;

import com.vcat.module.core.entity.DataEntity;

import java.util.List;

/**
 * 运费模板
 */
public class ExpressTemplate extends DataEntity<ExpressTemplate> {
    private List<FreightConfig> freightConfigList;  // 运费设置集合
    private String name;                            // 模板名称
    private String nation;                          // 发货人所属国家
    private String province;                        // 发货人省份
    private String city;                            // 发货人城市
    private String district;                        // 发货人地区
    private String provinceTitle;                   // 发货人省份
    private String cityTitle;                       // 发货人城市
    private String districtTitle;                   // 发货人地区
    private String detailAddress;                   // 发货人详细地址
    private String addresserPhone;                  // 发货人电话
    private String addresserName;                   // 发货人名称
    private Integer valuationMethod;                // 计价方式(1:件数|2:重量|3:体积)
    private Boolean isDefault;                      // 是否默认

    public List<FreightConfig> getFreightConfigList() {
        return freightConfigList;
    }

    public void setFreightConfigList(List<FreightConfig> freightConfigList) {
        this.freightConfigList = freightConfigList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getProvinceTitle() {
        return provinceTitle;
    }

    public void setProvinceTitle(String provinceTitle) {
        this.provinceTitle = provinceTitle;
    }

    public String getCityTitle() {
        return cityTitle;
    }

    public void setCityTitle(String cityTitle) {
        this.cityTitle = cityTitle;
    }

    public String getDistrictTitle() {
        return districtTitle;
    }

    public void setDistrictTitle(String districtTitle) {
        this.districtTitle = districtTitle;
    }

    public String getDetailAddress() {
        return detailAddress;
    }

    public void setDetailAddress(String detailAddress) {
        this.detailAddress = detailAddress;
    }

    public String getAddresserPhone() {
        return addresserPhone;
    }

    public void setAddresserPhone(String addresserPhone) {
        this.addresserPhone = addresserPhone;
    }

    public String getAddresserName() {
        return addresserName;
    }

    public void setAddresserName(String addresserName) {
        this.addresserName = addresserName;
    }

    public Integer getValuationMethod() {
        return valuationMethod;
    }

    public void setValuationMethod(Integer valuationMethod) {
        this.valuationMethod = valuationMethod;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }
}