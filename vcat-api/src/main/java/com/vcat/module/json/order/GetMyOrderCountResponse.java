package com.vcat.module.json.order;

import com.vcat.module.core.entity.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Code.Ai on 16/5/12.
 * Description:
 */
public class GetMyOrderCountResponse extends ResponseEntity {
    private Map<String,Integer> map = new HashMap<>();

    public Map<String, Integer> getMap() {
        return map;
    }

    public void setMap(Map<String, Integer> map) {
        this.map = map;
    }
}
