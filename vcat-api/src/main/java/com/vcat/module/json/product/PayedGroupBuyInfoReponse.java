package com.vcat.module.json.product;

import com.vcat.module.core.entity.ResponseEntity;
import com.vcat.module.ec.dto.GroupBuySponsorDto;

import java.util.*;

/**
 * Created by Code.Ai on 16/5/13.
 * Description:
 */
public class PayedGroupBuyInfoReponse extends ResponseEntity {
    private Map<String, Object> result = new HashMap<>();

    public Map<String, Object> getResult() {
        return result;
    }

    public void setResult(Map<String, Object> result) {
        this.result = result;
    }
}
