package com.vcat.api.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vcat.common.constant.ApiConstants;
import com.vcat.common.constant.ApiMsgConstants;
import com.vcat.config.CacheConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vcat.api.service.AppVersionService;
import com.vcat.common.version.ApiVersion;
import com.vcat.common.web.rest.RestBaseController;

@RestController
@ApiVersion({1,2})
public class AppVersionController extends RestBaseController {
	@Autowired
	private AppVersionService service;

	/**
	 * 返回android最新版本
	 * @return
	 */
	@RequestMapping("/anon/apk/version/last")
	@Cacheable(value = CacheConfig.GET_APK_VERSION_CACHE, keyGenerator = "keyGenerator", cacheManager = "apiCM")
	public Object getApkVersion() {
		Map<String, Object> ret = service
				.queryLastVersion(AppVersionService.ANDROID);
		Map<String, Object> version = new HashMap<String, Object>();
		version.put("version", String.valueOf(ret.get("version")));
		version.put("url", String.valueOf(ret.get("url")));
		version.put("force", ret.get("force"));
		version.put("note", String.valueOf(ret.get("note")));
		version.put(ApiConstants.CODE, String.valueOf(ApiMsgConstants.SUCCESS_CODE));
		return version;
	}

	/**
	 * 返回ios最新版本
	 * @return
	 */
	@RequestMapping("/anon/ios/version/last")
	@Cacheable(value = CacheConfig.GET_IOS_VERSION_CACHE, keyGenerator = "keyGenerator", cacheManager = "apiCM")
	public Object getIosVersion() {
		Map<String, Object> ret = service
				.queryLastVersion(AppVersionService.IOS);
		Map<String, Object> version = new HashMap<String, Object>();
		version.put("version", String.valueOf(ret.get("version")));
		version.put("url", String.valueOf(ret.get("url")));
		version.put("force", ret.get("force") );
		version.put("note", String.valueOf(ret.get("note")));
		version.put(ApiConstants.CODE, String.valueOf(ApiMsgConstants.SUCCESS_CODE));
		return version;
	}
}
