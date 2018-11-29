package com.vcat.common.cloud;

import com.qcloud.PicCloud;
import com.vcat.common.config.Global;
import com.vcat.common.security.Cryptos;
import com.vcat.common.utils.StringUtils;

import java.util.Random;

public class QCloudUtils {
	public static final String APP_ID = Global.getConfig("qcloud.appId");
	public static final String SECRET_ID = Global.getConfig("qcloud.secretId");
	public static final String SECRET_KEY = Global
			.getConfig("qcloud.secretKey");
	public static final String API_URL = Global.getConfig("qcloud.apiUrl");
	public static final String DOMAIN = Global.getConfig("qcloud.domain");
	public static final String userid = "0";
	public static final PicCloud PIC_CLOUD = new PicCloud(Integer.valueOf(QCloudUtils.APP_ID),
			QCloudUtils.SECRET_ID, QCloudUtils.SECRET_KEY);

	public static String createOriginalDownloadUrl(String id) {
		if(StringUtils.isNullBlank(id)){
			return null;
		}
		String  url = String.format("http://%s/%d/%s/%s/original",
				new Object[]{DOMAIN, Integer.valueOf(APP_ID), userid, id});
		return url;
	}

	public static String createThumbDownloadUrl(String id,String style) {
		if(StringUtils.isNullBlank(id)){
			return null;
		}
		String url = String.format("http://%s/%d/%s/%s/%s",
				new Object[]{DOMAIN, Integer.valueOf(APP_ID), userid, id, style});
		return url;
	}

	/**实时缩放，1比1
	 * @param id
	 * @param width
     * @return
     */
	public static String createThumbDownloadUrl(String id, int width){
		return createThumbDownloadUrl(id,width,width);
	}

	/**实时缩放，自定义高宽
	 * @param id
	 * @param width
	 * @return
	 */
	public static String createThumbDownloadUrl(String id, int width, int height){
		return createOriginalDownloadUrl(id)+"?ss=1&w="+width+"&h="+height;
	}

	@Deprecated
	public static int appSign(long expired, String userid, StringBuffer mySign) {
		return appSignBase(APP_ID, SECRET_ID, SECRET_KEY, expired, userid,
				null, mySign);
	}

	@Deprecated
	public static String createOriginalDownloadUrlV1(String id) {
		if(StringUtils.isNullBlank(id)){
			return null;
		}
		return "http://" + APP_ID + "." + DOMAIN + "/" + APP_ID + "/0/" + id
				+ "/original";
	}

	@Deprecated
	public static int appSignOnce(String userid, String id, StringBuffer mySign) {
		return appSignBase(APP_ID, SECRET_ID, SECRET_KEY, 0L, userid, id,
				mySign);
	}

	@Deprecated
	private static int appSignBase(String appId, String secret_id,
			String secret_key, long expired, String userid, String id,
			StringBuffer mySign) {
		if ((empty(secret_id)) || (empty(secret_key))) {
			return -1;
		}
		String puserid = "";
		if (!empty(userid)) {
			if (userid.length() > 64) {
				return -2;
			}
			puserid = userid;
		}
		String fileid = "";
		if (!empty(id)) {
			fileid = id;
		}
		long now = System.currentTimeMillis() / 1000L;
		int rdm = Math.abs(new Random().nextInt());
		String plainText = "a=" + appId + "&k=" + secret_id + "&e=" + expired
				+ "&t=" + now + "&r=" + rdm + "&u=" + puserid + "&f="
				+ fileid.toString();

		byte[] bin = hashHmac(plainText, secret_key);

		byte[] all = new byte[bin.length + plainText.getBytes().length];
		System.arraycopy(bin, 0, all, 0, bin.length);
		System.arraycopy(plainText.getBytes(), 0, all, bin.length,
				plainText.getBytes().length);

		mySign.append(Base64Util.encode(all));

		return 0;
	}

	@Deprecated
	private static byte[] hashHmac(String plain_text, String accessKey) {
		return Cryptos.hmacSha1(plain_text.getBytes(), accessKey.getBytes());
	}

	@Deprecated
	private static boolean empty(String s) {
		if ((s == null) || (s.trim().equals("")) || (s.trim().equals("null"))) {
			return true;
		}
		return false;
	}
}
