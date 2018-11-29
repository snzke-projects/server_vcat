package com.vcat.module.json.product;

import com.vcat.module.core.entity.ResponseEntity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Code.Ai on 16/5/13.
 * Description:
 */
public class ShareGroupBuyInfoResponse extends ResponseEntity implements Serializable {
    private Map<String, Object> result = new HashMap<>();

    public Map<String, Object> getResult() {
        return result;
    }

    public void setResult(Map<String, Object> result) {
        this.result = result;
    }
}
