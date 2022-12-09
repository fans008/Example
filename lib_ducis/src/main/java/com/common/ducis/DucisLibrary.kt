package com.common.ducis

import android.app.Application
import android.app.ProgressDialog
import android.content.Context
import com.common.ducis.commonutil.MySharedPreferences
import com.common.ducis.file.FileUtils
import com.common.ducis.commonutil.log.MyLog
import com.common.ducis.commonutil.toast.ToastConfig
import com.common.ducis.exception.CrashCatchHandler
import com.common.ducis.component.network.bean.ProxyBeanView
import com.common.ducis.component.network.common.GsonConverter
import com.drake.net.NetConfig
import com.drake.net.interceptor.LogRecordInterceptor
import com.drake.net.interceptor.RequestInterceptor
import com.drake.net.okhttp.setConverter
import com.drake.net.okhttp.setDialogFactory
import com.drake.net.okhttp.setLog
import com.drake.net.okhttp.setRequestInterceptor
import com.drake.net.request.BaseRequest
import java.util.concurrent.TimeUnit

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
     * 初始化网络相关
     * @param url String
     */
    fun initNet(url:String){
        this.baseUrl = url
        NetConfig.initialize(url) {
            // 超时设置
            connectTimeout(2, TimeUnit.MINUTES)
            readTimeout(2, TimeUnit.MINUTES)
            writeTimeout(2, TimeUnit.MINUTES)

            setLog(true) // LogCat异常日志
            addInterceptor(LogRecordInterceptor(true)) // 添加日志记录器
            setRequestInterceptor(object : RequestInterceptor { // 添加请求拦截器
                override fun interceptor(request: BaseRequest) {
                    request.addHeader("client", "Net")
                    request.setHeader("token", "123456")
                }
            })

            setConverter(GsonConverter()) // 数据转换器

            setDialogFactory { // 全局加载对话框
                ProgressDialog(it).apply {
                    setMessage("加载中...")
                }
            }
        }
    }

    /**
     * 初始化代理
     */
    fun initProxy(context:Context,vararg params:String){
        var ipList = MySharedPreferences.getListData(context,"ipList", ProxyBeanView::class.java)
        if (ipList.isNullOrEmpty()){
            ipList = mutableListOf()
            params.forEach {
                ipList.add(ProxyBeanView(it, 1000))
            }
            MySharedPreferences.putListData(context,"ipList",ipList)
            MyLog.i("ipList","新存入ip：$ipList")
        }
        MyLog.i("ipList","已存在$ipList")
    }
}