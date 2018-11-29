package com.vcat.core;

import com.vcat.module.ec.dao.CloudImageDao;
import com.vcat.module.ec.dao.ExpressApiDao;
import com.vcat.module.ec.dao.ProductDao;
import com.vcat.module.ec.entity.CloudImage;
import com.vcat.module.ec.entity.Product;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations = { "classpath:test-context.xml" })
public class DaoTest extends AbstractJUnit4SpringContextTests {
	@Autowired
	private ProductDao productDao;
	@Autowired
	private CloudImageDao ciDao;
	@Autowired ExpressApiDao e;

	@BeforeClass
	public static void setUp() {
	}

	@Test
	public void testGet() {
		Product p = new Product();
		p.setId("575a834b1e1c4d64951a3b15b8508477");
		Product po =productDao.get(p);
		System.out.println(po);
	}
	
	@Test
	public void testInsert(){
		CloudImage i = new CloudImage();
		i.preInsert();
		i.setMagiContext("123123");
		ciDao.insert(i);
	}
	
	@Test
	public void testExp(){
		//e.insert("123212", "shengtong", "123123123", "213123333333123123123");
	}
	
	@Test
	public void testExpQuery(){
		System.out.println(e.query("shengtong", "123123123"));
	}

}
