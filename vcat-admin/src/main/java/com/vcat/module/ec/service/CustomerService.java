package com.vcat.module.ec.service;

import com.tencent.xinge.Message;
import com.tencent.xinge.MessageIOS;
import com.vcat.common.persistence.Page;
import com.vcat.common.push.PushService;
import com.vcat.common.utils.DateUtils;
import com.vcat.common.utils.StringUtils;
import com.vcat.module.content.entity.Article;
import com.vcat.module.content.service.ArticleService;
import com.vcat.module.core.service.CrudService;
import com.vcat.module.ec.dao.CustomerDao;
import com.vcat.module.ec.dao.MessageDao;
import com.vcat.module.ec.entity.Customer;
import com.vcat.module.ec.entity.Level;
import com.vcat.module.ec.entity.Shop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 客户Service
 */
@Service
@Transactional(readOnly = true)
public class CustomerService extends CrudService<CustomerDao, Customer> {
    @Autowired
    private MessageDao messageDao;
    @Autowired
    private ArticleService articleService;

    @Override
    public Customer get(Customer entity) {
        return super.get(entity);
    }

    @Override
    public Customer get(String id) {
        Customer customer = new Customer();
        customer.setId(id);
        return get(customer);
    }

    /**
     * 查询用户的设备唯一Token
     * @param levels 查询条件(小店等级)
     * @param deviceType 3:安卓 4: IOS
     * @return 集合
     */
    public List<String> findCustomerPushToken(String levels,String deviceType){
        Map<String,Object> param = new HashMap<>();
        param.put("deviceType", StringUtils.isEmpty(deviceType) ? PushService.ANDROID : deviceType);
        if(StringUtils.isNotEmpty(levels)){
            param.put("levels",levels.split(","));
        }
        return findCustomerPushToken(param);
    }

    /**
     * 查询用户的设备唯一Token
     * @param param 查询条件(小店等级，设备类型)
     * @return 集合
     */
    public List<String> findCustomerPushToken(Map<String,Object> param){
        return dao.findCustomerPushToken(param);
    }

    /**
     * 根据条件推送应用消息
     * @param levels 查询条件(小店等级)
     * @param sendTime 消息发送时间
     * @param articleId 系V猫新动态文章ID
     * @return 发送的消息数量
     */
    @Transactional(readOnly = false)
    public Integer pushAppMessage(String levels,String sendTime,String articleId){
        Article article = articleService.get(articleId);

        com.vcat.module.ec.entity.Message systemMessage = new com.vcat.module.ec.entity.Message();
        systemMessage.preInsert();
        systemMessage.setTitle(article.getTitle());
        systemMessage.setThumb(article.getImage());
        systemMessage.setIntro(article.getDescription());
        systemMessage.setArticle(article);
        systemMessage.setType(com.vcat.module.ec.entity.Message.PERSONAL);
        messageDao.insert(systemMessage);

        messageDao.insertFlags(systemMessage.getId(), levels.split(","));

        List<String> androidPushTokenList = findCustomerPushToken(levels,"3");
        List<String> iosPushTokenList = findCustomerPushToken(levels,"4");
        Message message = PushService.createMessage(sendTime, StringUtils.abbr(article.getTitle(), 50));
        MessageIOS messageIOS = PushService.createMessageIOS(sendTime, StringUtils.abbr(article.getTitle(), 50));
        PushService.pushDeviceListMultiple(message, androidPushTokenList);
        PushService.pushDeviceListMultiple(messageIOS, iosPushTokenList);
        Integer count = null == androidPushTokenList || androidPushTokenList.isEmpty() ? 0 : androidPushTokenList.size();
        count += null == iosPushTokenList || iosPushTokenList.isEmpty() ? 0 : iosPushTokenList.size();

        logger.info("PUSH-INFO-推送系V猫新动态[" + count + "]条：推送目标[所有用户]推送时间[" + DateUtils.getDate("yyyy-MM-dd HH:mm:ss") + "]" + "标题[" + article.getTitle() + "]");

        return count;
    }

    public Page<Map<String,Object>> findTeamPage(Page page, Shop shop) {
        shop.setPage(page);
        page.setList(dao.findTeam(shop));
        return page;
    }

    /**
     * 设置(取消)推荐
     * @param shop
     */
    @Transactional(readOnly = false)
    public void setRecommend(Shop shop) {
        if(1 == shop.getIsRecommend()){
            dao.setRecommend(shop);
        }else{
            dao.clearRecommend(shop);
        }
    }

    /**
     * 保存推荐店铺排序
     * @param shopIdArray
     * @param recommendOrderArray
     */
    @Transactional(readOnly = false)
    public void saveRecommendOrder(String[] shopIdArray, String[] recommendOrderArray) {
        if(null == shopIdArray || null == recommendOrderArray
                || shopIdArray.length != recommendOrderArray.length){
            return;
        }
        for (int i = 0; i < shopIdArray.length; i++) {
            dao.saveRecommendOrder(shopIdArray[i],recommendOrderArray[i]);
        }
    }

    @Transactional(readOnly = false)
    public void updateRecommend(Shop shop) {
        dao.updateRecommend(shop);
    }

    public Integer getCurrentUpgradeLimit(){
        return dao.getCurrentUpgradeLimit();
    }

    @Transactional(readOnly = false)
    public void setUpgradeLimit(Level level) {
        dao.setUpgradeLimit(level);
    }

    /**
     * 根据电话号码获取用户信息
     * @param phone
     * @return
     */
    public Customer getByPhone(String phone) {
        return dao.getByPhone(phone);
    }

    public List<Customer> findListByIds(String ids) {
        String[] idArray = StringUtils.isNotBlank(ids) ? ids.split("\\|") : new String[]{};
        return dao.findListByIds(idArray);
    }
}
