package com.common.ducis.exception

import java.util.*

/**
 * @describe：
 * @author：ftt
 * @date：2019/11/8
 */
class ExceptionBean {
    /**
     * @Field("accountNumber")accountNumber:String?
     * ,@Field("deviceName")deviceName:String?
     * ,@Field("exceptionName")exceptionName:String?
     * ,@Field("exceptionDetail")exceptionDetail:String?
     * ,@Field("imei")imei:String?
     * ,@Field("manufacturer")manufacturer:String?
     * ,@Field("systemVersion")systemVersion:String?
     * ,@Field("version")version:String?
     * ,@Field("time")
     */
    /**
     * 当前应用版本
     */
    var appVersionId: Long = 0

    /**
     * 异常名称
     */
    var exceptionName: String? = null

    /**
     * 异常详情
     */
    var exceptionDetail: String? = null

    /**
     * 异常发生时间
     */
    var time: Date? = null

    /**
     * 设备名称
     */
    var deviceName: String? = null

    /**
     * 设备厂商
     */
    var manufacturer: String? = null

    /**
     * 设备唯一识别码
     */
    var imei: String? = null

    /**
     * android系统版本号
     */
    var systemVersion: String? = null

    /**
     * 用户身份识别码
     */
    var clientId: String? = null
    var authId: String? = null

    /**
     * 项目唯一标识
     */
    var projectName: String? = null

    /**
     * 项目秘钥
     */
    var secret: String? = null
    override fun toString(): String {
        return "ExceptionBean{" +
                "appVersionId=" + appVersionId +
                ", exceptionName='" + exceptionName + '\'' +
                ", exceptionDetail='" + exceptionDetail + '\'' +
                ", time=" + time +
                ", deviceName='" + deviceName + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", imei='" + imei + '\'' +
                ", systemVersion='" + systemVersion + '\'' +
                ", clientId='" + clientId + '\'' +
                ", authId='" + authId + '\'' +
                ", projectName='" + projectName + '\'' +
                ", secret='" + secret + '\'' +
                '}'
    }
}