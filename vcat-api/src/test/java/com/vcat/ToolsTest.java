package com.vcat;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.util.Asserts;
import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ylin on 2016/6/6.
 */
public class ToolsTest {

    @Test
    public void isGroupProductTest(){
        Assert.assertFalse(isGroupProduct("http://test.v-cat.cn:9090/buyer/views/goods.html?productId=c006972e7a33492d87d5a5f329e8d759&shopId=32ef567c83404827a6169657abf96630"));
        Assert.assertTrue(isGroupProduct("https://test.v-cat.cn:9090/buyer/views/goods.html?type=9&shopId=7eca1c41ab124583a794b3d4825525b9&groupBuyId=e066434245b74dc5993095d0c54073e7"));
    }

    private boolean isGroupProduct(String longUrl){
        String[] arr = longUrl.split("\\?");
        List<NameValuePair> pairs = URLEncodedUtils.parse(arr[1], Charset.forName("UTF-8"));
        List<NameValuePair> removes = new ArrayList<>(3);
        for (NameValuePair pair : pairs){
            if(pair.getName().equalsIgnoreCase("productId")
                    || pair.getName().equalsIgnoreCase("shopId")
                    || pair.getName().equalsIgnoreCase("shareType")) {
                removes.add(pair);
            }
        }
        pairs.removeAll(removes);
        return !pairs.isEmpty();
    }
}
