package com.vcat.module.ec.service;

import com.vcat.module.core.service.CrudService;
import com.vcat.module.ec.dao.MessageRegisterDao;
import com.vcat.module.ec.entity.MessageRegister;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MessageRegisterService extends CrudService<MessageRegisterDao, MessageRegister> {
	@Transactional(readOnly = false)
	public Integer activate(MessageRegister messageRegister) throws Exception {
		return dao.activate(messageRegister);
	}
}
