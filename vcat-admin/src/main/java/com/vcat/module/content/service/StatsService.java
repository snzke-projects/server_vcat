package com.vcat.module.content.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcat.common.utils.DateUtils;
import com.vcat.module.content.dao.ArticleDao;
import com.vcat.module.content.entity.Category;
import com.vcat.module.content.entity.Site;
import com.vcat.module.core.entity.Office;
import com.vcat.module.core.service.BaseService;

/**
 * 统计Service
 */
@Service
@Transactional(readOnly = true)
public class StatsService extends BaseService {

	@Autowired
	private ArticleDao articleDao;

	public List<Category> article(Map<String, Object> paramMap) {
		Category category = new Category();

		Site site = new Site();
		site.setId(Site.getCurrentSiteId());
		category.setSite(site);

		Date beginDate = DateUtils.parseDate(paramMap.get("beginDate"));
		if (beginDate == null) {
			beginDate = DateUtils.setDays(new Date(), 1);
			paramMap.put("beginDate",
					DateUtils.formatDate(beginDate, "yyyy-MM-dd"));
		}
		category.setBeginDate(beginDate);
		Date endDate = DateUtils.parseDate(paramMap.get("endDate"));
		if (endDate == null) {
			endDate = DateUtils.addDays(DateUtils.addMonths(beginDate, 1), -1);
			paramMap.put("endDate", DateUtils.formatDate(endDate, "yyyy-MM-dd"));
		}
		category.setEndDate(endDate);

		String categoryId = (String) paramMap.get("categoryId");
		if (categoryId != null && !("".equals(categoryId))) {
			category.setId(categoryId);
			category.setParentIds(categoryId);
		}

		String officeId = (String) (paramMap.get("officeId"));
		Office office = new Office();
		if (officeId != null && !("".equals(officeId))) {
			office.setId(officeId);
			category.setOffice(office);
		} else {
			category.setOffice(office);
		}

		List<Category> list = articleDao.findStats(category);
		return list;
	}

}
