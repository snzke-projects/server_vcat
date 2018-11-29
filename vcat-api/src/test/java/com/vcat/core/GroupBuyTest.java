package com.vcat.core;

import com.vcat.ApiApplication;
import com.vcat.api.service.ProductService;
import com.vcat.common.constant.ApiConstants;
import com.vcat.common.persistence.Pager;
import com.vcat.module.ec.dto.GroupBuyProductDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Code.Ai on 16/5/13.
 * Description:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApiApplication.class)
@WebIntegrationTest
@Rollback(true)
public class GroupBuyTest {
    @Autowired
    private ProductService productService;
    @Test
    @Transactional
    public void getVcatGroupBuyListTest(){
        int   count = productService.countGroupBuyProductList("","app");
        System.out.println(count);
        Pager page  = new Pager();
        page.setPageNo(1);
        page.setPageSize(ApiConstants.DEFAULT_PAGE_SIZE);
        page.setRowCount(count);
        page.doPage();
        List<GroupBuyProductDto> groupBuyList = productService.getGroupBuyProductList("",page,"app");
        System.out.println(groupBuyList.size());
    }
}
