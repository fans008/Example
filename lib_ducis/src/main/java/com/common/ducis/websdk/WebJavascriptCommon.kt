package com.common.ducis.websdk

/**
 * @ClassName: WebJavascriptCommon
 * @Description:
 * @Author: Fan TaoTao
 * @Date: 2021/4/21
 */
object WebJavascriptCommon {

    /**  通用echo */
    const val response = "javaScript:M.client.response('%s','%s')"

    /**  android js sdk*/
    private const val clientUrl = "https://dev.ducis.cn/h5/macaw-js/dist/beta/20220114/client/macaw-client-android.js"

    /** load js */
    const val loadClient = "javaScript:M.loadClient('$clientUrl')"
}