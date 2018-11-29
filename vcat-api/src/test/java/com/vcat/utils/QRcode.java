package com.vcat.utils;

import com.vcat.common.utils.ZxingHandler;
import org.junit.Test;

/**
 * Created by Code.Ai on 16/4/25.
 * Description:
 */
public class QRcode {
    @Test
    public void test1(){
        String url =  ZxingHandler.decode2("/Users/codeai/Downloads/qrcode.JPG");
        System.out.println(url);
    }
}
