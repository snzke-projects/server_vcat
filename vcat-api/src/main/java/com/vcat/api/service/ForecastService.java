package com.vcat.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcat.common.cloud.QCloudUtils;
import com.vcat.common.constant.ApiConstants;
import com.vcat.common.persistence.CrudDao;
import com.vcat.common.persistence.Page;
import com.vcat.common.persistence.Pager;
import com.vcat.common.service.CrudService;
import com.vcat.module.ec.dao.ProductForecastDao;
import com.vcat.module.ec.entity.ProductForecast;

@Service
@Transactional(readOnly = true)
public class ForecastService extends CrudService<ProductForecast> {
	@Autowired
	private ProductForecastDao productForecastDao;
	@Override
	protected CrudDao<ProductForecast> getDao() {
		return productForecastDao;
	}

	public Pager getPager(Pager pager){
		Page page = new Page();
		page.setPageNo(pager.getPageNo());
		page.setPageSize(pager.getPageSize());
		List list;
		Integer rowCount;

		// 封装查询实体
		ProductForecast forecast = new ProductForecast();
		forecast.setPage(page);

		list = productForecastDao.findAppList(forecast);

		for (int i = 0;i<list.size();i++){
			Object res = list.get(i);
			if(res instanceof Map){
				Map<String,Object> row = (Map)res;
				row.put("imgUrl", QCloudUtils.createOriginalDownloadUrl(row.get("imgUrl") + ""));
				row.put("templateUrl", ApiConstants.VCAT_DOMAIN+"/buyer/views/shelvesforecast.html?id="+row.get("id"));
			}
		}

		if(null != list && !list.isEmpty()){
			rowCount = Integer.parseInt(forecast.getPage().getCount() + "");
			pager.setList(list);
			pager.setRowCount(rowCount);
			pager.doPage();
		}
		if (pager.getPageNo() > pager.getPageCount()) {
			pager.setList(new ArrayList<>());
		}
		return pager;
	}

    public Map<String, Object> getForecastDetail(ProductForecast productForecast){
        Map<String, Object> map = productForecastDao.getForecastDetail(productForecast);
        map.put("imgUrl", QCloudUtils.createOriginalDownloadUrl(map.get("imgUrl") + ""));
        return map;
    }

}
