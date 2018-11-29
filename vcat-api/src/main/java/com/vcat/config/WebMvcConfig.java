package com.vcat.config;

import java.nio.charset.Charset;
import java.util.List;

import com.vcat.api.logging.LoggingFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.vcat.common.version.ApiVersionRequestMappingHandlerMapping;

import javax.validation.ValidatorFactory;
@Configuration
public class WebMvcConfig extends DelegatingWebMvcConfiguration {
	@Value("${vcat.environment}")
	private String environment;

	@Override
	protected void addCorsMappings(CorsRegistry registry) {
		if(!"PROD".equalsIgnoreCase(environment)){
			registry.addMapping("/**").allowedHeaders("*");
		}
		super.addCorsMappings(registry);
	}

	@Bean
	public FilterRegistrationBean filterRegistrationBean() {
		FilterRegistrationBean registrationBean = new FilterRegistrationBean();
		CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
		characterEncodingFilter.setEncoding("UTF-8");
		characterEncodingFilter.setForceEncoding(true);
		registrationBean.setFilter(characterEncodingFilter);
		return registrationBean;
	}

	@Bean
	public FilterRegistrationBean loggingFilterProxy(){
		FilterRegistrationBean loggingRb = new FilterRegistrationBean(new LoggingFilter());
		loggingRb.addUrlPatterns("/*");
		return loggingRb;
	}

	@Override
	protected RequestMappingHandlerMapping createRequestMappingHandlerMapping() {
		return new ApiVersionRequestMappingHandlerMapping("v");
	}

	@Bean
	public ValidatorFactory validatorFactory() {
		return new LocalValidatorFactoryBean();
	}

	@Override
	public void configureMessageConverters(
			List<HttpMessageConverter<?>> messageConverters) {
		StringHttpMessageConverter stringConverter = new StringHttpMessageConverter(
				Charset.forName("UTF-8"));
		stringConverter.setWriteAcceptCharset(false);
		messageConverters.add(new ByteArrayHttpMessageConverter());
		messageConverters.add(stringConverter);
		messageConverters.add(new ResourceHttpMessageConverter());
		messageConverters.add(new AllEncompassingFormHttpMessageConverter());
		com.vcat.common.mapper.JsonMapper om = new com.vcat.common.mapper.JsonMapper();
		om.setSerializationInclusion(Include.ALWAYS);
		om.configure(com.fasterxml.jackson.databind.SerializationFeature.WRITE_NULL_MAP_VALUES, true);
		om.configure(com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS,false);
		MappingJackson2HttpMessageConverter con = new MappingJackson2HttpMessageConverter();
		con.setObjectMapper(om);
		messageConverters.add(con);
	}



}
