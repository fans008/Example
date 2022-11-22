package com.common.ducis.commonutil

import java.math.BigDecimal

/**
 *@describe：高精度数学计算方法
 *@author：ftt
 *@date：2020/5/19
 */
object BigDecimalUtils {

    // 加法运算
    @JvmStatic
    fun add(d1: Double, d2: Double, pointNumber: Int): Double = BigDecimal(d1).add(BigDecimal(d2)).setScale(
        pointNumber,
        BigDecimal.ROUND_DOWN
    ).toDouble()

    // 减法运算
    @JvmStatic
    fun sub(d1: Double, d2: Double, pointNumber: Int): Double = BigDecimal(d1).subtract(BigDecimal(d2)).setScale(
        pointNumber,
        BigDecimal.ROUND_DOWN
    ).toDouble()

    // 乘法运算
    @JvmStatic
    fun mul(d1: Double, d2: Double, decimalPoint: Int): Double =
        BigDecimal(d1).multiply(BigDecimal(d2)).setScale(
            decimalPoint,
            BigDecimal.ROUND_DOWN
        ).toDouble()

    // 除法运算
    @JvmStatic
    fun div(d1: Double, d2: Double, pointNumber: Int): Double = BigDecimal(d1).divide(BigDecimal(d2)).setScale(
        pointNumber,
        BigDecimal.ROUND_DOWN
    ).toDouble()

    //格式化字符串
    fun getBigDecimalToString(data:String,scale:Int = 4):String{
        return data.toBigDecimal().setScale(scale,BigDecimal.ROUND_DOWN).toPlainString()
    }
}