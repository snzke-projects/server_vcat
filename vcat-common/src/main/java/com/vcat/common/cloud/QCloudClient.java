package com.vcat.common.cloud;

import java.io.File;
import java.io.IOException;

import com.qcloud.PicCloud;
import com.qcloud.UploadResult;
import com.vcat.common.utils.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class QCloudClient {
	private static Logger LOG = LoggerFactory.getLogger(QCloudClient.class);
	private static String userid = QCloudUtils.userid;
	public static final PicCloud PIC_CLOUD = QCloudUtils.PIC_CLOUD;


	public static boolean delImage(String id) {
		if (StringUtils.isNullBlank(id)) {
			return false;
		}
		int ret = PIC_CLOUD.delete(id);
		LOG.debug("delImage result:"+PIC_CLOUD.getError());
		return ret == 0;
	}

	public static String uploadImage(File file) {
		if (file == null) {
			return null;
		}
		UploadResult r = PIC_CLOUD.upload(file.getAbsolutePath());
		LOG.debug("upload result:"+PIC_CLOUD.getError());
		LOG.debug(PIC_CLOUD.getDownloadUrl(userid, r.fileId));
		return r == null ? null : r.fileId;
	}

	/**
	 * 根据id删除图片
	 * @param id
	 * @return
	 */
	@Deprecated
	public static boolean delImageV1(String id) {
		if (StringUtils.isNullBlank(id)) {
			return false;
		}
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		try {
			HttpPost post = new HttpPost(QCloudUtils.API_URL + "/"
					+ QCloudUtils.APP_ID + "/" + userid + "/" + id + "/del");
			StringBuffer mySign = new StringBuffer("");
			QCloudUtils.appSignOnce(userid, id, mySign);
			post.addHeader("Authorization", "QCloud " + mySign);
			response = httpClient.execute(post);
			HttpEntity responseEntity = response.getEntity();
			String responseText = EntityUtils.toString(responseEntity);
			if (0 == getReturnCode(responseText)) {
				return true;
			} else {
				LOG.error("del image failed[id=" + id + "] :" + responseText);
			}
		} catch (Exception e) {
			LOG.error("del image failed:" + e.getMessage(), e);
		} finally {
			try {
				httpClient.close();
				if (response != null) {
					response.close();
				}
			} catch (Exception e) {
				LOG.error("closing error:" + e.getMessage(), e);
			}
		}
		return false;
	}

	/**
	 * 返回id
	 * 
	 * @param file
	 * @return
	 */
	@Deprecated
	public static String uploadImageV1(File file) {
		if (file == null) {
			return null;
		}
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		try {
			HttpPost uploadFile = new HttpPost(QCloudUtils.API_URL + "/"
					+ QCloudUtils.APP_ID + "/" + userid);
			long expired = System.currentTimeMillis() / 1000 + 2592000;
			StringBuffer mySign = new StringBuffer("");
			QCloudUtils.appSign(expired, userid, mySign);
			uploadFile.addHeader("Authorization", "QCloud " + mySign);
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			builder.addBinaryBody("FileContent", file,
					ContentType.APPLICATION_OCTET_STREAM, "");
			HttpEntity multipart = builder.build();
			uploadFile.setEntity(multipart);
			response = httpClient.execute(uploadFile);
			HttpEntity responseEntity = response.getEntity();
			String responseText = EntityUtils.toString(responseEntity);
			JsonFactory jfactory = new JsonFactory();
			JsonParser jParser = jfactory.createParser(responseText);
			while (jParser.nextToken() != JsonToken.END_OBJECT) {
				if ("code" == jParser.getCurrentName()) {
					if (jParser.getValueAsInt() != 0) {
						LOG.error("upload image error:" + responseText);
					}
				}
				if ("data" == jParser.getCurrentName()) {
					jParser.nextToken();
					while (jParser.nextToken() != JsonToken.END_OBJECT) {
						if ("fileid" == jParser.getCurrentName()) {
							jParser.nextToken();
							return jParser.getValueAsString();
						}
					}
				}
			}
		} catch (Exception e) {
			LOG.error("upload image failed:" + e.getMessage(), e);
		} finally {
			try {
				httpClient.close();
				if (response != null) {
					response.close();
				}
			} catch (Exception e) {
				LOG.error("closing error:" + e.getMessage(), e);
			}
		}
		return null;
	}

	@Deprecated
	private static int getReturnCode(String json) throws JsonParseException,
			IOException {
		JsonFactory jfactory = new JsonFactory();
		JsonParser jParser = jfactory.createParser(json);
		while (jParser.nextToken() != JsonToken.END_OBJECT) {
			if ("code" == jParser.getCurrentName()) {
				return jParser.nextIntValue(-1);
			}
		}
		return -1;
	}
}
