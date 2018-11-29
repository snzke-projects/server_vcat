package com.vcat.core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vcat.ApiApplication;
import com.vcat.common.config.Global;
import com.vcat.common.persistence.Pager;
import com.vcat.module.core.entity.FundOperLog;
import com.vcat.module.ec.dao.*;
import com.vcat.module.ec.dto.OrderDto;
import com.vcat.module.ec.dto.OrderItemDto;
import com.vcat.module.ec.dto.ProductItemDto;
import com.vcat.module.ec.entity.*;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApiApplication.class)
@WebIntegrationTest
public class DaoTest extends AbstractJUnit4SpringContextTests {
	@Autowired
	private ProductDao productDao;
	//@Autowired
	//private CloudImageDao ciDao;
	@Autowired ExpressApiDao e;
	@Autowired
	CouponShopDao csDao;

    @Autowired
    ShopDao shopDao;
	@Autowired
	OrderDao orderDao;
	@Autowired
	CopywriteDefaultDao copywriteDefaultDao;
	@Autowired
	CopywriteDao copywriteDao;
	@Autowired
	FundOperLogDao fundOperLogDao;
	@Autowired
	ShopFundDao shopFundDao;
	@Autowired
	CustomerDao customerDao;
	@Autowired
	OrderItemDao orderItemDao;
	@Autowired
	ShopInfoDao shopInfoDao;
	private static Logger log = Logger.getLogger(DaoTest.class);

	@BeforeClass
	public static void setUp() {
	}

	@Test
	public void testGetCS(){
		Integer bl = csDao.getInviteeCouponCount("07fd9ed7edd14170b576b538d9852672");
		System.out.println(bl);
	}

	@Test
	public void testGet() {
		Product p = new Product();
		p.setId("575a834b1e1c4d64951a3b15b8508477");
		Product po =productDao.get(p);
		System.out.println(po);
	}
	
	//@Test
	//public void testInsert(){
	//	CloudImage i = new CloudImage();
	//	i.preInsert();
	//	i.setMagiContext("123123");
	//	ciDao.insert(i);
	//}
	
	@Test
	public void testExp(){
		//e.insert("123212", "shengtong", "123123123", "213123333333123123123");
	}
	
	@Test
	public void testExpQuery(){
		System.out.println(e.query("shengtong", "123123123"));
	}

	@Test
	public void test1(){
		//获取店铺总收入
		BigDecimal totalSaled1 = orderDao.getTotalSaled("982943d9a198479a972f20babffe9278");
		System.out.println(totalSaled1.intValue());
	}
	@Test
	public void test2(){
		Product product = new Product();
		product.setId("956d9146c26440d2a1ca19176dd1bec7");
		Shop shop = new Shop();
		shop.setId("982943d9a198479a972f20babffe9278");
		CopywriteDefault copywriteDefault = new CopywriteDefault();
		copywriteDefault.setShop(shop);
		copywriteDefault.setProduct(product);
		copywriteDefault = copywriteDefaultDao.get(copywriteDefault);
		System.out.println(copywriteDefault.getProduct().getId());
	}

	/**
	 * 2016.04.12
	 * 给23号到30到部署之前下单的用户增加可提现收入
	 */
	@Test
	public void test3(){
		// 根据订单号获取订单信息
		Order order = new Order();
		order.setOrderNumber("4542016031922254484");
		order = orderDao.get(order);
		System.out.println(order.getId());
		//
	}
	@Test
	public void test4(){
		//List<Map<String,String>> ss = copywriteDao.getShopCopywriteImages("982943d9a198479a972f20babffe9278","465sd4fsdfgdfg464s5d6fgsd54gd6df");

	}

	@Test
	public void test5(){
		int n = 31;
		int sum = n * (--n);
		for(int i = n ; i > 1 ; i--) {
			sum *= (--n);
		}
		//System.out.println(n);

		System.out.println(sum);
	}

	@Test
	public void test6(){
		List<FundOperLog> list = fundOperLogDao.getSaleLogs();
		for(FundOperLog fundOperLog : list){
			System.out.println(fundOperLog.getNote());
			Shop shop = new Shop();
			String shopId = fundOperLog.getShopFundId();
			// 获取我的收入
			ShopFund upFund = new ShopFund();
			upFund.setId(shopId);
			upFund = shopFundDao.get(upFund);

			//BigDecimal saleEarning = fundOperLog.getFund();
			// 计算销售奖励
			BigDecimal saleEarning = BigDecimal.ZERO;
			// 计算分红收入
			BigDecimal bonusEarning = BigDecimal.ZERO;
			// 计算二级分红收入
			BigDecimal secondBonusEarning = BigDecimal.ZERO;
			Order order = new Order();
			order.setId(fundOperLog.getRelateId());
			order = orderDao.get(order);
			//String productName = order.getProductName();
			// 将本店的待确认销售收入改为可提现收入

			// 增加卖家收入(减少待确认收入,增加可提现收入)
			shopFundDao.checkShipping(shopId, saleEarning, BigDecimal.ZERO);
			List<ProductItemDto> itemList   = orderDao.findProductItemList(fundOperLog.getRelateId());
			int                  requantity = 0;
			for(ProductItemDto item : itemList){
				if (Refund.REFUND_STATUS_COMPLETED.equals(item.getReFundStatus())
						&& Refund.RETURN_STATUS_COMPLETED.equals(item.getReFundStatus())) {
					requantity = item.getReQuantity() == null ? 0 : item.getReQuantity();
				}
				if (item.getItemSaleEarning() != null)
					saleEarning = saleEarning
							.add(item.getItemSaleEarning()
									.multiply(
											new BigDecimal(item.getQuantity()
													- requantity)));
				if (item.getItemBonusEarning() != null)
					bonusEarning = bonusEarning
							.add(item.getItemBonusEarning()
									.multiply(
											new BigDecimal(item.getQuantity()
													- requantity)));
				if (item.getItemSecondBonusEarning() != null)
					secondBonusEarning = secondBonusEarning
							.add(item.getItemSecondBonusEarning()
									.multiply(
											new BigDecimal(item.getQuantity()
													- requantity)));

				log.debug("增加店铺[" + shopId + "],销售收入[" + saleEarning
						+ "],订单号为[" + order.getOrderNumber()+"]");
				saveToFile("增加店铺[" + shopId + "],销售收入[" + saleEarning
						+ "],订单号为[" + order.getOrderNumber()+"]\n");
				// 记录销售收入log
				fundOperLogDao.insertSale(
						FundOperLog.create(
								shopId,
								FundFieldType.SALE_HOLD_FUND,
								saleEarning.negate(),
								upFund.getSaleHoldFund().subtract(saleEarning),
								fundOperLog.getRelateId(),
								FundOperLog.NORMAL_INCOME,
								"订单[" + order.getOrderNumber() + "]商品[" + order.getProductName() + "]确认收货，减去销售待确认资金[" + saleEarning + "]"));
				System.out.println("订单[" + order.getOrderNumber() + "]商品[" + order.getProductName() + "]确认收货，减去销售待确认资金[" + saleEarning + "]");
				saveToFile("订单[" + order.getOrderNumber() + "]商品[" + order.getProductName() + "]确认收货，减去销售待确认资金[" + saleEarning + "]\n");
				fundOperLogDao.insertSale(
						FundOperLog.create(
								shopId,
								FundFieldType.SALE_AVAILABLE_FUND,
								saleEarning,
								upFund.getSaleAvailableFund().add(saleEarning),
								fundOperLog.getRelateId(),
								FundOperLog.NORMAL_INCOME,
								"订单[" + order.getOrderNumber() + "]商品[" + order.getProductName() + "]确认收货，增加销售可提现资金[" + saleEarning + "]"));
				System.out.println("订单[" + order.getOrderNumber() + "]商品[" + order.getProductName() + "]确认收货，增加销售可提现资金[" + saleEarning + "]");
				saveToFile("订单[" + order.getOrderNumber() + "]商品[" + order.getProductName() + "]确认收货，增加销售可提现资金[" + saleEarning + "]\n");
				// 将上家待确认分红收入改为可提现收入
				Shop parentShop = shopDao.get(shop.getParentId());
				if(parentShop != null && bonusEarning.compareTo(BigDecimal.ZERO) != 0){
					log.debug("增加上级店铺[" + parentShop.getId() + "],分红收入[" + bonusEarning
							+ "],订单号为[" + order.getOrderNumber()+"]");
					saveToFile("增加上级店铺[" + parentShop.getId() + "],分红收入[" + bonusEarning
							+ "],订单号为[" + order.getOrderNumber()+"]\n");
					// 获取上级卖家的收入
					ShopFund parentUpFund = new ShopFund();
					parentUpFund.setId(parentShop.getId());
					parentUpFund	= shopFundDao.get(parentUpFund);
					shopFundDao.addBonusAvaiableFund(parentShop.getId(), bonusEarning);
					// 记录分红收入log
					fundOperLogDao.insertBonus(
						FundOperLog.create(
							parentShop.getId(),
							FundFieldType.BONUS_HOLD_FUND,
							bonusEarning.negate(),
							parentUpFund.getBonusHoldFund().subtract(bonusEarning),
							shopId,FundOperLog.NORMAL_INCOME,
							"订单[" + order.getOrderNumber() + "]商品[" + order.getProductName() + "]确认收货，减去上级卖家分红待确认资金[" + bonusEarning + "]"));
					System.out.println("订单[" + order.getOrderNumber() + "]商品[" + order.getProductName() + "]确认收货，减去上级卖家分红待确认资金[" + bonusEarning + "]");
					saveToFile("订单[" + order.getOrderNumber() + "]商品[" + order.getProductName() + "]确认收货，减去上级卖家分红待确认资金[" + bonusEarning + "]\n");
					fundOperLogDao.insertBonus(
						FundOperLog.create(
							parentShop.getId(),
							FundFieldType.BONUS_AVAILABLE_FUND,
							bonusEarning,
							parentUpFund.getBonusAvailableFund().add(bonusEarning),
							shopId,FundOperLog.NORMAL_INCOME,
							"订单[" + order.getOrderNumber() + "]商品[" + order.getProductName() + "]确认收货，增加上级卖家分红可提现资金[" + bonusEarning + "]"));
					System.out.println("订单[" + order.getOrderNumber() + "]商品[" + order.getProductName() + "]确认收货，增加上级卖家分红可提现资金[" + bonusEarning + "]");
					saveToFile("订单[" + order.getOrderNumber() + "]商品[" + order.getProductName() + "]确认收货，增加上级卖家分红可提现资金[" + bonusEarning + "]\n");
				}
				// 将上上家待确认收入改为可提现收入
				if(parentShop != null){
					Shop parentParentShop = shopDao.get(parentShop.getParentId());
					if(parentParentShop != null && secondBonusEarning.compareTo(BigDecimal.ZERO) != 0){
						log.debug("增加上上级店铺[" + parentParentShop.getId() + "],二级分红收入[" + secondBonusEarning
								+ "],订单号为[" + order.getOrderNumber() + "]");
						saveToFile("增加上上级店铺[" + parentParentShop.getId() + "],二级分红收入[" + secondBonusEarning
								+ "],订单号为[" + order.getOrderNumber() + "]\n");
						// 获取上级卖家的收入
						ShopFund parentParentUpFund = new ShopFund();
						parentParentUpFund.setId(parentParentShop.getId());
						parentParentUpFund = shopFundDao.get(parentParentUpFund);
						shopFundDao.addBonusAvaiableFund(parentParentShop.getId(), secondBonusEarning);
						// 记录上上级分红收入
						fundOperLogDao.insertBonus(
							FundOperLog.create(
								parentParentShop.getId(),
								FundFieldType.BONUS_HOLD_FUND,
								secondBonusEarning.negate(),
								parentParentUpFund.getBonusHoldFund().subtract(secondBonusEarning),
								parentShop.getId(),
								FundOperLog.SECOND_BONUS,
								"订单[" + order.getOrderNumber() + "]商品[" + order.getProductName() + "]确认收货，减去上上级卖家分红待确认资金[" + secondBonusEarning + "]"));
						System.out.println("订单[" + order.getOrderNumber() + "]商品[" + order.getProductName() + "]确认收货，减去上上级卖家分红待确认资金[" + secondBonusEarning + "]");
						saveToFile("订单[" + order.getOrderNumber() + "]商品[" + order.getProductName() + "]确认收货，减去上上级卖家分红待确认资金[" + secondBonusEarning + "]\n");
						fundOperLogDao.insertBonus(
							FundOperLog.create(
								parentParentShop.getId(),
								FundFieldType.BONUS_AVAILABLE_FUND,
								secondBonusEarning,
								parentParentUpFund.getBonusAvailableFund().add(secondBonusEarning),
								parentShop.getId(),
								FundOperLog.SECOND_BONUS,
								"订单[" + order.getOrderNumber() + "]商品[" + order.getProductName() + "]确认收货，增加上上级卖家分红可提现资金[" + secondBonusEarning + "]"));
						System.out.println("订单[" + order.getOrderNumber() + "]商品[" + order.getProductName() + "]确认收货，增加上上级卖家分红可提现资金[" + secondBonusEarning + "]");
						saveToFile("订单[" + order.getOrderNumber() + "]商品[" + order.getProductName() + "]确认收货，增加上上级卖家分红可提现资金[" + secondBonusEarning + "]\n");
					}
				}
			}
		}
	}

	private void saveToFile(String str) {
		File           file     = new File("/Users/codeai/Desktop/result.txt");
		FileWriter writer;
		try {
			if(!file.exists()){
				file.createNewFile();
			}
			writer = new FileWriter(file,true);
			writer.write(str);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test7(){
		String envirnment = Global.getConfig("vcat.environment");
		System.out.println(envirnment);
	}

	@Test
	public void test8(){
		String str = "[\n" +
				"  {\n" +
				"    \"id\": \"04750e98804344a68ede31bcd90ad661\",\n" +
				"    \"order_id\": \"66869b8044304ee1aeda0a4d2cd658a5\",\n" +
				"    \"product_item_id\": \"64fb8eb507e84645a0b9cca2dcba3d7a\",\n" +
				"    \"quantity\": 1,\n" +
				"    \"promotion_quantity\": 0,\n" +
				"    \"item_price\": 27.40,\n" +
				"    \"purchase_price\": 21.0000,\n" +
				"    \"sale_earning\": 6.60,\n" +
				"    \"bonus_earning\": 3.00,\n" +
				"    \"point\": 3.4000,\n" +
				"    \"single_coupon\": null,\n" +
				"    \"shop_id\": \"298a46cf5f784be39e84185d90d8c4d0\",\n" +
				"    \"order_item_type\": 2,\n" +
				"    \"supplier_id\": \"09412753aa55489fb85e316487973789\",\n" +
				"    \"brand_id\": \"bd8ad19bf8cd4d7499c61fbf6e74f413\",\n" +
				"    \"product_id\": \"caabb9559fbe471dbf0a1f697ef1d569\",\n" +
				"    \"first_bonus_earning\": 0.00,\n" +
				"    \"second_bonus_earning\": 0.00,\n" +
				"    \"grandfather_id\": null,\n" +
				"    \"parent_id\": \"0c928ff035f54b32868c8dd2efb78baa\"\n" +
				"  },\n" +
				"  {\n" +
				"    \"id\": \"1c7824e859264e71a0fda364566f3e44\",\n" +
				"    \"order_id\": \"b1f8783d574c43c092c71af5ec775399\",\n" +
				"    \"product_item_id\": \"0c3a3b449fe14de69494f682df15dd68\",\n" +
				"    \"quantity\": 1,\n" +
				"    \"promotion_quantity\": 0,\n" +
				"    \"item_price\": 77.50,\n" +
				"    \"purchase_price\": 60.0000,\n" +
				"    \"sale_earning\": 17.50,\n" +
				"    \"bonus_earning\": 8.00,\n" +
				"    \"point\": 9.5000,\n" +
				"    \"single_coupon\": null,\n" +
				"    \"shop_id\": \"6d2a92c39610490ba0f234d2672fe7b3\",\n" +
				"    \"order_item_type\": 2,\n" +
				"    \"supplier_id\": \"6f53bbdd5be0413dabf094ea1fb20554\",\n" +
				"    \"brand_id\": \"d5c901131d0141c7863e5a2ec99e8b6c\",\n" +
				"    \"product_id\": \"67b292f611d3497190584d24d682628c\",\n" +
				"    \"first_bonus_earning\": 0.00,\n" +
				"    \"second_bonus_earning\": 0.00,\n" +
				"    \"grandfather_id\": null,\n" +
				"    \"parent_id\": \"818b1d7a78234a37bd50a74e64c08c80\"\n" +
				"  },\n" +
				"  {\n" +
				"    \"id\": \"2137ea414a0a4ece898e792afb690030\",\n" +
				"    \"order_id\": \"43583d86c5a247b3ac5f2977e08904f9\",\n" +
				"    \"product_item_id\": \"061199a7fc57450daa33ff415aab5ee5\",\n" +
				"    \"quantity\": 1,\n" +
				"    \"promotion_quantity\": 0,\n" +
				"    \"item_price\": 21.00,\n" +
				"    \"purchase_price\": 14.0000,\n" +
				"    \"sale_earning\": 8.00,\n" +
				"    \"bonus_earning\": 4.10,\n" +
				"    \"point\": 2.9000,\n" +
				"    \"single_coupon\": null,\n" +
				"    \"shop_id\": \"a67fac8949b341f4ba2c779d6d0b754e\",\n" +
				"    \"order_item_type\": 2,\n" +
				"    \"supplier_id\": \"db73007811c04a849db599c16467813b\",\n" +
				"    \"brand_id\": \"86d8246abeb1435a85a9dd8938bed1ec\",\n" +
				"    \"product_id\": \"773cf50a53f8465d8145b20b0379797c\",\n" +
				"    \"first_bonus_earning\": 0.00,\n" +
				"    \"second_bonus_earning\": 0.00,\n" +
				"    \"grandfather_id\": null,\n" +
				"    \"parent_id\": \"818b1d7a78234a37bd50a74e64c08c80\"\n" +
				"  },\n" +
				"  {\n" +
				"    \"id\": \"31e3ed18ffa446c49866f7eae281736b\",\n" +
				"    \"order_id\": \"5ea52b87863d4c86a19d7811cae77220\",\n" +
				"    \"product_item_id\": \"ca00b1cff868423ab060cd52d8be45f5\",\n" +
				"    \"quantity\": 1,\n" +
				"    \"promotion_quantity\": 0,\n" +
				"    \"item_price\": 43.20,\n" +
				"    \"purchase_price\": 30.0000,\n" +
				"    \"sale_earning\": 16.80,\n" +
				"    \"bonus_earning\": 7.20,\n" +
				"    \"point\": 6.0000,\n" +
				"    \"single_coupon\": null,\n" +
				"    \"shop_id\": \"6d2a92c39610490ba0f234d2672fe7b3\",\n" +
				"    \"order_item_type\": 2,\n" +
				"    \"supplier_id\": \"0a7a37200dc245c69bbb502c1f093658\",\n" +
				"    \"brand_id\": \"e71b8675063f40ab8abb53d3e7639a66\",\n" +
				"    \"product_id\": \"6e14bb9690b941e58f7957e52de1008d\",\n" +
				"    \"first_bonus_earning\": 0.00,\n" +
				"    \"second_bonus_earning\": 0.00,\n" +
				"    \"grandfather_id\": null,\n" +
				"    \"parent_id\": \"818b1d7a78234a37bd50a74e64c08c80\"\n" +
				"  },\n" +
				"  {\n" +
				"    \"id\": \"4dd50591a251411cb9e5a435eb0ac97c\",\n" +
				"    \"order_id\": \"5b6bee50148f4dfb97184da2d37f61b0\",\n" +
				"    \"product_item_id\": \"f76562ddb6c543eea0f90c0c7e627107\",\n" +
				"    \"quantity\": 1,\n" +
				"    \"promotion_quantity\": 0,\n" +
				"    \"item_price\": 51.10,\n" +
				"    \"purchase_price\": 46.8100,\n" +
				"    \"sale_earning\": 6.50,\n" +
				"    \"bonus_earning\": 2.50,\n" +
				"    \"point\": 1.7900,\n" +
				"    \"single_coupon\": null,\n" +
				"    \"shop_id\": \"e69591589927497188c417cb90b8707b\",\n" +
				"    \"order_item_type\": 2,\n" +
				"    \"supplier_id\": \"0b347ad3bbc14364a53f73191e7886c0\",\n" +
				"    \"brand_id\": \"b54a28ce4b9911e5b7109c37f40198e8\",\n" +
				"    \"product_id\": \"55c0bc706af0488cbfc961d63b9f365a\",\n" +
				"    \"first_bonus_earning\": 0.00,\n" +
				"    \"second_bonus_earning\": 0.00,\n" +
				"    \"grandfather_id\": \"362e0762d4cb4d079ec0a5df2a62081a\",\n" +
				"    \"parent_id\": \"1bc85d9a0204430aa3ee21df0cf02e1f\"\n" +
				"  },\n" +
				"  {\n" +
				"    \"id\": \"5e68ac1dd7ad437692508fd64e300d8b\",\n" +
				"    \"order_id\": \"7bdeb1839b8843d8909c94e58372f489\",\n" +
				"    \"product_item_id\": \"152ab5d7fab646ab901689b33e60b389\",\n" +
				"    \"quantity\": 1,\n" +
				"    \"promotion_quantity\": 0,\n" +
				"    \"item_price\": 86.00,\n" +
				"    \"purchase_price\": 75.9400,\n" +
				"    \"sale_earning\": 12.00,\n" +
				"    \"bonus_earning\": 5.00,\n" +
				"    \"point\": 5.0600,\n" +
				"    \"single_coupon\": null,\n" +
				"    \"shop_id\": \"013018c3e1a34a46ab1cffb63df4ad2d\",\n" +
				"    \"order_item_type\": 2,\n" +
				"    \"supplier_id\": \"2570d53b475c459b86ea0d9f9cefa121\",\n" +
				"    \"brand_id\": \"b54a2a664b9911e5b7109c37f40198e8\",\n" +
				"    \"product_id\": \"9e4b30b503174e8ab6a3ecb81296ae95\",\n" +
				"    \"first_bonus_earning\": 0.00,\n" +
				"    \"second_bonus_earning\": 0.00,\n" +
				"    \"grandfather_id\": \"aee4fe510dc64cf98e7000ce7540ee5e\",\n" +
				"    \"parent_id\": \"8a6f71f9740342f88b44d13b363b93ac\"\n" +
				"  },\n" +
				"  {\n" +
				"    \"id\": \"71874cbba3d944039fd0e3eea3649ba7\",\n" +
				"    \"order_id\": \"43583d86c5a247b3ac5f2977e08904f9\",\n" +
				"    \"product_item_id\": \"7c077a8e3cc24ed0961462feec368f4e\",\n" +
				"    \"quantity\": 1,\n" +
				"    \"promotion_quantity\": 0,\n" +
				"    \"item_price\": 12.50,\n" +
				"    \"purchase_price\": 10.7500,\n" +
				"    \"sale_earning\": 2.40,\n" +
				"    \"bonus_earning\": 1.00,\n" +
				"    \"point\": 0.7500,\n" +
				"    \"single_coupon\": null,\n" +
				"    \"shop_id\": \"a67fac8949b341f4ba2c779d6d0b754e\",\n" +
				"    \"order_item_type\": 2,\n" +
				"    \"supplier_id\": \"d3f09820c64047bc8ec2a71479b3d0c1\",\n" +
				"    \"brand_id\": \"280c021867aa4c0fadd16e62bfc5abb1\",\n" +
				"    \"product_id\": \"de562d0922af47b78933b80f1bec9b3e\",\n" +
				"    \"first_bonus_earning\": 0.00,\n" +
				"    \"second_bonus_earning\": 0.00,\n" +
				"    \"grandfather_id\": null,\n" +
				"    \"parent_id\": \"818b1d7a78234a37bd50a74e64c08c80\"\n" +
				"  },\n" +
				"  {\n" +
				"    \"id\": \"825e069632cc4cd4a31d41341074c013\",\n" +
				"    \"order_id\": \"43583d86c5a247b3ac5f2977e08904f9\",\n" +
				"    \"product_item_id\": \"87ef8232e79243a2a64ba967d2394d89\",\n" +
				"    \"quantity\": 1,\n" +
				"    \"promotion_quantity\": 0,\n" +
				"    \"item_price\": 17.65,\n" +
				"    \"purchase_price\": 14.7400,\n" +
				"    \"sale_earning\": 4.25,\n" +
				"    \"bonus_earning\": 1.80,\n" +
				"    \"point\": 1.1100,\n" +
				"    \"single_coupon\": null,\n" +
				"    \"shop_id\": \"a67fac8949b341f4ba2c779d6d0b754e\",\n" +
				"    \"order_item_type\": 2,\n" +
				"    \"supplier_id\": \"d3f09820c64047bc8ec2a71479b3d0c1\",\n" +
				"    \"brand_id\": \"280c021867aa4c0fadd16e62bfc5abb1\",\n" +
				"    \"product_id\": \"6b5e4906ddf64815a100e2a4950b06cb\",\n" +
				"    \"first_bonus_earning\": 0.00,\n" +
				"    \"second_bonus_earning\": 0.00,\n" +
				"    \"grandfather_id\": null,\n" +
				"    \"parent_id\": \"818b1d7a78234a37bd50a74e64c08c80\"\n" +
				"  },\n" +
				"  {\n" +
				"    \"id\": \"97bc9f9ff0774dc1bb138e23c94ef729\",\n" +
				"    \"order_id\": \"241bdcc5454f4dc49625316e23ed28ec\",\n" +
				"    \"product_item_id\": \"066c24856a7e4eb794abf3afc3feb36a\",\n" +
				"    \"quantity\": 1,\n" +
				"    \"promotion_quantity\": 0,\n" +
				"    \"item_price\": 158.00,\n" +
				"    \"purchase_price\": 150.0000,\n" +
				"    \"sale_earning\": 40.00,\n" +
				"    \"bonus_earning\": 8.00,\n" +
				"    \"point\": 0.0000,\n" +
				"    \"single_coupon\": null,\n" +
				"    \"shop_id\": \"a425b8161111413699491dcc153490f7\",\n" +
				"    \"order_item_type\": 2,\n" +
				"    \"supplier_id\": \"c6c4df47cd0041058ef2ad32a132099d\",\n" +
				"    \"brand_id\": \"d2408a0048b1471cb0bdc70ba6817d37\",\n" +
				"    \"product_id\": \"3abf888865184220b008b71a2325574e\",\n" +
				"    \"first_bonus_earning\": 0.00,\n" +
				"    \"second_bonus_earning\": 0.00,\n" +
				"    \"grandfather_id\": null,\n" +
				"    \"parent_id\": \"818b1d7a78234a37bd50a74e64c08c80\"\n" +
				"  },\n" +
				"  {\n" +
				"    \"id\": \"a15641542ac74fe8a3b9ecd59fefac50\",\n" +
				"    \"order_id\": \"ea78a6b177314e8981a9eb0728660e83\",\n" +
				"    \"product_item_id\": \"4c0248e6f42742c482dbe3a63a4db0f8\",\n" +
				"    \"quantity\": 2,\n" +
				"    \"promotion_quantity\": 0,\n" +
				"    \"item_price\": 31.90,\n" +
				"    \"purchase_price\": 28.0000,\n" +
				"    \"sale_earning\": 5.00,\n" +
				"    \"bonus_earning\": 2.00,\n" +
				"    \"point\": 1.9000,\n" +
				"    \"single_coupon\": null,\n" +
				"    \"shop_id\": \"298a46cf5f784be39e84185d90d8c4d0\",\n" +
				"    \"order_item_type\": 2,\n" +
				"    \"supplier_id\": \"db73007811c04a849db599c16467813b\",\n" +
				"    \"brand_id\": \"e9be4c85c3e94a42ae1457471597efa2\",\n" +
				"    \"product_id\": \"4f56827130ce43b5b5ca8b82618ae6a3\",\n" +
				"    \"first_bonus_earning\": 0.00,\n" +
				"    \"second_bonus_earning\": 0.00,\n" +
				"    \"grandfather_id\": null,\n" +
				"    \"parent_id\": \"0c928ff035f54b32868c8dd2efb78baa\"\n" +
				"  },\n" +
				"  {\n" +
				"    \"id\": \"afd2052be31e4942b81603b78fdcf528\",\n" +
				"    \"order_id\": \"62fe83be5c334cb1b7288ed285fcb130\",\n" +
				"    \"product_item_id\": \"061199a7fc57450daa33ff415aab5ee5\",\n" +
				"    \"quantity\": 3,\n" +
				"    \"promotion_quantity\": 0,\n" +
				"    \"item_price\": 21.00,\n" +
				"    \"purchase_price\": 14.0000,\n" +
				"    \"sale_earning\": 8.00,\n" +
				"    \"bonus_earning\": 4.10,\n" +
				"    \"point\": 2.9000,\n" +
				"    \"single_coupon\": null,\n" +
				"    \"shop_id\": \"90cbb93d7457491b91c235825c446082\",\n" +
				"    \"order_item_type\": 2,\n" +
				"    \"supplier_id\": \"db73007811c04a849db599c16467813b\",\n" +
				"    \"brand_id\": \"86d8246abeb1435a85a9dd8938bed1ec\",\n" +
				"    \"product_id\": \"773cf50a53f8465d8145b20b0379797c\",\n" +
				"    \"first_bonus_earning\": 0.00,\n" +
				"    \"second_bonus_earning\": 0.00,\n" +
				"    \"grandfather_id\": null,\n" +
				"    \"parent_id\": \"818b1d7a78234a37bd50a74e64c08c80\"\n" +
				"  },\n" +
				"  {\n" +
				"    \"id\": \"bf26e457b008476c99c96f713ece6b30\",\n" +
				"    \"order_id\": \"00a0ed62607e4f9eaae0fc3dc9b8e88b\",\n" +
				"    \"product_item_id\": \"01524f2e97684f78a0cef31168525268\",\n" +
				"    \"quantity\": 1,\n" +
				"    \"promotion_quantity\": 0,\n" +
				"    \"item_price\": 26.90,\n" +
				"    \"purchase_price\": 23.2000,\n" +
				"    \"sale_earning\": 4.70,\n" +
				"    \"bonus_earning\": 2.10,\n" +
				"    \"point\": 1.6000,\n" +
				"    \"single_coupon\": null,\n" +
				"    \"shop_id\": \"e69591589927497188c417cb90b8707b\",\n" +
				"    \"order_item_type\": 2,\n" +
				"    \"supplier_id\": \"95d5e2d18ba24051b2f926d9b1054a8e\",\n" +
				"    \"brand_id\": \"f7909cf9ca1246d999204ee96a4e6ac4\",\n" +
				"    \"product_id\": \"8392e6666d494a9a9ed721a3b5d4efc0\",\n" +
				"    \"first_bonus_earning\": 0.00,\n" +
				"    \"second_bonus_earning\": 0.00,\n" +
				"    \"grandfather_id\": \"362e0762d4cb4d079ec0a5df2a62081a\",\n" +
				"    \"parent_id\": \"1bc85d9a0204430aa3ee21df0cf02e1f\"\n" +
				"  },\n" +
				"  {\n" +
				"    \"id\": \"c8ea43b78ae84812bb99dd8d4c5a4370\",\n" +
				"    \"order_id\": \"4a4ea5944b704d8c903994a58d7a4e4c\",\n" +
				"    \"product_item_id\": \"d6eab4e6cbe74329a94dc1771826f34b\",\n" +
				"    \"quantity\": 1,\n" +
				"    \"promotion_quantity\": 0,\n" +
				"    \"item_price\": 144.00,\n" +
				"    \"purchase_price\": 60.0000,\n" +
				"    \"sale_earning\": 36.00,\n" +
				"    \"bonus_earning\": 24.00,\n" +
				"    \"point\": 60.0000,\n" +
				"    \"single_coupon\": null,\n" +
				"    \"shop_id\": \"013018c3e1a34a46ab1cffb63df4ad2d\",\n" +
				"    \"order_item_type\": 2,\n" +
				"    \"supplier_id\": \"c6c4df47cd0041058ef2ad32a132099d\",\n" +
				"    \"brand_id\": \"fd1b942c397a47ac90febd449ac2d838\",\n" +
				"    \"product_id\": \"9d8a3f3d14ec4fbaa091d4356b00b24d\",\n" +
				"    \"first_bonus_earning\": 0.00,\n" +
				"    \"second_bonus_earning\": 0.00,\n" +
				"    \"grandfather_id\": \"aee4fe510dc64cf98e7000ce7540ee5e\",\n" +
				"    \"parent_id\": \"8a6f71f9740342f88b44d13b363b93ac\"\n" +
				"  },\n" +
				"  {\n" +
				"    \"id\": \"d237d45fe07f44339dd5775682a332e4\",\n" +
				"    \"order_id\": \"11d240a1e6824d6ab640e1268544126a\",\n" +
				"    \"product_item_id\": \"0c3a3b449fe14de69494f682df15dd68\",\n" +
				"    \"quantity\": 1,\n" +
				"    \"promotion_quantity\": 0,\n" +
				"    \"item_price\": 77.50,\n" +
				"    \"purchase_price\": 60.0000,\n" +
				"    \"sale_earning\": 17.50,\n" +
				"    \"bonus_earning\": 8.00,\n" +
				"    \"point\": 9.5000,\n" +
				"    \"single_coupon\": null,\n" +
				"    \"shop_id\": \"6d2a92c39610490ba0f234d2672fe7b3\",\n" +
				"    \"order_item_type\": 2,\n" +
				"    \"supplier_id\": \"6f53bbdd5be0413dabf094ea1fb20554\",\n" +
				"    \"brand_id\": \"d5c901131d0141c7863e5a2ec99e8b6c\",\n" +
				"    \"product_id\": \"67b292f611d3497190584d24d682628c\",\n" +
				"    \"first_bonus_earning\": 0.00,\n" +
				"    \"second_bonus_earning\": 0.00,\n" +
				"    \"grandfather_id\": null,\n" +
				"    \"parent_id\": \"818b1d7a78234a37bd50a74e64c08c80\"\n" +
				"  },\n" +
				"  {\n" +
				"    \"id\": \"da37b5a62f3a4b0fa0aa8b32202b65dc\",\n" +
				"    \"order_id\": \"8697ece59ef2435ebe4a1a5c4ee6f1c8\",\n" +
				"    \"product_item_id\": \"ce591e7f58be4e27b531d612a792e27e\",\n" +
				"    \"quantity\": 2,\n" +
				"    \"promotion_quantity\": 0,\n" +
				"    \"item_price\": 90.30,\n" +
				"    \"purchase_price\": 78.3600,\n" +
				"    \"sale_earning\": 38.70,\n" +
				"    \"bonus_earning\": 10.00,\n" +
				"    \"point\": 1.9400,\n" +
				"    \"single_coupon\": null,\n" +
				"    \"shop_id\": \"75c39874ef9b4d21a889ea76653a2709\",\n" +
				"    \"order_item_type\": 2,\n" +
				"    \"supplier_id\": \"07cbcc14e0c64b43942a1a4401d12e81\",\n" +
				"    \"brand_id\": \"b54a25274b9911e5b7109c37f40198e8\",\n" +
				"    \"product_id\": \"bb3450c48e944eccbacf04cb91ccde35\",\n" +
				"    \"first_bonus_earning\": 0.00,\n" +
				"    \"second_bonus_earning\": 0.00,\n" +
				"    \"grandfather_id\": null,\n" +
				"    \"parent_id\": \"818b1d7a78234a37bd50a74e64c08c80\"\n" +
				"  },\n" +
				"  {\n" +
				"    \"id\": \"e38e53e7d43c47fe8e5fdcce16fac8d7\",\n" +
				"    \"order_id\": \"9cd670584a044b248145e28e5e63a7a0\",\n" +
				"    \"product_item_id\": \"4c0248e6f42742c482dbe3a63a4db0f8\",\n" +
				"    \"quantity\": 3,\n" +
				"    \"promotion_quantity\": 0,\n" +
				"    \"item_price\": 31.90,\n" +
				"    \"purchase_price\": 28.0000,\n" +
				"    \"sale_earning\": 5.00,\n" +
				"    \"bonus_earning\": 2.00,\n" +
				"    \"point\": 1.9000,\n" +
				"    \"single_coupon\": null,\n" +
				"    \"shop_id\": \"298a46cf5f784be39e84185d90d8c4d0\",\n" +
				"    \"order_item_type\": 2,\n" +
				"    \"supplier_id\": \"db73007811c04a849db599c16467813b\",\n" +
				"    \"brand_id\": \"e9be4c85c3e94a42ae1457471597efa2\",\n" +
				"    \"product_id\": \"4f56827130ce43b5b5ca8b82618ae6a3\",\n" +
				"    \"first_bonus_earning\": 0.00,\n" +
				"    \"second_bonus_earning\": 0.00,\n" +
				"    \"grandfather_id\": null,\n" +
				"    \"parent_id\": \"0c928ff035f54b32868c8dd2efb78baa\"\n" +
				"  },\n" +
				"  {\n" +
				"    \"id\": \"e9e3c6b24e4940fb892f730b1f8d7fe2\",\n" +
				"    \"order_id\": \"400ea92da23046f29eec287e647eb0c2\",\n" +
				"    \"product_item_id\": \"066c24856a7e4eb794abf3afc3feb36a\",\n" +
				"    \"quantity\": 1,\n" +
				"    \"promotion_quantity\": 0,\n" +
				"    \"item_price\": 158.00,\n" +
				"    \"purchase_price\": 150.0000,\n" +
				"    \"sale_earning\": 40.00,\n" +
				"    \"bonus_earning\": 8.00,\n" +
				"    \"point\": 0.0000,\n" +
				"    \"single_coupon\": null,\n" +
				"    \"shop_id\": \"af0312c4620f41c992fdf825a24c5d18\",\n" +
				"    \"order_item_type\": 2,\n" +
				"    \"supplier_id\": \"c6c4df47cd0041058ef2ad32a132099d\",\n" +
				"    \"brand_id\": \"d2408a0048b1471cb0bdc70ba6817d37\",\n" +
				"    \"product_id\": \"3abf888865184220b008b71a2325574e\",\n" +
				"    \"first_bonus_earning\": 0.00,\n" +
				"    \"second_bonus_earning\": 0.00,\n" +
				"    \"grandfather_id\": null,\n" +
				"    \"parent_id\": \"818b1d7a78234a37bd50a74e64c08c80\"\n" +
				"  },\n" +
				"  {\n" +
				"    \"id\": \"ee304299c6bf4e4e9dd823eb8d29233d\",\n" +
				"    \"order_id\": \"f4720443e874436cac30f751c6161ff0\",\n" +
				"    \"product_item_id\": \"5bcf2991c07f4f7da5a50516260b11d2\",\n" +
				"    \"quantity\": 1,\n" +
				"    \"promotion_quantity\": 0,\n" +
				"    \"item_price\": 173.00,\n" +
				"    \"purchase_price\": 152.3800,\n" +
				"    \"sale_earning\": 23.00,\n" +
				"    \"bonus_earning\": 10.00,\n" +
				"    \"point\": 10.6200,\n" +
				"    \"single_coupon\": null,\n" +
				"    \"shop_id\": \"6d2a92c39610490ba0f234d2672fe7b3\",\n" +
				"    \"order_item_type\": 2,\n" +
				"    \"supplier_id\": \"2570d53b475c459b86ea0d9f9cefa121\",\n" +
				"    \"brand_id\": \"b54a2a664b9911e5b7109c37f40198e8\",\n" +
				"    \"product_id\": \"9e4b30b503174e8ab6a3ecb81296ae95\",\n" +
				"    \"first_bonus_earning\": 0.00,\n" +
				"    \"second_bonus_earning\": 0.00,\n" +
				"    \"grandfather_id\": null,\n" +
				"    \"parent_id\": \"818b1d7a78234a37bd50a74e64c08c80\"\n" +
				"  },\n" +
				"  {\n" +
				"    \"id\": \"f65064a6e0ad4fc5875cedafb83eabda\",\n" +
				"    \"order_id\": \"8e5522c5f44c4dcfa97fc763e4a030cb\",\n" +
				"    \"product_item_id\": \"152ab5d7fab646ab901689b33e60b389\",\n" +
				"    \"quantity\": 1,\n" +
				"    \"promotion_quantity\": 0,\n" +
				"    \"item_price\": 86.00,\n" +
				"    \"purchase_price\": 75.9400,\n" +
				"    \"sale_earning\": 12.00,\n" +
				"    \"bonus_earning\": 5.00,\n" +
				"    \"point\": 5.0600,\n" +
				"    \"single_coupon\": null,\n" +
				"    \"shop_id\": \"013018c3e1a34a46ab1cffb63df4ad2d\",\n" +
				"    \"order_item_type\": 2,\n" +
				"    \"supplier_id\": \"2570d53b475c459b86ea0d9f9cefa121\",\n" +
				"    \"brand_id\": \"b54a2a664b9911e5b7109c37f40198e8\",\n" +
				"    \"product_id\": \"9e4b30b503174e8ab6a3ecb81296ae95\",\n" +
				"    \"first_bonus_earning\": 0.00,\n" +
				"    \"second_bonus_earning\": 0.00,\n" +
				"    \"grandfather_id\": \"aee4fe510dc64cf98e7000ce7540ee5e\",\n" +
				"    \"parent_id\": \"8a6f71f9740342f88b44d13b363b93ac\"\n" +
				"  },\n" +
				"  {\n" +
				"    \"id\": \"fbc906da4fd5420d9e74252d711c91ba\",\n" +
				"    \"order_id\": \"19ba61bf7e5c460baaec71982b6266d4\",\n" +
				"    \"product_item_id\": \"d6eab4e6cbe74329a94dc1771826f34b\",\n" +
				"    \"quantity\": 1,\n" +
				"    \"promotion_quantity\": 0,\n" +
				"    \"item_price\": 144.00,\n" +
				"    \"purchase_price\": 60.0000,\n" +
				"    \"sale_earning\": 36.00,\n" +
				"    \"bonus_earning\": 24.00,\n" +
				"    \"point\": 60.0000,\n" +
				"    \"single_coupon\": null,\n" +
				"    \"shop_id\": \"013018c3e1a34a46ab1cffb63df4ad2d\",\n" +
				"    \"order_item_type\": 2,\n" +
				"    \"supplier_id\": \"c6c4df47cd0041058ef2ad32a132099d\",\n" +
				"    \"brand_id\": \"fd1b942c397a47ac90febd449ac2d838\",\n" +
				"    \"product_id\": \"9d8a3f3d14ec4fbaa091d4356b00b24d\",\n" +
				"    \"first_bonus_earning\": 0.00,\n" +
				"    \"second_bonus_earning\": 0.00,\n" +
				"    \"grandfather_id\": \"aee4fe510dc64cf98e7000ce7540ee5e\",\n" +
				"    \"parent_id\": \"8a6f71f9740342f88b44d13b363b93ac\"\n" +
				"  }\n" +
				"]";
		JSONArray jsonArray = JSONArray.parseArray(str);

		for(int i = 0; i < jsonArray.size(); i++){
			JSONObject jsonObject =  jsonArray.getJSONObject(i);
			OrderItemDto orderItemDto = JSON.toJavaObject(jsonObject,OrderItemDto.class);
			System.out.println(orderItemDto.getId());
			//orderItemDao.updateBonusEarning(orderItemDto.getId(),orderItemDto.ge);
		}
	}

	@Test
	public void test9(){
		Map<String, Object> map = new HashMap<>();
		map.put("orderId", "0f88744523554a2785a0f6c76728dede");
		map.put("orderType", "3");
		map.put("buyerId", "7eca1c41ab124583a794b3d4825525b9");
		Pager page = new Pager();
		page.setPageNo(1);
		page.setPageSize(1);
		page.doPage();
		map.put("page", page);
		List<OrderDto> list = orderDao.getBuyerOrderList(map);
		if (null != list && list.size() == 1) {
			System.out.println(list.get(0).getId());
		}else
			System.out.println("aaaaa");
	}

	@Test
	public void test10(){
		ShopFund shopFund = new ShopFund();
		shopFund.setId("666887d7c0c944a0836c4713dfb160b4");
		shopFund = shopFundDao.get(shopFund);

		BigDecimal b = shopFund.getTotalSale();
		System.out.println(b);
		//BigDecimal nowSales          = shopDao.getTotalSale("a87cc34f258f4d8696236a68444cecff");
		//System.out.println(nowSales);
	}

	@Test
	public void test11(){
		orderDao.getOrderItemsInfo("f54adb0366264026ac79116248506293");
	}
}
