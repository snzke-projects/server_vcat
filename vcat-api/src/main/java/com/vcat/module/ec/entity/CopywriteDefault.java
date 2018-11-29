package com.vcat.module.ec.entity;

import com.vcat.module.core.entity.DataEntity;

/**
 * Created by Code.Ai on 16/4/11.
 * Description:
 */
public class CopywriteDefault extends DataEntity<CopywriteDefault> {

    private static final long serialVersionUID = 2564716328832206608L;
    private Shop shop;
    private Product product;
    private Copywrite copywrite;

    public Copywrite getCopywrite() {
        return copywrite;
    }

    public void setCopywrite(Copywrite copywrite) {
        this.copywrite = copywrite;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }
}
