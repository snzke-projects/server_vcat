package com.vcat.common;

import com.vcat.common.utils.StringUtils;
import org.junit.Test;

import com.vcat.common.express.ExpressUtils;

public class ExpressUtilsTest {
	@Test
	public void test() throws Exception {
		System.out.println(ExpressUtils.queryExpress("shentong",
				"968701410621"));
	}
	
	@Test
	public void testExpress() {
        String[] nos = new String[]{
                "350581370507"
        };
        for(String no :nos){
            ExpressUtils.subscribeExpress("huitongkuaidi",no);
        }
	}

	@Test
	public void filterBlankTest(){
		System.out.println("123123123123\t1");
		System.out.println(StringUtils.filterBlank("123123123123\t1"));
	}
}
