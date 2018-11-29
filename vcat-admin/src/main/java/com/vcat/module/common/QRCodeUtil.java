package com.vcat.module.common;

import com.vcat.common.utils.SpringContextHolder;
import com.vcat.common.utils.StringUtils;
import com.vcat.common.utils.ZxingHandler;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import java.io.File;
import java.util.Calendar;
import java.util.Date;

/**
 * 二维码工具类
 */
public class QRCodeUtil {
    private static final String QR_CODE_IMAGE_FOLDER = "/qrCode";
    private static final Integer WIDTH = 300;
    private static final Integer HEIGHT = 300;
    private static Logger logger = LoggerFactory.getLogger(QRCodeUtil.class);

    /**
     * 创建二维码
     * @param content
     * @return
     */
    public static String create(String content){
        if(StringUtils.isBlank(content)){
            return null;
        }
        content = StringEscapeUtils.unescapeHtml4(content);
        ServletContext context = SpringContextHolder.getBean(ServletContext.class);
        String realPath = context.getRealPath(QR_CODE_IMAGE_FOLDER);
        File dir = new File(realPath);
        if(dir.exists()){
            deleteFiles(realPath);
        }else{
            dir.mkdir();
        }
        String fileName = new Date().getTime() + ".png";
        ZxingHandler.encode2(content,WIDTH,HEIGHT,dir.getAbsolutePath() + "/" + fileName);
        logger.info("生成二维码完成：" + dir.getAbsolutePath() + "/" + fileName);
        return QR_CODE_IMAGE_FOLDER + "/" + fileName;
    }

    /**
     * 删除今天之前生成的二维码图片
     * @param path
     * @return
     */
    private static boolean deleteFiles(String path){
        if(StringUtils.isBlank(path)){
            return false;
        }
        File dir = new File(path);
        if(!dir.isDirectory() || !dir.canWrite()){
            return false;
        }
        File[] list = dir.listFiles();
        for (int i = 0; i < list.length; i++) {
            File file = list[i];
            if(new Date(file.lastModified()).before(toDay())){
                boolean result = file.delete();
                if(!result){
                    return result;
                }
                logger.info("删除文件：" + list[i].getAbsolutePath());
            }
        }

        return true;
    }

    private static Date toDay(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        return calendar.getTime();
    }
}

