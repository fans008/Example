package com.common.ducis.network.common

import com.common.ducis.DucisLibrary
import com.common.ducis.commonutil.MySharedPreferences
import com.common.ducis.network.HttpConfig
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * @ClassName: ReceivedCookiesInterceptor
 * @Description:
 * @Author: Fan TaoTao
 * @Date: 2020/10/23
 */
@Suppress("NAME_SHADOWING")
class ReceivedCookiesInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        //这里获取请求返回的cookie
        val originalResponse = chain.proceed(chain.request())
        if (originalResponse.headers("set-cookie").isNotEmpty()) {
            val cookieValue = originalResponse.headers("set-cookie")
            val cookieBuffer = StringBuffer()
            for (cookie in cookieValue) {
                if (cookie.startsWith("MINI")){
                    MySharedPreferences.setString(DucisLibrary.appContext,HttpConfig.COOKIE_AUTH_ID,cookie,MySharedPreferences.APP_DATA)
                }
                cookieBuffer.append("$cookie;")
            }
            if (cookieBuffer.toString().isNotEmpty()){
                MySharedPreferences.setString(DucisLibrary.appContext,HttpConfig.COOKIE,cookieBuffer.toString(),MySharedPreferences.APP_DATA)
            }
        }
        //用户信息
        originalResponse.header("mrpc-auth-id")?.let {
            MySharedPreferences.setString(DucisLibrary.appContext,HttpConfig.COOKIE_AUTH_ID,it,MySharedPreferences.APP_DATA)
        }
        return originalResponse
    }
}