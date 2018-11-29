package com.vcat.api.web;

import com.vcat.api.service.GroupBuyService;
import com.vcat.api.service.ShortUrlService;
import com.vcat.api.web.validation.ValidateParams;
import com.vcat.common.constant.ApiConstants;
import com.vcat.common.constant.ApiMsgConstants;
import com.vcat.common.utils.IdGen;
import com.vcat.common.utils.StringUtils;
import com.vcat.common.version.ApiVersion;
import com.vcat.common.web.rest.RestBaseController;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ylin on 2016/2/23.
 */
@RestController
public class ShortUrlController extends RestBaseController {
    @Autowired
    private ShortUrlService shortUrlService;
    @Autowired
    private GroupBuyService groupBuyService;

    @ApiVersion({1,2})
    @RequiresRoles("seller")
    @RequestMapping("/api/short")
    @ValidateParams
    public Object cut(@RequestHeader(value = "token", defaultValue = "") String token,
                      @NotEmpty
                      @RequestParam(value = "longUrl", defaultValue = "") String longUrl, HttpServletRequest request, HttpServletResponse response) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        longUrl = URLDecoder.decode(longUrl, "UTF8");
        String shortUrl = shortUrlService.getShortUrl(longUrl);
        if(shortUrl == null){
            shortUrl =ShortUrl.generateCode(longUrl)[2];
            shortUrlService.insert(longUrl, shortUrl);
        }
        String uri = request.getScheme() + "://" +   // "http" + "://
                request.getServerName() +       // "myhost"
                ":" + request.getServerPort(); // ":" + "8080"
        Map<String, Object> map = new HashMap<>();
        map.put("code",ApiMsgConstants.SUCCESS_CODE);
        map.put("msg", ApiMsgConstants.SUCCESS_MSG);
        map.put("url", ApiConstants.VCAT_DOMAIN+"/vcat-api/s/"+shortUrl);
        return map;
    }

    @RequestMapping("/s/{shortUrl}")
    @ValidateParams
    public void cut(@NotEmpty
                      @PathVariable(value = "shortUrl") String shortUrl, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String longUrl = shortUrlService.getLongUrl(shortUrl);
        if(longUrl == null || StringUtils.isNullBlank(longUrl)){
            logger.warn("未知url...");
            return;
        }
        logger.debug("URLDecoder bef:"+longUrl);
        //longUrl = URLDecoder.decode(longUrl, "UTF8");
        logger.debug("URLDecoder aft:"+longUrl);
        String[] split = longUrl.split("\\?");
        List<NameValuePair> pairs = URLEncodedUtils.parse(split[1], Charset.forName("UTF-8"));
        List<NameValuePair> removes = new ArrayList<>(3);
        String productId = "";
        String shopId = "";
        for (NameValuePair pair : pairs){
            if(pair.getName().equalsIgnoreCase("productId")) {
                productId = pair.getValue();
                removes.add(pair);
            }else if(pair.getName().equalsIgnoreCase("shopId")){
                shopId= pair.getValue();
                removes.add(pair);
            } else if(pair.getName().equalsIgnoreCase("shareType")){
                removes.add(pair);
            }
        }
        pairs.removeAll(removes);
        boolean isProductShare =pairs.isEmpty();
        String gbId = "";
        if(isProductShare && !StringUtils.isNullBlank(productId)
                && !StringUtils.isNullBlank(gbId = groupBuyService.getGroupBuyByProductId(productId))){
            logger.debug("转换为团购商品分享：原始url"+longUrl);
            //是团购分享，拼接url
            longUrl = ApiConstants.VCAT_DOMAIN + "/buyer/views/goods.html?type=9&groupbuy=1&shopId="+shopId+"&groupBuyId="+gbId;
            logger.debug("新url"+longUrl);
        }
        logger.debug("redircet:"+longUrl);
        response.sendRedirect(longUrl);
    }
}

class ShortUrl {
    public static String[] generateCode(String url) throws NoSuchAlgorithmException {
        String key = "VcatShort"; // 网址的混合KEY
        String[] chars = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
                "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v",
                "w", "x", "y", "z", "0", "1", "2", "3", "4", "5", "6", "7",
                "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
                "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
                "W", "X", "Y", "Z"};
        String hex = toMd5(key + url);// 对传入网址和混合KEY进行MD5加密
        String[] resUrl = new String[4];

        for (int i = 0; i < 4; i++) {
            // 把加密字符按照8位一组16进制与0x3FFFFFFF进行位与运算
            String sTempSubString = hex.substring(i * 8, i * 8 + 8);
            long lHexLong = 0x3FFFFFFF & Long.parseLong(sTempSubString, 16);
            String outChars = "";
            for (int j = 0; j < 6; j++) {
                // 把得到的值与 0x0000003D 进行位与运算，取得字符数组 chars 索引
                long index = 0x0000003D & lHexLong;
                // 把取得的字符相加
                outChars += chars[(int) index];
                // 每次循环按位右移 5 位
                lHexLong = lHexLong >> 5;
            }
            resUrl[i] = outChars;
        }

        return resUrl;
    }

    private static String toMd5(String arg) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(arg.getBytes());
        byte b[] = md.digest();
        int i;
        StringBuffer buf = new StringBuffer("");
        for (int offset = 0; offset < b.length; offset++) {
            i = b[offset];
            if (i < 0)
                i += 256;
            if (i < 16)
                buf.append("0");
            buf.append(Integer.toHexString(i));
        }
        return buf.toString();
    }
}
