package com.common.ducis.component.websdk

data class WebViewRequestParam(
    var responseId: String,
    var data: Any? = null,
)

/**
 * 代理ajax请求
 * @property url String
 * @property method String
 * @property data Any?
 * @property dataType String?
 * @constructor
 */
data class AjaxData(val url: String, val method: String = "POST", var data: Any?, val dataType: String?)

/**
 * 调用原生toast
 * @property toast String
 * @constructor
 */
data class ToastData(val toast: String)

/**
 * 设置标题
 * @property title String
 * @constructor
 */
data class TitleData(val title: String)

/**
 * 设置副标题
 * @property secondTitle String
 * @constructor
 */
data class SubTitleData(val secondTitle: String)

/**
 * 定位参数
 * @property maximumAge Int 缓存有效时间，0表示不使用缓存，正数表示缓存有效的毫秒数，默认0
 * @property timeout Long 获取超时时间，默认是Infinity
 * @property enableHighAccuracy Boolean 是否尝试获取更高精度的定位，可能会导致返回速度较慢
 * @constructor
 */
data class LocationData(val maximumAge: Int, val timeout: Long, val enableHighAccuracy: Boolean)
