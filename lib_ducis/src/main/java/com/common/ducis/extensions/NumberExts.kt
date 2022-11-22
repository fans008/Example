package com.common.ducis.extensions

import java.math.BigDecimal
import java.text.DecimalFormat

/**
 * @ClassName: NumberExts.kt
 * @Description:数字格式化
 * @Author: Fan TaoTao
 * @Date: 2022/4/5
 */

//保留小数位4位此处默认为四舍五入
fun Double.removeEndZeros(scale: Int = 4, mode: Int = BigDecimal.ROUND_HALF_UP): String {
    // 不足两位小数补0
    val decimalFormat = DecimalFormat("0.0000");
    return decimalFormat.format(BigDecimal(this.toString()).setScale(scale, mode))
}

//去除末尾多余的0
fun Float.removeEndZeros(scale:Int = 4,mode: Int = BigDecimal.ROUND_HALF_UP):String{
    val decimalFormat = DecimalFormat("0.0000");
    return decimalFormat.format(BigDecimal(this.toString()).setScale(scale, mode))
}

//去除末尾多余的0
fun BigDecimal.removeEndZeros(scale:Int = 4,mode: Int = BigDecimal.ROUND_HALF_UP):String{
    val decimalFormat = DecimalFormat("0.0000");
    return decimalFormat.format(this.setScale(scale, mode))
}

//保留小数位并清除多余的0,舍入模式可自定义，此处默认为四舍五入
fun Double.setScale(scale:Int,mode: Int = BigDecimal.ROUND_HALF_UP):String{
    if (scale ==  4){
        val decimalFormat = DecimalFormat("0.0000");
        return decimalFormat.format(BigDecimal(this.toString()).setScale(scale, mode))
    }else{
        return BigDecimal(this.toString()).setScale(scale, mode).toPlainString()
    }
}

//保留小数位并清除多余的0,舍入模式可自定义，此处默认为四舍五入
fun Float.setScale(scale:Int,mode: Int = BigDecimal.ROUND_HALF_UP):String{
    val decimalFormat = DecimalFormat("0.0000");
    return decimalFormat.format(BigDecimal(this.toString()).setScale(scale, mode))
}
