package com.vcat.module.ec.service;

import com.vcat.module.core.service.CrudService;
import com.vcat.module.ec.dao.BrandDao;
import com.vcat.module.ec.entity.Brand;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 品牌Service
 */
@Service
@Transactional(readOnly = true)
public class BrandService extends CrudService<BrandDao, Brand> {
    @Override
    public List<Brand> findList(Brand entity) {
        return super.findList(entity);
    }

    @Transactional(readOnly = false)
	public void save(Brand brand) {
		// 将转义后的HTML字符回转
		brand.setDescription(StringEscapeUtils.unescapeHtml4(brand.getDescription()));
		super.save(brand);
	}

}
