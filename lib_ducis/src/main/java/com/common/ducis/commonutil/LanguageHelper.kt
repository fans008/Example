package com.common.ducis.commonutil

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.LocaleList
import android.preference.PreferenceManager
import android.text.TextUtils
import java.util.*

/**
 * @ClassName: LanguageHelper
 * @Description:
 * @Author: Fan TaoTao
 * @Date: 2021/3/4
 */
object LanguageHelper {
    /**
     * 保存选择的语言
     */
    fun saveSelectLanguage(context: Context, language: String) {
        MySharedPreferences.setString(context,"language",language,MySharedPreferences.APP_DATA)
    }

    fun setLocal(context: Context): Context? {
        return updateResources(context, getSetLanguageLocale(context))
    }

    /**
     * 获取选择的语言设置
     * @param context
     * @return
     */
    private fun getSetLanguageLocale(context: Context): Locale {
        if (context == null) Locale.CHINA
        var language = MySharedPreferences.getString(context,"language",MySharedPreferences.APP_DATA)
        return when (language) {
            "auto" -> getSystemLocale(context)
            "zh" -> Locale.SIMPLIFIED_CHINESE
            "en" -> Locale.ENGLISH
            "zh-rTW" -> Locale.TRADITIONAL_CHINESE
            else -> Locale.SIMPLIFIED_CHINESE
        }
    }

    /**
     * 获取系统的locale
     */
    private fun getSystemLocale(context: Context?): Locale {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LocaleList.getDefault()[0]
        } else {
            Locale.getDefault()
        }
    }

    private fun updateResources(context: Context, locale: Locale): Context? {
        var context: Context = context
        Locale.setDefault(locale)
        val res: Resources = context.resources
        val config = Configuration(res.configuration)
        if (Build.VERSION.SDK_INT >= 17) {
            config.setLocale(locale)
            context = context.createConfigurationContext(config)
        } else {
            config.locale = locale
            res.updateConfiguration(config, res.displayMetrics)
        }
        return context
    }

    fun isZh(context: Context): Boolean {
        val locale = context.resources.configuration.locale
        val language = locale.language
        return language.endsWith("zh")
    }

    fun isZhTw(context: Context): Boolean {
        val locale = context.resources.configuration.locale
        val language = locale.language
        return language.endsWith("zh-rTW")
    }


    /**
     * 语言有变化后,全局设置语言类型
     */
    fun setApplicationLanguage(context: Context) {
        val resources = context.applicationContext.resources
        val dm = resources.displayMetrics
        val config = resources.configuration
        val locale = getSetLanguageLocale(context)
        config.locale = locale
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val localeList = LocaleList(locale)
            LocaleList.setDefault(localeList)
//            config.locales = localeList
            context.applicationContext.createConfigurationContext(config)
            Locale.setDefault(locale)
        }
        resources.updateConfiguration(config, dm)
    }
}