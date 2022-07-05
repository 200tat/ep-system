package com.primeton.manageProvider.util;

/**
 * 此类为处理字符串为null或是空字符串
 */
public class StringDealUtil {
    /**
     * 若形参为null或是空字符串则赋值为null
     * @param s
     * @return
     */
    public static String verify(String s){
        if (s == null || s.isEmpty())
            return null;
        return s;
    }
}
