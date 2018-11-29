package com.vcat.common;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Test;

import com.vcat.common.cloud.QCloudClient;
import com.vcat.common.cloud.QCloudUtils;

public class QCloudClientTest {
	@Test
	public void uploadTest() throws IOException, URISyntaxException {
		System.out.println(QCloudClient.uploadImage(new File(
				"C:\\Users\\dean\\Desktop\\20160120115742.png")));
		//8763386e-e59c-4916-94f5-36fce21399fe
	}
	
	@Test
	public void delTest() {
		//f48ae5d4-4038-4024-9ef9-5b40988af86e
		System.out.println(QCloudClient.delImage("11d0f6ef-4c9f-483f-b77a-57f20bff7bca"));
	}
	
	@Test
	public void createOriginalDownloadUrlTest(){
		System.out.println(QCloudUtils.createOriginalDownloadUrl("821d8017-7e1b-450c-b780-991f86544cf9"));
	}

	@Test
	public void createThumbDownloadUrl(){
		System.out.println(QCloudUtils.createThumbDownloadUrl("821d8017-7e1b-450c-b780-991f86544cf9","200"));
		System.out.println(QCloudUtils.createThumbDownloadUrl("821d8017-7e1b-450c-b780-991f86544cf9",110));
	}
}
