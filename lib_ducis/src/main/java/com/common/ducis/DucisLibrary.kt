package com.common.ducis

import android.app.Application
import android.content.Context
import com.common.ducis.commonutil.MySharedPreferences
import com.common.ducis.file.FileUtils
import com.common.ducis.commonutil.log.MyLog
import com.common.ducis.commonutil.toast.ToastConfig
import com.common.ducis.exception.CrashCatchHandler
import com.common.ducis.component.network.bean.common.ProxyBeanView

/**
 * @ClassName: DucisLibrary
 * @Description:
 * @Author: Fan TaoTao
 * @Date: 2020/11/3
 */
object DucisLibrary {


    lateinit var appContext:Application

    var isDarkFont = false

    var baseUrl = ""

    fun init(context: Application, isOpenLog:Boolean){
        ToastConfig.initialize(context)
        MyLog.setDebug(isOpenLog)
        appContext = context
        FileUtils.initPath()
        CrashCatchHandler.instance.init(context)
    }

    /**
     * 设置状态栏颜色
     */
    fun setThemeIsDark(isDarkFont:Boolean){
        this.isDarkFont = isDarkFont
    }

    /**
     * 设置baseUrl
     * @param url String
     */
    fun setBaseUrlAddress(url:String){
        baseUrl = url
    }

    /**
     * 初始化代理
     */
    fun initProxy(context:Context,vararg params:String){
        var ipList = MySharedPreferences.getListData(context,"ipList", com.common.ducis.component.network.bean.common.ProxyBeanView::class.java)
        if (ipList.isNullOrEmpty()){
            ipList = mutableListOf()
            params.forEach {
                ipList.add(com.common.ducis.component.network.bean.common.ProxyBeanView(it, 1000))
            }
            MySharedPreferences.putListData(context,"ipList",ipList)
            MyLog.i("ipList","新存入ip：$ipList")
        }
        MyLog.i("ipList","已存在$ipList")
    }
}