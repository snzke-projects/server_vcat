package com.tencent.common;

import java.util.Random;

/**
 * User: rizenguo
 * Date: 2014/10/29
 * Time: 14:18
 */
public class RandomStringGenerator {

    /**
     * 获取一定长度的随机字符串
     * @param length 指定字符串长度
     * @return 一定长度的字符串
     */
    public static String getRandomCharByLength(int length) {
        return getRandomStringByRange("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789",length);
    }

    /**
     * 获取一定长度的随机字符串
     * @param length 指定字符串长度
     * @return 一定长度的字符串
     */
    public static String getRandomStringByLength(int length) {
        return getRandomStringByRange("abcdefghijklmnopqrstuvwxyz0123456789",length);
    }

    /**
     * 获取一定长度的随机数字
     * @param length 指定数字长度
     * @return 一定长度的数字
     */
    public static String getRandomNumberByLength(int length) {
        return getRandomStringByRange("0123456789",length);
    }

    private static String getRandomStringByRange(String range,int length){
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(range.length());
            sb.append(range.charAt(number));
        }
        return sb.toString();
    }
}
