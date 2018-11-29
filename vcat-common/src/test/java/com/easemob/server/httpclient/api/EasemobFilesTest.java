package com.easemob.server.httpclient.api;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by ylin on 2015/10/21.
 */
public class EasemobFilesTest {
    private EasemobFiles easemobFiles = new EasemobFiles();
    private static final Logger LOGGER = LoggerFactory.getLogger(EasemobFilesTest.class);

    @Test
    public void filesTest(){
        /**
         * 上传图片文件
         * curl示例
         * curl --verbose --header "Authorization: Bearer {token}" --header "restrict-access:true" --form file=@/Users/stliu/a.jpg
         * https://a1.easemob.com/easemob-playground/test1/chatfiles
         */
        File uploadImgFile = new File("/home/lynch/Pictures/24849.jpg");
        ObjectNode imgDataNode = easemobFiles.mediaUpload(uploadImgFile);
        if (null != imgDataNode) {
            LOGGER.info("上传图片文件: " + imgDataNode.toString());
        }
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /**
         * 下载图片文件
         * curl示例
         * curl -O -H "share-secret: " --header "Authorization: Bearer {token}" -H "Accept: application/octet-stream"
         * http://a1.easemob.com/easemob-playground/test1/chatfiles/0c0f5f3a-e66b-11e3-8863-f1c202c2b3ae
         */
        String imgFileUUID = imgDataNode.path("entities").get(0).path("uuid").asText();
        String shareSecret = imgDataNode.path("entities").get(0).path("share-secret").asText();
        File downloadedImgFileLocalPath = new File(uploadImgFile.getPath().substring(0, uploadImgFile.getPath().lastIndexOf(".")) + "-1.jpg");
        boolean isThumbnail = false;
        ObjectNode downloadImgDataNode = easemobFiles.mediaDownload(imgFileUUID, shareSecret, downloadedImgFileLocalPath, isThumbnail);
        if (null != downloadImgDataNode) {
            LOGGER.info("下载图片文件: " + downloadImgDataNode.toString());
        }
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /**
         * 下载缩略图
         * curl示例
         * curl -O -H "thumbnail: true" -H "share-secret: {secret}" -H "Authorization: Bearer {token}" -H "Accept: application/octet-stream"
         * http://a1.easemob.com/easemob-playground/test1/chatfiles/0c0f5f3a-e66b-11e3-8863-f1c202c2b3ae
         */
        File downloadedLocalPathThumnailImg = new File(uploadImgFile.getPath().substring(0, uploadImgFile.getPath().lastIndexOf(".")) + "-2.jpg");
        isThumbnail = true;
        ObjectNode downloadThumnailImgDataNode = easemobFiles.mediaDownload(imgFileUUID, shareSecret, downloadedLocalPathThumnailImg, isThumbnail);
        if (null != downloadThumnailImgDataNode) {
            LOGGER.info("下载缩略图: " + downloadThumnailImgDataNode.toString());
        }

        /**
         * 上传语音文件
         * curl示例
         * curl --verbose --header "Authorization: Bearer {token}" --header "restrict-access:true" --form file=@/Users/stliu/music.MP3
         * https://a1.easemob.com/easemob-playground/test1/chatfiles
         */
        File uploadAudioFile = new File("/home/lynch/Music/music.MP3");
        ObjectNode audioDataNode = easemobFiles.mediaUpload(uploadAudioFile);
        if (null != audioDataNode) {
            LOGGER.info("上传语音文件: " + audioDataNode.toString());
        }

        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /**
         * 下载语音文件
         * curl示例
         * curl -O -H "share-secret: {secret}" --header "Authorization: Bearer {token}"
         * -H "Accept: application/octet-stream" http://a1.easemob.com/easemob-playground/test1/chatfiles/0c0f5f3a-e66b-11e3-8863-f1c202c2b3ae
         */
        String audioFileUUID = audioDataNode.path("entities").get(0).path("uuid").asText();
        String audioFileShareSecret = audioDataNode.path("entities").get(0).path("share-secret").asText();
        File audioFileLocalPath = new File(uploadAudioFile.getPath().substring(0, uploadAudioFile.getPath().lastIndexOf(".")) + "-1.MP3");
        ObjectNode downloadAudioDataNode = easemobFiles.mediaDownload(audioFileUUID, audioFileShareSecret, audioFileLocalPath, null);
        if (null != downloadAudioDataNode) {
            LOGGER.info("下载语音文件: " + downloadAudioDataNode.toString());
        }
    }
}
