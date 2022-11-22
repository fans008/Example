package com.common.ducis.commonutil.extensions

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

/**
 * @ClassName: TimeExts.kt
 * @Description:时间类扩展方法
 * @Author: Fan TaoTao
 * @Date: 2022/3/27
 */

/**
 * 返回日期时间格式
 * yyyy-MM-dd HH:mm:ss
 */
@SuppressLint("SimpleDateFormat")
fun Long.toDateTime():String{
    val date = Date(this)
    val pattern = "yyyy-MM-dd HH:mm:ss"
    val format = SimpleDateFormat(pattern)
    return format.format(date)
}

/**
 * 返回日期格式
 * yyyy-MM-dd
 */
@SuppressLint("SimpleDateFormat")
fun Long.toDate():String{
    val date = Date(this*1000)
    val pattern = "yyyy-MM-dd"
    val format = SimpleDateFormat(pattern)
    return format.format(date)
}

/**
 * 返回日期格式
 * yyyy-MM-dd
 */
@SuppressLint("SimpleDateFormat")
fun Long.toTime():String{
    val date = Date(this)
    val pattern = "HH:mm:ss"
    val format = SimpleDateFormat(pattern)
    return format.format(date)
}

/**
 * 返回日期格式
 * yyyy-MM-dd
 */
@SuppressLint("SimpleDateFormat")
fun Long.toTime(pattern:String):String{
    val date = Date(this*1000)
    val format = SimpleDateFormat(pattern)
    return format.format(date)
}