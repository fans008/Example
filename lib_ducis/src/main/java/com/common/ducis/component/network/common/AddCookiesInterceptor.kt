package common

import com.common.ducis.DucisLibrary
import com.common.ducis.commonutil.MySharedPreferences
import com.common.ducis.component.network.HttpConfig
import com.common.ducis.commonutil.VersionUtil
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * @ClassName: AddCookiesInterceptor
 * @Description:
 * @Author: Fan TaoTao
 * @Date: 2020/10/23
 */
class AddCookiesInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        val cookies = MySharedPreferences.getString(DucisLibrary.appContext, HttpConfig.COOKIE,MySharedPreferences.APP_DATA)
        //cookie信息
        cookies?.let { builder.addHeader(HttpConfig.COOKIE, it)}
        //请求类型（后台识别）
        builder.addHeader(HttpConfig.head_request_type_key, HttpConfig.head_request_type_mrpc)
        //序列化方式
        builder.addHeader(HttpConfig.head_serializer_format_key, HttpConfig.head_serializer_format_json)
        //数据体格式
        builder.addHeader(HttpConfig.head_content_type_key, HttpConfig.head_content_type_json)
        //ducis后台传输方式特有 省略外层回传
        builder.addHeader(HttpConfig.head_request_profile_key, HttpConfig.head_request_profile_product)
        //版本信息
        builder.addHeader(
            HttpConfig.head_version_key, "${HttpConfig.head_version_versionName}=${VersionUtil.getVersionName()};" +
                "${HttpConfig.head_version_versionCode}=${VersionUtil.getVersionCode()}")
        return chain.proceed(builder.build())
    }
}
