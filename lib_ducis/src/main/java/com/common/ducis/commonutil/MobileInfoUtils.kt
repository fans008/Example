package com.common.ducis.commonutil

import android.annotation.SuppressLint
import android.os.Build
import java.util.*

/**
 *@describe：
 *@author：ftt
 *@date：2020/3/12
 */
object MobileInfoUtils {
    @SuppressLint("MissingPermission")
    fun getUUID(): String? {
        var serial: String?
        val mSzdevidshort =
            "35" + Build.BOARD.length % 10 + Build.BRAND.length % 10 + Build.CPU_ABI.length % 10 +
                    Build.DEVICE.length % 10 + Build.DISPLAY.length % 10 + Build.HOST.length % 10 +
                    Build.ID.length % 10 + Build.MANUFACTURER.length % 10 + Build.MODEL.length % 10 +
                    Build.PRODUCT.length % 10 + Build.TAGS.length % 10 + Build.TYPE.length % 10 +
                    Build.USER.length % 10 //13 位
        try {
            serial = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Build.getSerial()
            } else {
                Build.SERIAL
            }
            //API>=9 使用serial号
            return UUID(mSzdevidshort.hashCode().toLong(), serial.hashCode().toLong()).toString()
        } catch (exception: Exception) { //serial需要一个初始化
            serial = "serial" // 随便一个初始化
        }
        //使用硬件信息拼凑出来的15位号码
        return UUID(mSzdevidshort.hashCode().toLong(), serial.hashCode().toLong()).toString()
    }

    /**
     * 获取厂商名
     */
    fun getDeviceManuFacturer(): String? {
        return Build.MANUFACTURER
    }

    /**
     * 获取手机产品名称
     */
    fun getDeviceProduct(): String? {
        return Build.PRODUCT
    }

    /**
     * 获取手机品牌
     */
    fun getDeviceBrand(): String? {
        return Build.BRAND
    }

    /**
     * 获取手机型号
     */
    fun getDeviceModel(): String? {
        return Build.MODEL
    }

    /**
     * 设备名称
     */
    fun getDevice(): String? {
        return Build.DEVICE
    }
    /**
     * 获取系统版本号
     */
    fun getSystemVersion(): String? {
        return Build.VERSION.SDK_INT.toString()
    }

}