package com.vcat.common.lock;

import com.vcat.common.config.Global;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Config;
import org.redisson.Redisson;
import org.redisson.RedissonClient;
import org.redisson.core.RLock;

/**
 * Created by ylin on 2015/9/1.
 */
public class DistLockHelper {
    private final static String host = Global.getConfig("redis.host");
    private final static String port = Global.getConfig("redis.port");
    private final static String password = Global.getConfig("redis.password");
    public final static int MAX_HOLD_TIME = 5;
    private static Config config = new Config();
    private static RedissonClient redissonClient;
    static {
        if(!StringUtils.isEmpty(password)) {
            config.useSingleServer().setPassword(password);
        }
        config.useSingleServer().setAddress(host+":"+port);
        config.useSingleServer().setDatabase(8);
        redissonClient = Redisson.create(config);
    }

    /**
     * Returns distributed lock instance by name.
     * @param name 锁名称
     *
     */
    public static RLock getLock(String name){
        return redissonClient.getLock(name);
    }




}
