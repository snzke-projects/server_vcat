package com.vcat.api.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcat.module.ec.dao.AppVersionDao;

@Service
@Transactional(readOnly = true)
public class AppVersionService {
	public final static int ANDROID = 1;
	public final static int IOS = 2;
	@Autowired
	private AppVersionDao dao;

	public Map<String, Object> queryLastVersion(int type) {
		return dao.queryLastVersion(type);
	}
}
