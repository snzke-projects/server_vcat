package com.vcat.module.content.service;

import com.google.common.collect.Lists;
import com.vcat.common.config.Global;
import com.vcat.common.persistence.Page;
import com.vcat.common.push.PushService;
import com.vcat.common.utils.CacheUtils;
import com.vcat.common.utils.StringUtils;
import com.vcat.module.content.dao.ArticleDao;
import com.vcat.module.content.dao.ArticleDataDao;
import com.vcat.module.content.dao.CategoryDao;
import com.vcat.module.content.entity.Article;
import com.vcat.module.content.entity.ArticleData;
import com.vcat.module.content.entity.Category;
import com.vcat.module.core.service.CrudService;
import com.vcat.module.core.utils.DictUtils;
import com.vcat.module.core.utils.UserUtils;
import com.vcat.module.ec.service.MessageService;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 文章Service
 */
@Service
@Transactional(readOnly = true)
public class ArticleService extends CrudService<ArticleDao, Article> {
    private static final String TYPE_NOVICE = "TYPE_NOVICE";						// 新手上路 --> V买卖
    private static final String TYPE_MARKETING_STRATEGY = "TYPE_MARKETING_STRATEGY";// 营销策略 --> V猫会
    private static final String TYPE_POPULAR_STORIES = "TYPE_POPULAR_STORIES";		// 人气案例 --> 寻宝
    private static final String TYPE_NEW_TUTORIAL = "TYPE_NEW_TUTORIAL";			// 最新教程 --> 涨知识
    private static final String DICT_CLASS_ROOM_TYPE = "ec_class_room_type";		// 字典表的类型

	@Autowired
	private ArticleDataDao articleDataDao;
	@Autowired
	private CategoryDao categoryDao;
    @Autowired
    private MessageService messageService;

	@Transactional(readOnly = false)
	public Page<Article> findPage(Page<Article> page, Article article,
			boolean isDataScopeFilter) {
		// 更新过期的权重，间隔为“6”个小时
		Date updateExpiredWeightDate = (Date) CacheUtils
				.get("updateExpiredWeightDateByArticle");
		if (updateExpiredWeightDate == null
				|| (updateExpiredWeightDate != null && updateExpiredWeightDate
						.getTime() < new Date().getTime())) {
			dao.updateExpiredWeight(article);
			CacheUtils.put("updateExpiredWeightDateByArticle",
					DateUtils.addHours(new Date(), 6));
		}
		// DetachedCriteria dc = dao.createDetachedCriteria();
		// dc.createAlias("category", "category");
		// dc.createAlias("category.site", "category.site");
		if (article.getCategory() != null
				&& StringUtils.isNotBlank(article.getCategory().getId())
				&& !Category.isRoot(article.getCategory().getId())) {
			Category category = categoryDao.get(article.getCategory().getId());
			if (category == null) {
				category = new Category();
			}
			category.setParentIds(category.getId());
			category.setSite(category.getSite());
			article.setCategory(category);
		} else {
			article.setCategory(new Category());
		}
		// if (StringUtils.isBlank(page.getOrderBy())){
		// page.setOrderBy("a.weight,a.update_date desc");
		// }
		// return dao.find(page, dc);
		// article.getSqlMap().put("dsf",
		// dataScopeFilter(article.getCurrentUser(), "o", "u"));
		return super.findPage(page, article);

	}

	@Transactional(readOnly = false)
	public void save(Article article) {
		if (article.getArticleData().getContent() != null) {
			article.getArticleData().setContent(
					StringEscapeUtils.unescapeHtml4(article.getArticleData()
							.getContent()));
		}
		// 如果没有审核权限，则将当前内容改为待审核状态
		if (!UserUtils.getSubject().isPermitted("cms:article:audit")) {
			article.setDelFlag(Article.DEL_FLAG_AUDIT);
		}
		// 如果栏目不需要审核，则将该内容设为发布状态
		if (article.getCategory() != null
				&& StringUtils.isNotBlank(article.getCategory().getId())) {
			Category category = categoryDao.get(article.getCategory().getId());
			if (!Global.YES.equals(category.getIsAudit())) {
				article.setDelFlag(Article.DEL_FLAG_NORMAL);
			}
		}
        article.preUpdate();
		if (StringUtils.isNotBlank(article.getViewConfig())) {
			article.setViewConfig(StringEscapeUtils.unescapeHtml4(article
					.getViewConfig()));
		}

		if (StringUtils.isBlank(article.getId())) {
            ArticleData articleData = article.getArticleData();
			article.preInsert();
			articleData.setId(article.getId());
            article.setHits(3000);              // 文章初始化点击量为3000
			dao.insert(article);
			articleDataDao.insert(articleData);
            pushMsgBeforeSaveArticle(article);
		} else {
			dao.update(article);
            article.getArticleData().setId(article.getId());
			articleDataDao.update(article.getArticleData());
		}
	}

    /**
     * 推送新文章通知
     * 判断文章是否为v买卖分类下文章，如果是，则推送透传
     * @param article
     */
    private void pushMsgBeforeSaveArticle(Article article){
        String noviceCategoryId = DictUtils.getDictValue(TYPE_NOVICE,DICT_CLASS_ROOM_TYPE,"11");
        if(null != article && null != article.getCategory() && noviceCategoryId.equals(article.getCategory().getId())){
            messageService.pushAllHideNotRead(PushService.MSG_TYPE_BUSINESS);
        }
    }

	@Transactional(readOnly = false)
	public void delete(Article article, Boolean isRe) {
		// dao.updateDelFlag(id,
		// isRe!=null&&isRe?Article.DEL_FLAG_NORMAL:Article.DEL_FLAG_DELETE);
		// 使用下面方法，以便更新索引。
		// Article article = dao.get(id);
		// article.setDelFlag(isRe!=null&&isRe?Article.DEL_FLAG_NORMAL:Article.DEL_FLAG_DELETE);
		// dao.insert(article);
		super.delete(article);
	}

	/**
	 * 通过编号获取内容标题
	 * 
	 * @return new Object[]{栏目Id,文章Id,文章标题}
	 */
	public List<Object[]> findByIds(String ids) {
		if (ids == null) {
			return new ArrayList<Object[]>();
		}
		List<Object[]> list = Lists.newArrayList();
		String[] idss = StringUtils.split(ids, ",");
		Article e = null;
		for (int i = 0; (idss.length - i) > 0; i++) {
			e = dao.get(idss[i]);
			list.add(new Object[] { e.getCategory().getId(), e.getId(),
					StringUtils.abbr(e.getTitle(), 50) });
		}
		return list;
	}

	/**
	 * 点击数加一
	 */
	@Transactional(readOnly = false)
	public void updateHitsAddOne(String id) {
		dao.updateHitsAddOne(id);
	}

	/**
	 * 更新索引
	 */
	public void createIndex() {
		// dao.createIndex();
	}

	/**
	 * 全文检索
	 */
	// FIXME 暂不提供检索功能
	public Page<Article> search(Page<Article> page, String q,
			String categoryId, String beginDate, String endDate) {
		return page;
	}

}
