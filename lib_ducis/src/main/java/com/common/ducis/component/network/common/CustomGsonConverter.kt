package com.common.ducis.component.network.common

import com.common.ducis.DucisLibrary
import com.common.ducis.commonutil.MySharedPreferences
import com.common.ducis.commonutil.toast.toast
import com.drake.net.NetConfig
import com.drake.net.convert.NetConverter
import com.drake.net.exception.ConvertException
import com.drake.net.exception.RequestParamsException
import com.drake.net.exception.ResponseException
import com.drake.net.exception.ServerResponseException
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.lang.reflect.Type

abstract class CustomJsonConverter(
    val status: String = "status",
    val message: String = "message",
    val data: String = "data"
) : NetConverter {

    override fun <R> onConvert(succeed: Type, response: Response): R? {
        try {
            return NetConverter.onConvert<R>(succeed, response)
        } catch (e: ConvertException) {
            val code = response.code
            when {
                code in 200..299 -> { // 请求成功
                    val bodyString = response.body?.string() ?: return null
                    return try {
                        val json = JSONObject(bodyString)
                        when(json.getString(this.status)){
                            "SUCCESS" ->{
                                bodyString.parseBody<R>(succeed)
                            }
                            "FAILURE" ->{
                                val errorMessage = json.optString(message, NetConfig.app.getString(com.drake.net.R.string.no_error_message))
                                toast(errorMessage)
                                bodyString.parseBody<R>(succeed)
                            }
                            "UNAUTHORIZATION" ->{
                                MySharedPreferences.clearSP(DucisLibrary.appContext,MySharedPreferences.APP_DATA)
                                val errorMessage = json.optString(message, "登录超时")
                                throw ResponseException(response, errorMessage)
                            }
                            else ->{
                                val errorMessage = json.optString(message, NetConfig.app.getString(com.drake.net.R.string.no_error_message))
                                throw ResponseException(response, errorMessage)
                            }
                        }
                    } catch (e: JSONException) { // 固定格式JSON分析失败直接解析JSON
                        bodyString.parseBody<R>(succeed)
                    }
                }
                code in 400..499 -> throw RequestParamsException(response, code.toString()) // 请求参数错误
                code >= 500 -> throw ServerResponseException(response, code.toString()) // 服务器异常错误
                else -> throw ConvertException(response)
            }
        }
    }

    /**
     * 反序列化JSON
     *
     * @param succeed JSON对象的类型
     * @receiver 原始字符串
     */
    abstract fun <R> String.parseBody(succeed: Type): R?
}