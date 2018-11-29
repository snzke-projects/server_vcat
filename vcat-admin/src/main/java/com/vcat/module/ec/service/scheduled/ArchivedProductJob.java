package com.vcat.module.ec.service.scheduled;

import com.vcat.common.utils.DateUtils;
import com.vcat.common.utils.QuartzUtils;
import com.vcat.common.utils.StringUtils;
import com.vcat.module.ec.entity.Product;
import com.vcat.module.ec.service.ProductService;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 商品定时上架任务
 */
public class ArchivedProductJob extends QuartzJobBean {
	// 商品预购ID的键
	public static final String PRODUCT_ID_KEY = "PRODUCT_ID";
    public static final String ARCHIVED_KEY = "ARCHIVED";
	public static final String JOB_NAME_PREFIX = "ARCHIVED_P_";

	@Autowired
	private ProductService productService;
	private static Logger LOG = Logger.getLogger(ArchivedProductJob.class);

	@Override
	protected void executeInternal(JobExecutionContext ctx)
			throws JobExecutionException {
		String productId = ctx.getMergedJobDataMap().get(PRODUCT_ID_KEY)+"";
        Integer archived;
        try {
            archived = Integer.parseInt(ctx.getMergedJobDataMap().get(ARCHIVED_KEY)+"");
        } catch (NumberFormatException e) {
            LOG.error("商品自动上下架任务执行出错，下架标识获取失败：" + e.getMessage());
            return;
        }
        String runTime = DateUtils.formatDateTime(ctx.getFireTime());
		if(StringUtils.isEmpty(productId) || null == archived){
            LOG.error("商品自动上下架任务失败，商品ID["+productId+"]或下架标识["+archived+"]为空");
			return;
		}

		LOG.info("AutoForecastProductJob[" + productId + "][" + archived + "][" + runTime + "]---自动上架START");

		Product product = new Product();

		product.setId(productId);
		product.setArchived(archived);

		// 上下架商品
		productService.archived(product.getId(),product.getArchived());

		LOG.info("AutoForecastProductJob[" + productId + "][" + archived + "][" + runTime + "]---自动上架END");
	}

	/**
	 * 设置商品定时上下架任务
	 * @param productId
     * @param archived
	 * @param archivedDate
	 */
	public static void pushArchivedProductJob(String productId, Integer archived, Date archivedDate){
		Map<String,Object> param = new HashMap<>();
		param.put(PRODUCT_ID_KEY, productId);
        param.put(ARCHIVED_KEY, archived + "");
        StringBuffer desc = new StringBuffer("商品定时上下架任务：");
        desc.append("\r\n" + PRODUCT_ID_KEY + "：商品ID");
        desc.append("\r\n" + ARCHIVED_KEY + "：下架状态(0 :上架 1 :下架)");
		QuartzUtils.pushJob(JOB_NAME_PREFIX + productId, ArchivedProductJob.class, QuartzUtils.getCronExpression(archivedDate),desc.toString(), param);
	}

	/**
	 * 更新商品上下架时间
	 * @param productId
	 * @param archivedDate
	 */
	public static void updateArchivedProductJob(String productId, Date archivedDate){
		QuartzUtils.updateJob(JOB_NAME_PREFIX + productId, QuartzUtils.getCronExpression(archivedDate));
	}

	/**
	 * 删除商品上下架任务
	 * @param productId
	 */
	public static void deleteArchivedProductJob(String productId){
		QuartzUtils.deleteJob(JOB_NAME_PREFIX + productId);
	}
}
