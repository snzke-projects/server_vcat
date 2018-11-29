package com.vcat.api.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcat.common.persistence.CrudDao;
import com.vcat.common.service.CrudService;
import com.vcat.module.ec.dao.ShopBrandDao;
import com.vcat.module.ec.entity.Brand;
import com.vcat.module.ec.entity.Shop;
import com.vcat.module.ec.entity.ShopBrand;
@Service
@Transactional(readOnly = true)
public class ShopBrandService extends CrudService<ShopBrand> {

	@Autowired
	private ShopBrandDao shopBrandDao;
	@Autowired
	private ShopProductService shopProductService;
	@Autowired 	
	private BrandService brandService;
	@Override
	protected CrudDao<ShopBrand> getDao() {
		return shopBrandDao;
	}
	//店铺批量绑定品牌
	@Transactional(readOnly = false)
	public void bathSave(String shopId,String guruId, List<String> list) {
		//先获取之前的绑定
		List<Map<String,Object>> sup = null;
		Shop shop = new Shop();
		shop.setId(shopId);
		sup = brandService.getBrandList(shopId,guruId);
		if(sup!=null&&sup.size()>0){
			for(Map<String,Object> supplier:sup){
				//如果新修改的品牌包含在之前绑定的品牌里面，且此品牌已经绑定
				String suId = (String) supplier.get("id");
				String isBind = (String)supplier.get("isBind");
				if (list.contains(suId) && "1".equals(isBind)) {
					list.remove(suId);
				}
				//如果之前绑定的品牌没有在新修改的品牌列表里面，设置del_flag=1
				else if(!list.contains(suId)&& "1".equals(isBind)){
					shopBrandDao.deleteBrand(shopId,suId);
				}
			}
			
		}
		if(list.size()>0)
		for(String supplierId:list){
			//添加店铺品牌
			ShopBrand shopBrand = new ShopBrand();
			Brand supplier = new Brand();
			supplier.setId(supplierId);
			shopBrand.setShop(shop);
			shopBrand.setBrand(supplier);
			if(shopBrandDao.get(shopBrand)==null){
				shopBrand.preInsert();
				shopBrandDao.insert(shopBrand);
				//上架品牌下的所有商品
				shopProductService.addBrandProduct(shopId,supplierId);
			}
			else{
				shopBrandDao.updateBrand(shopId,supplierId);
				//更新品牌下的所有商品
				shopProductService.updateBrandProduct(shopId,supplierId);
			}
		}
	}
	//删除店铺下的所有品牌
	@Transactional(readOnly = false)
	public void batchDelete(String shopId) {
		shopBrandDao.batchDelete(shopId);
	}

}
