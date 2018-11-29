package com.vcat.api.web;

import com.vcat.api.service.CustomerService;
import com.vcat.api.service.ShopService;
import com.vcat.api.web.validation.ValidateParams;
import com.vcat.common.constant.ApiMsgConstants;
import com.vcat.config.CacheConfig;
import com.vcat.module.core.entity.MsgEntity;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.vcat.common.version.ApiVersion;
import com.vcat.common.web.rest.RestBaseController;
import com.vcat.module.core.entity.User;

import javax.validation.Valid;
import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@RestController
@ApiVersion({1,2})
public class TestController extends RestBaseController {
	@Autowired
	private ShopService shopService;

	@RequestMapping(value = "/anon/cacheTest", method = RequestMethod.GET)
	@CacheEvict(value = CacheConfig.GET_IOS_VERSION_CACHE, cacheManager = "apiCM", allEntries = true)
	public String cacheTest() {
		return new String("test");
	}

	@RequestMapping(value = "/anon/trxTest")
	public void trxTest(){
		shopService.updateAdvanced("0002964cf39942ddb9d7f3a352ec50ab",1);
	}



	/**
	 * 验证的例子
	 * @param testType
	 * @param myObject
     * @return
     */
	@ValidateParams
	@RequestMapping(value = "/anon/param/post/test")
	public Object objectParamPost(
			@Length(max = 1) @RequestParam(value = "type", required = true) String testType,
			@Valid @RequestBody MyObject myObject){
		return new ObjectResult();
	}

	@ValidateParams
	@RequestMapping(value = "/anon/param/get/test",
	method = RequestMethod.GET)
	public Object objectParamGet(
			@Size(max = 1) @RequestParam("type") String testType
	){
		return new ObjectResult();
	}
}

class MyObject {
	@NotNull
	@Size(max = 1)
	private String id;
	@NotNull
	private String name;
	@NotNull
	private String code;
	@AssertFalse
	private boolean prod;

	/**
	 * 不推荐用这种方式，一般自定义验证注解(validator annotation),参见com.vcat.api.web.validation
     */
	@AssertTrue(message="code必须等于test")
	public boolean isValid() {
		return this.code.equals("test");
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public boolean isProd() {
		return prod;
	}

	public void setProd(boolean prod) {
		this.prod = prod;
	}
}

class ObjectResult extends MsgEntity {
	public List getResultList() {
		return resultList;
	}

	public void setResultList(List resultList) {
		this.resultList = resultList;
	}

	private List resultList;

	public ObjectResult(){
		super(ApiMsgConstants.SUCCESS_MSG,ApiMsgConstants.SUCCESS_CODE);
		resultList = new ArrayList<>();
		resultList.add("aaaa");
		resultList.add("bbbb");
	}
}
