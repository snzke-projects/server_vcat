package com.vcat.module.ec.dao;

import java.util.List;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import org.apache.ibatis.annotations.Param;
import com.vcat.module.ec.dto.ShopBgImageDto;
import com.vcat.module.ec.entity.ShopBgImage;

@MyBatisDao
public interface ShopBgImageDao extends CrudDao<ShopBgImage> {
	List<ShopBgImageDto> findAllBackgroundPicList(@Param("shopId")String shopId);
	List<ShopBgImageDto> findBindBackgroundPicList(@Param("shopId")String shopId);

	void deleteList(@Param("shopId")String shopId);
	//List<String> getList();
}
