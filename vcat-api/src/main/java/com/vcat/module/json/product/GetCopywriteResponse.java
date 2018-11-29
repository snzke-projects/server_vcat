package com.vcat.module.json.product;

import com.vcat.common.persistence.Pager;
import com.vcat.module.core.entity.ResponseEntity;

/**
 * Created by Code.Ai on 16/4/11.
 * Description:
 */
public class GetCopywriteResponse extends ResponseEntity {
    private Pager page;
    public Pager getPage() {
        return page;
    }
    public void setPage(Pager page) {
        this.page = page;
    }
}
