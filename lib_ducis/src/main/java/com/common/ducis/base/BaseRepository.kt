package com.common.ducis.base

import android.util.Log
import com.common.ducis.component.network.common.ParseResult
import com.common.ducis.component.network.common.ResponseData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


open class BaseRepository {
    suspend fun <T : Any> request(call: suspend () -> ResponseData<T>): ResponseData<T> {
        return withContext(Dispatchers.IO) {
            call.invoke() }.apply {
            when (status) {
                "SUCCESS" -> Log.i("请求状态值:$status", "请求成功")
                "FAILURE" -> Log.e("请求状态值:$status", "请求失败----->失败原因:$message")
                "UNAUTHORIZATION" -> Log.e("请求状态值:$status", "未授权")
                else -> Log.e("请求状态值:$status", "请求失败----->失败原因:$message")
            }
        }
    }

    suspend fun <T : Any> convert(call: suspend () -> ParseResult<T>): ParseResult<T> {
        return withContext(Dispatchers.IO) {
            call.invoke() }.apply {
        }
    }
}