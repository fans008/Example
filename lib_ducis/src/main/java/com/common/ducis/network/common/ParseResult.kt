package com.common.ducis.network.common

import io.anyone.ducis.network.bean.common.HttpError

/**
 *    author : plm
 *    date   : 2021/10/19
 *    desc   :
 */
sealed class ParseResult<out T : Any> {
    /* 请求成功，返回成功响应  */
    data class Success<out T : Any>(val data: T?) : ParseResult<T>()

    /* 请求成功，返回失败响应 */
    data class Failure(val status: String, var msg: String? = null) :
        ParseResult<Nothing>()

    /* 请求失败，抛出异常 */
    data class ERROR(val ex: Throwable, val error: HttpError) : ParseResult<Nothing>()

    private var successBlock: (suspend (data: T?) -> Unit)? = null
    private var failureBlock: (suspend (status: String, msg: String?) -> Unit)? = null
    private var errorBlock: (suspend (ex: Throwable, error: HttpError) -> Unit)? = null
    private var cancelBlock: (suspend () -> Unit)? = null

    /**
     * 设置网络请求成功处理
     */
    fun doSuccess(successBlock: (suspend (data: T?) -> Unit)?): ParseResult<T> {
        this.successBlock = successBlock
        return this
    }

    /**
     * 设置网络请求失败处理
     */
    fun doFailure(failureBlock: (suspend (status: String, msg: String?) -> Unit)?): ParseResult<T> {
        this.failureBlock = failureBlock
        return this
    }

    /**
     * 设置网络请求异常处理
     */
    fun doError(errorBlock: (suspend (ex: Throwable, error: HttpError) -> Unit)?): ParseResult<T> {
        this.errorBlock = errorBlock
        return this
    }

    /**
     * 设置网络请求取消处理
     */
    fun doCancel(cancelBlock: (suspend () -> Unit)?): ParseResult<T> {
        this.cancelBlock = cancelBlock
        return this
    }

    suspend fun procceed() {
        when (this) {
            is Success<T> -> successBlock?.invoke(data)
            is Failure -> failureBlock?.invoke(status, msg)
            is ERROR -> {
                if (this.error == HttpError.CANCEL_REQUEST) {
                    cancelBlock?.invoke()
                } else {
                    errorBlock?.invoke(ex, error)
                }
            }
        }
    }
}
