package com.common.ducis.commonutil

import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.os.LocaleList
import com.google.gson.Gson
import java.util.*

/**
 * @ClassName: LocalManagerUtil
 * @Description:
 * @Author: Fan TaoTao
 * @Date: 2021/3/10
 */
object LocalManageUtil {
    /**
     * 获取选择的语言设置
     * @param context
     * @return
     */
    fun getSetLanguageLocale(context: Context): Locale {
        var local = Locale.SIMPLIFIED_CHINESE
        if (!MySharedPreferences.getString(context, "language", MySharedPreferences.APP_DATA).isNullOrEmpty()){
            local = Gson().fromJson(MySharedPreferences.getString(context, "language", MySharedPreferences.APP_DATA), Locale::class.java)
        }
        return local
    }

    /**
     * 设置 本地语言
     *
     * @param context
     * @param select
     */
    fun saveSelectLanguage(context: Context, select: Locale,point:Int) {
        MySharedPreferences.setString(context, "language", Gson().toJson(select), MySharedPreferences.APP_DATA)
        MySharedPreferences.setInt(context, "local", point, MySharedPreferences.APP_DATA)
        setApplicationLanguage(context)
    }

    /**
     * 初始化语言 方法
     *
     * @param context
     */
    fun setLocal(context: Context): Context {
        return setApplicationLanguage(context)
    }

    /**
     * 设置语言类型
     */
    fun setApplicationLanguage(context: Context): Context {
        val resources = context.resources
        val dm = resources.displayMetrics
        val config = resources.configuration
        val locale = getSetLanguageLocale(context) //获取sp里面保存的语言
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val localeList = LocaleList(locale)
            LocaleList.setDefault(localeList)
            config.setLocales(localeList)
            Locale.setDefault(locale)
            return context.createConfigurationContext(config)
        } else {
            config.locale = locale
        }
        resources.updateConfiguration(config, dm)
        return context
    }

    /**
     * 获取App的locale
     *
     * @return Locale对象
     */
    val appLocale: Locale
        get() {
            val locale: Locale
            locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                LocaleList.getDefault()[0]
            } else {
                Locale.getDefault()
            }
            return locale
        }

    /**
     * 获取系统local
     *
     * @return
     */
    val systemLocale: Locale
        get() = Resources.getSystem().configuration.locale

    /**
     * 获取本地保存的语言
     *
     * @param context
     * @return
     */
    fun getLocalSaveLanguage(context: Context): String {
        val locale = getSetLanguageLocale(context)
        var language = locale.language
        if (language == "zh") {
            language = "zh-TW"
        } else if (language == "en") {
            language = "en"
        } else {
            language = "zh-TW"
        }
        return language
    }
}
