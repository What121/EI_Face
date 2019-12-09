package com.bestom.ei_library.commons.utils;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 通用工具类
 */
public class MyUtil {

    static public boolean IDCardEL(String id){
        String EL="^[1-9]\\d{5}(18|19|2([0-9]))\\d{2}(0[0-9]|10|11|12)([0-2][1-9]|30|31)\\d{3}[0-9Xx]$";
        Pattern pattern=Pattern.compile(EL);
        Matcher m = pattern.matcher(id);
        boolean b = m.matches();
        return b;
    }

    /***************************************************************
     *  和校验
     * @param sHex 16进制字符串
     * @return 校验和
     */
    static public String sumCheck(String sHex) {
        if (sHex == null || sHex.equals("")) {
            return "";
        }
        int total = 0;
        int len = sHex.length();
        int num = 0;
        while (num < len) {
            String s = sHex.substring(num, num + 2);
            total += Integer.parseInt(s, 16);
            num = num + 2;
        }
        // 用256求余最大是255，即16进制的FF
        int mod = total % 256;
        String hex = Integer.toHexString(mod);
        len = hex.length();
        if (len < 2) {
            hex = "0" + hex;
        }
        return hex.toUpperCase();
    }

    /***************************************************************
     *  字符串反转
     * @param str 字符串
     * @return 返回
     */
    static public String reverse(String str) {
        return new StringBuilder(str).reverse().toString();
    }

    /***************************************************************
     *  十六进制字符串从低位至高位
     * @param hex 16进制字符串
     * @return 返回
     */
    static public String lowHexOrder(String hex) {
        int len = hex.length();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < len; i = i + 2) {
            builder.insert(0, hex.substring(i, i + 2));
        }
        return builder.toString();
    }

    /**
     * 把原始字符串分割成指定长度的字符串列表
     *
     * @param inputString 原始字符串
     * @param length      指定长度
     * @return 返回
     */
    static public List<String> getStrList(String inputString, int length) {
        int size = inputString.length() / length;
        if (inputString.length() % length != 0) {
            size += 1;
        }
        return getStrList(inputString, length, size);
    }

    /**
     * 把原始字符串分割成指定长度的字符串列表
     *
     * @param inputString 原始字符串
     * @param length      指定长度
     * @param size        指定列表大小
     * @return 返回
     */
    static public List<String> getStrList(String inputString, int length, int size) {
        List<String> list = new ArrayList<>();
        for (int index = 0; index < size; index++) {
            String childStr = substring(inputString, index * length, (index + 1) * length);
            list.add(childStr);
        }
        return list;
    }

    /**
     * 分割字符串，如果开始位置大于字符串长度，返回空
     *
     * @param str 原始字符串
     * @param f   开始位置
     * @param t   结束位置
     * @return 返回
     */
    static public String substring(String str, int f, int t) {
        if (f > str.length())
            return null;
        if (t > str.length()) {
            return str.substring(f, str.length());
        } else {
            return str.substring(f, t);
        }
    }

    /**
     * 截取相应长度的数组
     *
     * @param src   源数组
     * @param begin 起始位置
     * @param size  长度
     */
    static public byte[] subBytes(byte[] src, int begin, int size) {
        byte[] bs = new byte[size];
        System.arraycopy(src, begin, bs, 0, size);
        return bs;
    }

    /**
     * 补充16进制字符串长度
     *
     * @param hex     原始长度
     * @param maxSize 最大长度
     * @return 返回补充后16进制字符串
     */
    static public String supHexSize(String hex, int maxSize) {
        int hexSize = hex.length();
        if (hexSize == 0 || hexSize > maxSize) {
            return null;
        }
        // 先前补充零
        StringBuilder builder = new StringBuilder(hex);
        int size = maxSize - hexSize;
        for (int i = 0; i < size; i++) {
            builder.insert(0, "0");
        }
        return builder.toString();
    }

    /**
     * 判断是否是16进制字符串
     *
     * @param str 16进制字符串
     * @return 结果
     */
    static public boolean isHexNumber(String str) {
        String validate = "(?i)[0-9a-f]+";
        return str.matches(validate);
    }

    /**
     * 判断是否是符合格式的时间
     * 一个字符串，字符串固定格式”2018:10:10:16:37:12”，年：月：日：时：
     * 分：秒，其中“：”为分隔符，不可省略
     *
     * @param str 格式时间的字符串
     * @return 结果
     */
    static public boolean isFormatTime(String str) {
        String validate = "^\\d{4}:\\d{2}:\\d{2}:\\d{2}:\\d{2}:\\d{2}$";
        return str.matches(validate);
    }

    /**
     * 获取byte数组真实使用长度
     *
     * @param bytes bytes
     * @return 长度
     */
    static public int getRealBytesLength(byte[] bytes) {
        int i = 0;
        for (; i < bytes.length; i++) {
            if (bytes[i] == '\0') {
                break;
            }
        }
        return i;
    }

    /**
     * 获取真实使用byte数组
     *
     * @param bytes bytes
     * @return 长度
     */
    static public byte[] getRealBytes(byte[] bytes) {
        int length = getRealBytesLength(bytes);
        return subBytes(bytes, 0, length);
    }

    public static boolean ping(String ipAddress){
        int  timeOut =  3000 ;  //超时应该在3钞以上
        boolean status = false;     // 当返回值是true时，说明host是可用的，false则不可。
        try {
            status = InetAddress.getByName(ipAddress).isReachable(timeOut);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return status;
    }
}
