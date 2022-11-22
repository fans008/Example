package io.anyone.ducis.network.bean.common

/**
 *    author : plm
 *    date   : 2021/10/19
 *    desc   :
 */
enum class HttpError(val code: Int, val message: String) {
    // 未知错误
    UNKNOWN(-1, "未知错误"),

    // 网络连接错误
    CONNECT_ERROR(-2, "网络错误"),

    // 连接超时
    CONNECT_TIMEOUT(-3, "连接超时"),

    // 错误的请求
    BAD_NETWORK(-4, "错误的请求"),

    // 数据解析错误
    PARSE_ERROR(-5, "数据解析错误"),

    // 取消请求
    CANCEL_REQUEST(-6, "取消请求"),

}