package com.vcat.module.ec.service;

import com.tencent.common.RandomStringGenerator;
import com.vcat.module.core.service.CrudService;
import com.vcat.module.ec.dao.InvitationCodeDao;
import com.vcat.module.ec.entity.InvitationCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class InvitationCodeService extends CrudService<InvitationCodeDao, InvitationCode> {
    /**
     * 批量插入邀请码
     * @param count
     * @return
     */
    @Transactional(readOnly = false)
    public int batchInsert(Integer count){
        List<String> codeList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            codeList.add(RandomStringGenerator.getRandomCharByLength(6));
        }
        dao.batchInsert(codeList);
        return count;
    }

    /**
     * 停用/启用该邀请码
     * @param invitationCode
     */
    @Transactional(readOnly = false)
    public void stopCode(InvitationCode invitationCode) {
        boolean isStop = invitationCode.getStatus() != 2;
        if(isStop){
            invitationCode.setStatus(2);
        }else{
            invitationCode.setStatus(dao.getUsedById(invitationCode.getId()));
        }
        dao.stopCode(invitationCode);
    }
}
