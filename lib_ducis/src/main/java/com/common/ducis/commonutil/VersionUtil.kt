package com.common.ducis.commonutil

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import com.common.ducis.DucisLibrary

/**
 * @ClassName: VersionUtil
 * @Description:
 * @Author: Fan TaoTao
 * @Date: 2022/3/3
 */
object VersionUtil {
    /**
     * 获取版本名字
     * @return String?
     */
    fun getVersionName():String?{
        try {
            val manager: PackageManager = DucisLibrary.appContext.packageManager
            val info: PackageInfo = manager.getPackageInfo(DucisLibrary.appContext.packageName, 0)
            return info.versionName
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 获取版本号
     * @return Long
     */
    fun getVersionCode():Long{
        try {
            val manager: PackageManager = DucisLibrary.appContext.packageManager
            val info: PackageInfo = manager.getPackageInfo(DucisLibrary.appContext.packageName, 0)
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                info.longVersionCode
            } else {
                info.versionCode.toLong()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0
    }

    /**
     * 是否需要更新版本
     * @param versionCode Long
     * @param versionName String?
     * @return Boolean
     */
    fun isUpdateVersion(versionCode:Long,versionName:String?):Boolean{
        return versionCode > getVersionCode() || versionName != getVersionName()
    }
}