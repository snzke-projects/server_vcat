package com.vcat.module.ec.dao;

import com.vcat.common.persistence.annotation.MyBatisDao;
import org.apache.ibatis.annotations.Param;

/**
 * Created by ylin on 2016/2/23.
 */
@MyBatisDao
public interface ShortUrlDao {
    void insertShortUrl(@Param("id") String id, @Param("longUrl") String longUrl,
                        @Param("shortUrl") String shortUrl);
    String getLongUrl( @Param("shortUrl") String shortUrl);
    String getShortUrl( @Param("longUrl") String longUrl);
}
