//package com.vcat.api.service.scheduled;
//
//import com.vcat.common.utils.QuartzUtils;
//import com.vcat.common.utils.StringUtils;
//import com.vcat.module.ec.dao.ProductDao;
//import org.apache.log4j.Logger;
//import org.quartz.JobExecutionContext;
//import org.quartz.JobExecutionException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.quartz.QuartzJobBean;
//
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * 自动下架小店商品任务
// */
//public class ArchivedShopProductJob extends QuartzJobBean {
//	private final static String PRODUCT_ID = "PRODUCT_ID";	// 参数商品ID
//	private final static String SHOP_ID = "SHOP_ID";		// 参数小店ID
//	private final static String JOB_NAME_PRIFIX = "API_ARCHIVED_SP_";		// 任务名称前缀
//
//	@Autowired
//	private ProductDao productDao;
//
//	private static Logger LOG = Logger.getLogger(ArchivedShopProductJob.class);
//	@Override
//	protected void executeInternal(JobExecutionContext ctx)
//			throws JobExecutionException {
//		String productId = ctx.getMergedJobDataMap().get(PRODUCT_ID) + "";
//		String shopId = ctx.getMergedJobDataMap().get(SHOP_ID) + "";
//
//		if(StringUtils.isNotEmpty(productId) && StringUtils.isNotEmpty(shopId)){
//            productDao.archivedShopProduct(productId,shopId);
//			LOG.info("自动下架拿样商品完成productId[" + productId + "]shopId[" + shopId + "]");
//		}
//	}
//
//	/**
//	 * 增加自动下架小店商品任务
//     * @param id 任务ID
//	 * @param productId 商品ID
//	 * @param shopId 小店ID
//	 * @param endDate 自动下架时间
//	 */
//	public static void pushArchivedShopProductJob(String id, String productId, String shopId, Date endDate){
//		String jobName = JOB_NAME_PRIFIX + id;
//		Map<String,String> param = new HashMap<>();
//		param.put(PRODUCT_ID,productId);
//		param.put(SHOP_ID, shopId);
//        StringBuffer desc = new StringBuffer("自动下架小店商品任务：");
//        desc.append("\r\n" + PRODUCT_ID + "：商品ID");
//        desc.append("\r\n" + SHOP_ID + "：小店ID");
//		QuartzUtils.pushJob(jobName, ArchivedShopProductJob.class, QuartzUtils.getCronExpression(endDate), desc.toString(), param);
//	}
//}
