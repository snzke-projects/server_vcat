package com.vcat.module.ec.dao;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.core.entity.Area;
import com.vcat.module.ec.dto.OrderItemDto;
import com.vcat.module.ec.entity.FreightConfig;

import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

@MyBatisDao
public interface FreightConfigDao extends CrudDao<FreightConfig> {
    /**
     * 删除运费配置所属城市
     * @param id
     */
    void deleteCityList(String id);

    /**
     * 插入运费配置所属城市
     * @param cityList
     */
    void insertCityList(@Param(value = "id")String id,@Param(value = "cityList")List<Area> cityList);

    /**
     * 根据运费配置获取所属城市集合
     * @param freightConfigId
     * @return
     */
    List<Area> getCityList(String freightConfigId);

    /**
     * 根据运费模板删除运费配置
     * @param expressTemplateId
     */
    void deleteConfigs(String expressTemplateId);
    /**
     * 获取商品的默认邮费
     * @param buyerId
     * @param productId
     * @return
     */
	BigDecimal getFreightPrice(@Param(value = "customerId")String buyerId, @Param(value = "productId")String productId);

	FreightConfig getFreightBylist(@Param(value = "customerId")String buyerId, @Param(value = "list")List<OrderItemDto> list);
}
