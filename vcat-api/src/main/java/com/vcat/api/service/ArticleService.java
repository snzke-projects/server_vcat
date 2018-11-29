package com.vcat.api.service;

import com.vcat.common.cloud.QCloudUtils;
import com.vcat.common.constant.ApiConstants;
import com.vcat.common.persistence.Page;
import com.vcat.common.persistence.Pager;
import com.vcat.module.content.dao.ArticleDao;
import com.vcat.module.content.entity.Article;
import com.vcat.module.core.utils.DictUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class ArticleService {
	private static final String TYPE_NOVICE = "TYPE_NOVICE";						// 新手上路
	private static final String TYPE_MARKETING_STRATEGY = "TYPE_MARKETING_STRATEGY";// 营销策略
	private static final String TYPE_POPULAR_STORIES = "TYPE_POPULAR_STORIES";		// 人气案例
	private static final String TYPE_NEW_TUTORIAL = "TYPE_NEW_TUTORIAL";			// 最新教程
	private static final String DICT_CLASS_ROOM_TYPE = "ec_class_room_type";		// 字典表的类型
	private static final String DEFAULT_CATEGORY_ID = "10";							// 默认 新手上路栏目ID

	@Autowired
	private ArticleDao articleDao;

	public Map<String, Object> findTopArticle(String label, String type){
		Article article = new Article();
		article.getSqlMap().put("categoryId",
				DictUtils.getDictValue(label, type, DEFAULT_CATEGORY_ID));
		return articleDao.findTopArticle(article);
	}

	@Transactional(readOnly = false)
	public Pager findPage(Pager pager,String type){
		Page page = new Page();
		page.setPageNo(pager.getPageNo());
		page.setPageSize(pager.getPageSize());
		List list = null;
		Integer rowCount = 0;

		// 封装查询实体
		Article article = new Article();
		article.setPage(page);
		article.getSqlMap().put("categoryId",
				DictUtils.getDictValue(type,DICT_CLASS_ROOM_TYPE,DEFAULT_CATEGORY_ID));

		list = articleDao.findClassRoomArticle(article);

		for (int i = 0;i<list.size();i++){
			Object res = list.get(i);
			if(res instanceof Map){
				Map<String,Object> row = (Map)res;
				row.put("imageUrl",QCloudUtils.createOriginalDownloadUrl(row.get("imageUrl")+""));
				row.put("templateUrl", ApiConstants.VCAT_DOMAIN+"/buyer/views/article.html?id="+row.get("id"));
				//row.put("templateUrl", "http://192.168.2.5:8080/vcat-api/vmao/views/article.html?id="+row.get("id"));
			}
		}

		if(null != list && !list.isEmpty()){
			rowCount = Integer.parseInt(article.getPage().getCount() + "");
			pager.setList(list);
			pager.setRowCount(rowCount);
			pager.doPage();
		}

		if (pager.getPageNo() > pager.getPageCount()) {
			pager.setList(new ArrayList<>());
		}
		return pager;
	}

	public Pager findPage2(Pager pager, String type) {
		Page page = new Page();
		page.setPageNo(pager.getPageNo());
		page.setPageSize(pager.getPageSize());
		List list = null;
		Integer rowCount = 0;

		// 封装查询实体
		Article article = new Article();
		article.setPage(page);
		article.getSqlMap().put(
				"categoryId",
				DictUtils.getDictValue(type, DICT_CLASS_ROOM_TYPE,
						DEFAULT_CATEGORY_ID));

		list = articleDao.findClassRoomArticle(article);

		for (int i = 0; i < list.size(); i++) {
			Object res = list.get(i);
			if (res instanceof Map) {
				Map<String, Object> row = (Map) res;
				row.put("thumb",
						QCloudUtils.createOriginalDownloadUrl(row
								.get("imageUrl") + ""));
				row.put("templateUrl", ApiConstants.VCAT_DOMAIN
						+ "/buyer/views/article.html?id=" + row.get("id"));
				// row.put("templateUrl",
				// "http://192.168.2.5:8080/vcat-api/vmao/views/article.html?id="+row.get("id"));
			}
		}

		if (null != list && !list.isEmpty()) {
			rowCount = Integer.parseInt(article.getPage().getCount() + "");
			pager.setList(list);
			pager.setRowCount(rowCount);
			pager.doPage();
		}

		if (pager.getPageNo() > pager.getPageCount()) {
			pager.setList(new ArrayList<>());
		}
		return pager;
	}
	@Transactional(readOnly = false)
	public void addHits(String id) {
		articleDao.addHits(id);
	}
}
