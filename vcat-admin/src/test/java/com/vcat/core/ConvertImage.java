package com.vcat.core;

import com.vcat.common.cloud.QCloudClient;
import com.vcat.common.cloud.QCloudUtils;
import com.vcat.common.utils.FileUtils;
import com.vcat.module.ec.dao.CustomerDao;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by ylin on 2015/12/11.
 */
@ContextConfiguration(locations = { "classpath:test-context.xml" })
public class ConvertImage extends AbstractJUnit4SpringContextTests {
    @Autowired
    private CustomerDao customerDao;
    @Test
    public void uploadAvatars() throws Exception {
        List<Map<String, Object>> list = customerDao.getAvatars("%http://wx.qlogo.cn%");
        for(Map<String, Object> map : list){
            String url = (String)map.get("avatar_url");
            String cid = (String)map.get("id");
            File file = new File(FileUtils.download(url, "ava_img_" + new Date().getTime(), "/TEMP"));
            String imgId = QCloudClient.uploadImage(file);
            customerDao.updateAvatarById(imgId, cid);
            System.out.println(cid + "--------------" + imgId);
        }
    }
}
