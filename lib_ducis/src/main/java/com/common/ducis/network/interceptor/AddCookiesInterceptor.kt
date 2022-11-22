package com.common.ducis.network.interceptor

import android.content.Context
import android.os.Build
import com.common.ducis.commonutil.LocalManageUtil
import com.common.ducis.commonutil.MySharedPreferences
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.util.*

/**
 * @ClassName: AddCookiesInterceptor
 * @Description:
 * @Author: Fan TaoTao
 * @Date: 2020/10/23
 */
class AddCookiesInterceptor(private val context: Context) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        val cookies = MySharedPreferences.getHashMapData(
            context,
            "cookies",
            MySharedPreferences.APP_DATA
        )

        var cookieBuffer = StringBuffer()
        if (cookies.isNotEmpty()) {
            for (cookie in cookies) {
                cookieBuffer.append(cookie.value + ";")
            }
        }
        if (cookieBuffer.length > 1) {
            builder.addHeader("cookie", cookieBuffer.substring(0, cookieBuffer.length - 1))
        }
        builder.addHeader("m-request-type", "mrpc")
        builder.addHeader("mrpc-serializer-format", "JSON")
        builder.addHeader("Content-Type", "application/octet-stream")
        builder.addHeader("m-request-profile", "product")
//        if (MySharedPreferences.getString(context, "language", MySharedPreferences.APP_DATA).isNullOrEmpty()) {
//            builder.addHeader("language", "${Locale.TRADITIONAL_CHINESE.language}_${Locale.TRADITIONAL_CHINESE.country}")
//        } else {
//            val language = LocalManageUtil.getSetLanguageLocale(context).language
//            var country = LocalManageUtil.getSetLanguageLocale(context).country
//            if (language.isEmpty() && language == "en") {
//                country = Locale.US.country
//            }
//            builder.addHeader("language", "${language}_${country}")
//        }
        builder.addHeader(
            "appVersion", "versionName=${context.packageManager.getPackageInfo(context.packageName, 0).versionName};" +
                    "versionCode=${
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        context.packageManager.getPackageInfo(context.packageName, 0).longVersionCode.toString()
                    } else {
                        context.packageManager.getPackageInfo(context.packageName, 0).versionCode.toString()
                    }}"
        )
        return chain.proceed(builder.build())
    }
}
