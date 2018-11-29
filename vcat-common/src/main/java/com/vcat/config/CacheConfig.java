package com.vcat.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ylin on 2016/4/27.
 */
@EnableCaching
public class CacheConfig extends CachingConfigurerSupport {
    public static final String GET_SERVICE_ADDRESS_CACHE = "getServiceAddressCache";
    public static final String GET_IOS_VERSION_CACHE = "getIosVersionCache";
    public static final String GET_APK_VERSION_CACHE = "getApkVersionCache";
    public static final String GET_APP_CONFING_CACHE = "getAppConfigCache";
    public static final String GET_COPYWRITE_LIST_CACHE = "getCopywriteListCache";
    public static final String GET_INDEX_IMAGE_LIST_CACHE = "getIndexImageListCache";
    public static final String GET_NEWEST_CACHE = "getNewestCache";
    public static final String GET_CATEGORY_LIST_CACHE = "getCategoryListCache";
    public static final String GET_NEW_PRODUCT_LIST_CACHE = "getNewProductListCache";
    public static final String GET_RESERVE_PRODUCT_LIST_CACHE = "getReserveProductListCache";
    public static final String GET_LEROY_PRODUCT_LIST_CACHE = "getLeroyProductListCache";
    public static final String GET_CATEGORY_PRODUCT_LIST_CACHE = "getCategoryProductListCache";
    public static final String GET_VCAT_SHOP_CONFIG_CACHE = "getVcatShopConfigCache";
    public static final String FIND_LIST_BY_PRODUCT_ID_CACHE = "findListByProductIdCache";
    public static final String FIND_LIST_BY_PRODUCT_CACHE = "findListByProductCache";
    public static final String GET_LEROY_INFO_CACHE = "getLeroyerInfoCache";
    public static final String GET_PARENT_SHOP_CAHCE = "getParentShopCache";
    public static final String GET_VCAT_SHOP_PRODUCT_LIST_CACHE = "getVcatShopProductListCache";
    public static final String GET_REVIEW_LIST_CACHE = "getReviewListCache";
    public static final String GET_SELLER_PRODUCT_DETAIL_CACHE = "getSellerProductDetailCache";
    public static final String GET_VCAT_PRODUCT_DETAIL_CACHE = "getVcatProductDetailCache";

    /**
     * ！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
     * ！！！！！！！！！！！！缓存的参数需要自己实现toString方法并且要实现Serializable接口！！！！！！！！！！！！！
     * ！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
     *      @Override
     *      public String toString() {
     *           return ToStringBuilder.reflectionToString(this,ToStringStyle.JSON_STYLE);
     *      }
     * 过期时间单位秒
     * @Cacheable(value = "getServiceAddressCache", keyGenerator = "keyGenerator", cacheManager = "apiCM")
     */
    private void initExpiresMap(){
        if(environment.equalsIgnoreCase("prod")) {
            expires.put(GET_SERVICE_ADDRESS_CACHE, 60 * 60 * 24 * 14l);//14天
            expires.put(GET_IOS_VERSION_CACHE, 60 * 60 * 24 * 7l);//7天
            expires.put(GET_APK_VERSION_CACHE, 60 * 60 * 24 * 7l);
            expires.put(GET_APP_CONFING_CACHE, 60 * 60 * 24 * 14l);
            expires.put(GET_COPYWRITE_LIST_CACHE, 60 * 60 * 24 * 14l);
            expires.put(GET_INDEX_IMAGE_LIST_CACHE, 60 * 60 * 24 * 14l);
            // 获取发现最新3条数据, 1小时
            expires.put(GET_NEWEST_CACHE, 60 * 60L);
            //分类
            expires.put(GET_CATEGORY_LIST_CACHE, 60 * 60l);
            //新品
            expires.put(GET_NEW_PRODUCT_LIST_CACHE, 60 * 60l);
            //预售商品
            expires.put(GET_RESERVE_PRODUCT_LIST_CACHE, 60 * 60l);
            //查询V猫庄园商品
            expires.put(GET_LEROY_PRODUCT_LIST_CACHE, 60 * 60l);
            //拿样的商品列表
            expires.put(GET_CATEGORY_PRODUCT_LIST_CACHE, 60 * 60l);
            //v猫商场首页广告位配置信息
            expires.put(GET_VCAT_SHOP_CONFIG_CACHE, 60 * 60l);
            //ProductImage缓存
            expires.put(FIND_LIST_BY_PRODUCT_ID_CACHE, 60 * 60l);
            //ProductProperty属性缓存
            expires.put(FIND_LIST_BY_PRODUCT_CACHE, 60 * 60l);
            // 获取庄园主信息
            expires.put(GET_LEROY_INFO_CACHE, 60 * 10L);
            // 根据邀请码获取上家店铺信息
            expires.put(GET_PARENT_SHOP_CAHCE, 60 * 10L);
            //卖家获取V猫商场商品列表
            expires.put(GET_VCAT_SHOP_PRODUCT_LIST_CACHE, 60 * 5l);
            //获取评价列表 (商品详情页使用)
            expires.put(GET_REVIEW_LIST_CACHE, 60 * 15l);
            //获取商品的详细信息,卖家从v猫商城,从app点击进入
            expires.put(GET_VCAT_PRODUCT_DETAIL_CACHE, 60 * 5l);
            //获取商品的详细信息,买家使用,从分享进入，并不需要登录
            expires.put(GET_SELLER_PRODUCT_DETAIL_CACHE, 60 * 5l);
        }
    }


    // Number of seconds before expiration. Defaults to unlimited (0)
    // 默认1秒过期
    private final static long DEFAULT_EXPIRATION_TIME = 1l;

    @Value("${redis.cache.host}")
    private String host;

    @Value("${redis.cache.port}")
    private int port;

    @Value("${redis.cache.password}")
    private String password;

    @Value("${vcat.environment}")
    private String environment = "prod";

    @Value("classpath:${ehcache.configFile}")
    private Resource ehCfg;

    @Value("${redis.host}")
    private String host2;

    @Value("${redis.port}")
    private int port2;

    @Value("#{'${redis.password}'!=''?'${redis.password}':null}")
    private String password2;

    @Value("${redis.cache.index}")
    private int dbIndex;

    @Value("${redis.index}")
    private int tokenIndex;

    private Map<String, Long> expires = new HashMap<>(20);

    @Bean
    public KeyGenerator keyGenerator() {
        return (o, method, objects) -> {
            // This will generate a unique key of the class name, the method name,
            // and all method parameters appended.
            StringBuilder sb = new StringBuilder("_"+environment+"_");//添加环境避免冲突
            sb.append(o.getClass().getName()).append(".");
            sb.append(method.getName()).append("_");
            for (Object obj : objects) {
                if(obj != null){
                    sb.append(obj.toString());
                }else{
                    sb.append("null");
                }
                sb.append("+");
            }
            return sb.toString();
        };
    }

    @Bean
    public JedisConnectionFactory redisConnectionFactory() {
        JedisConnectionFactory factory = new JedisConnectionFactory();
        factory.setHostName(host);
        factory.setPort(port);
        factory.setPassword(password);
        factory.setDatabase(dbIndex);//使用第几个数据库
        return factory;
    }

    @Bean(name = "apiCM")
    public CacheManager cacheManager(RedisTemplate redisTemplate) {
        initExpiresMap();
        RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate);
        cacheManager.setDefaultExpiration(DEFAULT_EXPIRATION_TIME);
        cacheManager.setExpires(expires);
        return cacheManager;
    }

    @Bean(name = "cacheManager")
    public EhCacheManagerFactoryBean ehCacheManager() {
        EhCacheManagerFactoryBean ehCacheManagerFactoryBean = new EhCacheManagerFactoryBean();
        ehCacheManagerFactoryBean.setConfigLocation(ehCfg);
        return ehCacheManagerFactoryBean;
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory cf) {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<String, String>();
        redisTemplate.setConnectionFactory(cf);
        return redisTemplate;
    }

    @Bean(name="jedisPoolConfig")
    public JedisPoolConfig jedisPoolConfig(){
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(300);
        jedisPoolConfig.setMaxTotal(60000);
        jedisPoolConfig.setTestOnBorrow(true);
        return jedisPoolConfig;
    }

    @Bean
    public JedisPool jedisPool(){
        JedisPool jedisPool = new JedisPool(jedisPoolConfig(),host2,port2,2000,password2,tokenIndex);
        return jedisPool;
    }
}
