package com.common.ducis.network

/**
 * @ClassName: HttpConfig
 * @Description:
 * @Author: Fan TaoTao
 * @Date: 2022/3/2
 */
object HttpConfig {
    const val BASE_URL = ""
    //请求类型（后台识别）
    const val head_request_type_key = "m-request-type"
    const val head_request_type_normal = "normal"
    const val head_request_type_mrpc = "mrpc"
    //序列化方式
    const val head_serializer_format_key = "mrpc-serializer-format"
    const val head_serializer_format_json = "JSON"
    //数据体格式
    const val head_content_type_key = "Content-Type"
    const val head_content_type_json = "application/json"
    const val head_content_type_stream = "application/octet-stream"
    const val head_content_type_form = "multipart/form-data"
    //ducis后台传输方式特有
    const val head_request_profile_key = "m-request-profile"
    const val head_request_profile_product = "product"
    //语言
    const val head_request_language_key = "language"
    const val head_request_language_zh = "zh_CN"
    const val head_request_language_zh_tw = "zh_TW"
    const val head_request_language_en = "en"
    //版本
    const val head_version_key = "appVersion"
    const val head_version_versionName = "versionName"
    const val head_version_versionCode = "versionCode"
    //用户相关
    const val ACCOUNT_ID = "account_id"//用户id
    const val ACCOUNT_BEAN = "account_bean"//用户详情（json字符串）
    const val COOKIE = "cookie"//cookie
    const val COOKIE_AUTH_ID = "cookie_auth_id"//cookie用户唯一标识
    const val TOKEN = "token"//token用户唯一标识
    const val LOGIN_STATE = "login_state"//登录状态
    const val LOGIN_FIRST = "login_first"//是否首次登录过，针对1.0.3版本以后用户新增参数
}