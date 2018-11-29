package com.vcat.module.ec.service;

import com.google.common.collect.Lists;
import com.vcat.common.utils.IdGen;
import com.vcat.common.utils.StringUtils;
import com.vcat.module.core.service.CrudService;
import com.vcat.module.core.service.ServiceException;
import com.vcat.module.ec.dao.*;
import com.vcat.module.ec.entity.*;
import com.vcat.module.ec.service.scheduled.ArchivedProductJob;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 商品Service
 */
@Service
@Transactional(readOnly = true)
public class ProductService extends CrudService<ProductDao, Product> {
	@Autowired
	private ProductItemService productItemService;
	@Autowired
	private ProductCategoryService productCategoryService;
	@Autowired
	private ProductImageService productImageService;
	@Autowired
	private ProductPropertyService productPropertyService;
	@Autowired
	private LoadEarningService loadEarningService;
	@Autowired
	private ShareEarningService shareEarningService;
	@Autowired
	private RecommendService recommendService;
	@Autowired
	private SpecService specService;
    @Autowired
    private ExpressTemplateService expressTemplateService;
    @Autowired
    private RatingSummaryDao ratingSummaryDao;
    @Autowired
    private TopicDao topicDao;
    @Autowired
    private CopywriteDao copywriteDao;
    @Autowired
    private CopywriteImageDao copywriteImageDao;
    @Autowired
    private GroupBuyingService groupBuyingService;

	public Product getBaseInfo(Product product){
		return super.get(product);
	}

	@Override
	public Product get(Product entity) {
		Product product = super.get(entity);
		product.setShareEarningList(shareEarningService.getShareEarningList(product.getId()));
		product.setImageList(productImageService.findListByProductId(entity.getId()));
		product.setPropertyList(productPropertyService.findListByProduct(product));
		product.setSpecNameArray(specService.getSpecNameArrayByProduct(product.getId()));
        productDataScopeFilter(product, "distribution", null);
		return product;
	}

	@Override
	public Product get(String id) {
		Product product = new Product();
		product.setId(id);
		return get(product);
	}
	public List<Product> findListByIds(String ids){
		List<Product> list = new ArrayList<>();
		if(StringUtils.isEmpty(ids)){
			return list;
		}
		return dao.findListByIds(ids.split("\\|"));
	}

	@Override
	public List<Product> findList(Product entity) {
        productDataScopeFilter(entity, "distribution", null);
		return super.findList(entity);
	}

	@Transactional(readOnly = false)
	public void save(Product product) {
		// 将转义后的HTML字符回转
		product.setDescription(StringEscapeUtils.unescapeHtml4(product.getDescription()));

		boolean isNew = product.getIsNewRecord();

        if(!isNew && product.getIsHot() == 1){
            GroupBuying groupBuying = new GroupBuying();
            groupBuying.setProduct(product);
            Assert.isNull(groupBuyingService.getAnother(groupBuying), "该商品正在参加团购活动，不能设置热销！");
        }

        super.save(product);

		// 保存商品所属分类
		product.getCategory().getSqlMap().put("productId", product.getId());
		productCategoryService.addProductCategory(product.getCategory());

		// 保存商品规格
		// 如果规格为新生成的规格，则删除历史规格
		boolean isNewProductItem = null != product.getItemList() && product.getItemList().size() > 0 && StringUtils.isEmpty(product.getItemList().get(0).getId());
		if(isNewProductItem){
			productItemService.deleteByProduct(product);
		}
		for (ProductItem productItem : product.getItemList()) {
			if(StringUtils.isNotEmpty(productItem.getItemSn()) && StringUtils.isNotEmpty(productItem.getName())){
				productItem.getSqlMap().put("productId",product.getId());
				productItemService.save(productItem,"商品详情");
			}
		}
		// 保存商品属性
		productPropertyService.saveProductProperty(product.getId(), product.getPropertyList());

		// 保存商品图片
		productImageService.deleteProductImage(product.getId());
        List<ProductImage> imageList = getProductImageList(product);
		productImageService.save(imageList);

		// 保存商品上架奖励
		product.getLoadEarning().setProduct(product);
		loadEarningService.save(product.getLoadEarning());

		// 保存是否热销
		saveHot(product);

		// 商品上下架
		if(!product.getArchived().toString().equals(product.getSqlMap().get("oldArchived"))
				|| isNew){
			archived(product.getId(),product.getArchived());
		}

        // 商品自动上架
        forecast(product, isNew);

        // 维护品牌表与分类的关系
        supplierCategory(product);

        // 保存邮费模板
        saveExpressTemplate(product);

        // 插入商品默认评分
        if(isNew){
            RatingSummary ratingSummary = new RatingSummary();
            ratingSummary.setProduct(product);
            ratingSummaryDao.insert(ratingSummary);

            insertCopywrite(product, imageList);
        }
	}

    private void insertCopywrite(Product product, List<ProductImage> imageList){
        Copywrite c = new Copywrite();
        c.preInsert();
        c.setTitle(product.getSqlMap().get("copywriteTitle"));
        c.setContent(product.getSqlMap().get("copywriteContent"));
        c.setIsActivate(true);
        c.setProduct(product);
        copywriteDao.insert(c);
        copywriteDao.activate(c);
        imageList.forEach(productImage -> {
            CopywriteImage image = new CopywriteImage();
            image.preInsert();
            image.setCopywrite(c);
            image.setDisplayOrder(productImage.getDisplayOrder());
            image.setImageUrl(productImage.getUrl());
            copywriteImageDao.insert(image);
        });
    }

    private void saveExpressTemplate(Product product){
        expressTemplateService.deleteFromProduct(product);
        if(product.getFreeShipping() == 0){
            expressTemplateService.saveWithProduct(product);
        }
    }

    private void supplierCategory(Product product){
        String brandId = product.getBrand().getId();
        String categoryId = product.getCategory().getId();
        String oldBrandId = product.getSqlMap().get("oldBrandId") + "";
        String oldCategoryId = product.getSqlMap().get("oldCategoryId") + "";

        // 维护品牌与商品分类的关系冗余表，便于查询
        if(!dao.hasBrandCategory(brandId, categoryId)){
            dao.insertBrandCategory(IdGen.uuid(), brandId, categoryId);
        }

        Boolean b = dao.needDeleteBrandCategory(oldBrandId, oldCategoryId);

        // 维护品牌与商品分类的关系冗余表，便于查询
        if(null != b && b){
            dao.deleteBrandCategory(oldBrandId, oldCategoryId);
        }
    }

    private void forecast(Product product,boolean isNew){
        // 商品自动上架
        // 设置自动上架任务
        if(!(product.getIsAutoLoad() + "").equals(product.getSqlMap().get("oldAutoLoad")) && 1 == product.getIsAutoLoad()){
            ArchivedProductJob.pushArchivedProductJob(product.getId(), 0, product.getAutoLoadDate());
        }

        // 修改自动上架任务时间
        if((product.getIsAutoLoad() + "").equals(product.getSqlMap().get("oldAutoLoad"))
                && 1 == product.getIsAutoLoad()
                && !product.getAutoLoadDate().equals(product.getSqlMap().get("oldAutoLoadDate"))
                && !isNew){
            ArchivedProductJob.updateArchivedProductJob(product.getId(), product.getAutoLoadDate());
        }

        // 关闭自动上架，删除自动上架任务
        if(!(product.getIsAutoLoad() + "").equals(product.getSqlMap().get("oldAutoLoad"))
                && 0 == product.getIsAutoLoad()
                || (!product.getArchived().toString().equals(product.getSqlMap().get("oldArchived"))
                && product.getArchived() == 0)){
            ArchivedProductJob.deleteArchivedProductJob(product.getId());
        }
    }

	private List<ProductImage> getProductImageList(Product product){
		List<ProductImage> productImageList = Lists.newArrayList();
		if(StringUtils.isNotEmpty(product.getProductImages())){
			String[] images = product.getProductImages().split("\\|");
			for (int i = 0; i < images.length; i++) {
				ProductImage productImage = new ProductImage();
				productImage.setName(product.getName() + " 图片" + (i + 1));
				productImage.setUrl(images[i]);
				productImage.getSqlMap().put("productId",product.getId());
				productImageList.add(productImage);
			}
		}
		return productImageList;
	}

	@Transactional(readOnly = false)
	public void delete(Product product) {
		super.delete(product);
	}

    @Transactional(readOnly = false)
    public void recover(Product product){
        dao.recover(product);
    }

	@Transactional(readOnly = false)
	private void saveHot(Product product){
		if(1 == product.getIsHot() && StringUtils.isEmpty(product.getHotRecommendId())){
			String id = IdGen.uuid();
			product.setHotRecommendId(id);
            recommendService.insertHotRecommend(product);
			recommendService.insertProductRecommend(product.getId(),id);
		}
		if(0 == product.getIsHot() && StringUtils.isNotEmpty(product.getHotRecommendId())){
			recommendService.deleteProductRecommend(product.getHotRecommendId());
            recommendService.deleteHotRecommend(product.getHotRecommendId());
		}
	}

	@Transactional(readOnly = false)
	public void archived(String productId,Integer archived) {
        if(archived == 0){
            Boolean canBeArchived = dao.getCanBeArchived(productId);
            if(null == canBeArchived || !canBeArchived){
                throw new ServiceException("请先激活该商品所属分类之后在执行上架操作！");
            }
        }else{
            // 取消选择专题中该商品
            topicDao.deleteByProductId(productId);
        }
        dao.archived(productId,archived);
	}

    @Transactional(readOnly = false)
    public void batchArchived(String[] productIds, Integer archived) {
        if(null == productIds || productIds.length == 0 || null == archived){
            throw new ServiceException("批量上下架商品失败：参数[productIds:"+productIds+",archived:"+archived+"]不完整");
        }
        for (int i = 0; i < productIds.length; i++) {
            archived(productIds[i], archived);
        }
    }

	public void autoLoadByForecast(Product product,Date forecastDate){
		product = getBaseInfo(product);

		product.setAutoLoadDate(forecastDate);
		Integer i = dao.autoLoadByForecast(product);
		if(i == 1){
			ArchivedProductJob.deleteArchivedProductJob(product.getId());
			ArchivedProductJob.pushArchivedProductJob(product.getId(), 0, forecastDate);
		}
	}

    @Transactional(readOnly = false)
    public void saveOrder(String[] productIds, String[] displayOrders) {
        if(null == productIds || productIds.length == 0){
            return;
        }
        if(productIds.length != displayOrders.length){
            throw new ServiceException("保存商品排序失败：商品ID数组长度与商品排序数组长度不一致[productIds:" + productIds.length + ",displayOrders:" + displayOrders.length + "]");
        }
        for (int i = 0; i < productIds.length; i++) {
            dao.updateOrder(productIds[i], displayOrders[i]);
        }
    }

    @Transactional(readOnly = false)
    public void stick(Product product) {
        dao.stick(product);
    }
}
