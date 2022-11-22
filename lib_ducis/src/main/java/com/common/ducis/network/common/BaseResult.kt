package com.common.ducis.network.common


/**
 * @ClassName: BaseResult.kt
 * @Description: 网络请求默认格式
 * @Author: Fan TaoTao
 * @Date: 2022/3/9
 */
open class BaseResult<T> {
    var status: String? = null
    var message: String? = null
    var data: T? = null

    fun isSuccess():Boolean{
        return status?.uppercase() == "SUCCESS"
    }
    override fun toString(): String {
        return "BaseResult(status=$status, message=$message, data=$data)"
    }

    fun getResultData():T?{
        return data
    }
}