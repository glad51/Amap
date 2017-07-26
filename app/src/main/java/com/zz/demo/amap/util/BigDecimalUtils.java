package com.zz.demo.amap.util;

import android.text.TextUtils;
import android.util.Log;

import java.math.BigDecimal;

/**
 * 由于Java的简单类型不能够精确的对浮点数进行运算，这个工具类提供精
 * 确的浮点数运算，包括加减乘除和四舍五入。
 */
public class BigDecimalUtils {

    //默认除法运算精度
    private static final int DEF_DIV_SCALE = 4;

    private static String TAG="tag";

    //这个类不能实例化
    private BigDecimalUtils() {
    }

    /**
     * 提供精确的加法运算。
     *
     * @param v1 被加数
     * @param v2 加数
     * @return 两个参数的和
     */
    public static double add(String v1, String v2) {
        BigDecimal b1 = null;
        BigDecimal b2 = null;
        if (TextUtils.isEmpty(v1)) {
            v1 = "0";
        }
        if (TextUtils.isEmpty(v2)) {
            v2 = "0";
        }
        b1 = new BigDecimal(v1);
        b2 = new BigDecimal(v2);
        return b1.add(b2).doubleValue();
    }

    /**
     * 提供精确的减法运算。
     *
     * @param v1 被减数
     * @param v2 减数
     * @return 两个参数的差
     */
    public static double sub(String v1, String v2) {
        BigDecimal b1 = null;
        BigDecimal b2 = null;
        try {
            b1 = new BigDecimal(v1);
            b2 = new BigDecimal(v2);
        } catch (Exception e) {
            Log.i(TAG,"输入有误，请重新输入");
            e.printStackTrace();
        }
        return b1.subtract(b2).doubleValue();
    }

    /**
     * 提供精确的乘法运算。
     *
     * @param v1 被乘数
     * @param v2 乘数
     * @return 两个参数的积
     */
    public static double mul(String v1, String v2) {
        BigDecimal b1 = null;
        BigDecimal b2 = null;
        try {
            b1 = new BigDecimal(v1);
            b2 = new BigDecimal(v2);
        } catch (Exception e) {
            Log.i(TAG,"输入有误，请重新输入");
            e.printStackTrace();
        }
        return b1.multiply(b2).doubleValue();
    }

    /**
     * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到
     * 小数点以后10位，以后的数字四舍五入。
     *
     * @param v1 被除数
     * @param v2 除数
     * @return 两个参数的商
     */
    public static double div(String v1, String v2) {
        return div(v1, v2, DEF_DIV_SCALE);
    }

    /**
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指
     * 定精度，以后的数字四舍五入。
     *
     * @param v1    被除数
     * @param v2    除数
     * @param scale 表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    public static double div(String v1, String v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        if (compareTo(v2, "0") == 0) {
            return 0.0;
        }
        BigDecimal b1 = null;
        BigDecimal b2 = null;
        try {
            b1 = new BigDecimal(v1);
            b2 = new BigDecimal(v2);
        } catch (Exception e) {
            Log.i(TAG,"输入有误，请重新输入");
            e.printStackTrace();
        }
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 精确对比两个数字
     *
     * @param v1 需要被对比的第一个数
     * @param v2 需要被对比的第二个数
     * @return 如果两个数一样则返回0，如果第一个数比第二个数大则返回1，反之返回-1
     */
    public static int compareTo(String v1, String v2) {
        BigDecimal b1 = null;
        BigDecimal b2 = null;
        try {
            b1 = new BigDecimal(v1);
            b2 = new BigDecimal(v2);
            return b1.compareTo(b2);
        } catch (Exception e) {
            Log.i(TAG,"输入有误，请重新输入");
            e.printStackTrace();
        }
        return b1.compareTo(b2);
    }

}   