package com.vcat.module.ec.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.annotation.MyBatisDao;
import com.vcat.module.ec.dto.CartDto;
import com.vcat.module.ec.entity.Cart;

@MyBatisDao
public interface CartDao extends CrudDao<Cart> {
	List<CartDto> getCartList(@Param("customerId")String buyerId, @Param("productType") String productType);
	void deleteCarts(Cart cart);
	Integer countByBuyerId(@Param("buyerId")String buyerId);
	List<Cart> findcartListByShop(@Param("customerId") String buyerId,@Param("shopId") String shopId, @Param("productType")String productType);
	List<Cart> findcartListByShop2(@Param("customerId") String buyerId,@Param("shopId") String shopId, @Param("productType")String productType);
	void insert2(Cart cart);
}
