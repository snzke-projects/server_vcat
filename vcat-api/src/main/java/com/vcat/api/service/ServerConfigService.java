package com.vcat.api.service;

import com.vcat.module.ec.dao.ServerConfigDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ServerConfigService {
	@Autowired
	private ServerConfigDao dao;

	public String findCfgValue(String name) {
		return dao.findCfgValue(name);
	}
}
