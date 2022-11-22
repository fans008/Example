package com.common.ducis.network.common

/**
 *    author : plm
 *    date   : 2021/9/1
 *    desc   :数据类基类
 */
data class ResponseData<out T>(val status: String, val message: String, val data: T){
    companion object{
        const val STATUS_SUCCESS = "SUCCESS"
        const val STATUS_FAILURE = "FAILURE"
        const val STATUS_ERROR = "ERROR"
    }
}