package com.common.ducis.websdk

import android.app.Activity
import android.webkit.JavascriptInterface
import com.common.ducis.commonutil.log.MyLog
import com.common.ducis.commonutil.toast.toast
import com.google.gson.Gson
import com.tencent.smtt.sdk.WebView

/**
 * @ClassName: BaseWebViewAgent
 * @Description:
 * @Author: Fan TaoTao
 * @Date: 2021/8/30
 */
open class BaseWebViewAgent(private val mContext: Activity, private val webView: WebView) {

    open val JS_TAG = "BaseWebView JS Start"

    /**
     * 通用js請求結果 map集合
     */
    open var responseMap: MutableMap<String, String?> = mutableMapOf()

    //-------------------------------------UI模块----------------------------------------------------
    /**
     * 设置标题 如有需要子类自己实现
     * @param params String
     */
    @JavascriptInterface
    fun setTitle(params:String){
        MyLog.d(JS_TAG, "setTitle : (params:$params)")
    }

    /**
     * 设置二级标题 如有需要子类自己实现
     * @param params String
     */
    @JavascriptInterface
    fun setSecondTitle(params:String){
        MyLog.d(JS_TAG, "setSecondTitle : (params:$params)")
    }

    /**
     * 设置标题栏右侧按钮文案  如有需要子类自己实现
     * @param params String
     */
    @JavascriptInterface
    fun setRightButton(params:String){
        MyLog.d(JS_TAG, "setRightButton : (params:$params)")
    }

    /**
     * 返回上一页
     */
    @JavascriptInterface
    fun goBack(){
        MyLog.d(JS_TAG, "goBack")
        if (webView.canGoBack()){
            webView.goBack()
        }else{
            mContext.finish()
        }
    }

    /**
     * 去往下一页
     */
    @JavascriptInterface
    fun goForward(){
        MyLog.d(JS_TAG, "goForward")
        if (webView.canGoForward()){
            webView.goForward()
        }
    }

    /**
     * 获取状态栏高度
     */
    @JavascriptInterface
    open fun getStatusBarHeight(params: String?) {
        var param = Gson().fromJson<WebViewRequestParam>(params, WebViewRequestParam::class.java)
        if (param?.responseId == null) return
        MyLog.d(JS_TAG, "getStatusBarHeight :(params:$params) ")
        var result = 0
        val resourceId = mContext.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = mContext.resources.getDimensionPixelSize(resourceId)
        }
        responseMap[param.responseId] = result.toString()
        responseToJs(param.responseId)
    }

    /**
     * 获取当前页面截图
     * @param params String?
     */
    @JavascriptInterface
    open fun getCurrentPageScreen(params: String?) {
        MyLog.d(JS_TAG, "getCurrentPageScreen :(params:$params) ")
    }

    /**
     * 展示webViewToast
     */
    @JavascriptInterface
    fun showWebToast(params: String?) {
        MyLog.d(JS_TAG, "showWebToast :(params:$params) ")
        val param = Gson().fromJson(params, WebViewRequestParam::class.java)
        mContext.runOnUiThread {
            if (param.data is ToastData){
                (param.data as ToastData).toast.let { toast(it) }
            }
        }
    }

    //----------------------------------------------功能模块----------------------------------------------------

    /**
     * 获取当前缓存大小
     * @param params String
     */
    @JavascriptInterface
    open fun getCacheSize(params:String?){
        MyLog.d(JS_TAG, "getCacheSize :(params:$params) ")
    }

    /**
     * 清理缓存
     */
    @JavascriptInterface
    open fun cleanCache(params:String?){
        MyLog.d(JS_TAG, "cleanCache :(params:$params) ")
    }

    /**
     * 获取应用版本信息
     * @param params String?
     */
    @JavascriptInterface
    fun getVersionInfo(params:String?){
        MyLog.d(JS_TAG, "getVersionInfo :(params:$params) ")
    }

    /**
     * 更新版本
     * @param params String?
     */
    @JavascriptInterface
    fun updateVersion(params: String?){
        MyLog.d(JS_TAG, "updateVersion :(params:$params) ")
    }

    /**
     * ajax代理
     * @param params String?
     */
    @JavascriptInterface
    open fun agentAjax(params: String?){
        MyLog.d(JS_TAG, "agentAjax :(params:$params) ")
    }

    /**
     * 调用本地摄像头扫码
     * @param params String?
     */
    @JavascriptInterface
    open fun startScanning(params: String?){
        MyLog.d(JS_TAG, "startScanning :(params:$params) ")
    }

    /**
     * 调用本地拍照
     * @param params String?
     */
    @JavascriptInterface
    open fun startCamera(params: String?){
        MyLog.d(JS_TAG, "startCamera :(params:$params) ")
    }

    /**
     * 调用本地相册
     * @param params String?
     */
    @JavascriptInterface
    open fun startGallery(params: String?){
        MyLog.d(JS_TAG, "startGallery :(params:$params) ")
    }

    /**
     * 选择文件
     * @param params String?
     */
    open fun asyncSelectFile(params: String?){
        MyLog.d(JS_TAG, "asyncSelectFile :(params:$params) ")
    }

    /**
     * 选择多个文件
     * @param params String?
     */
    open fun asyncSelectFiles(params: String?){
        MyLog.d(JS_TAG, "asyncSelectFiles :(params:$params) ")
    }

    /**
     * 选择单个图片
     * @param params String?
     */
    open fun asyncSelectImage(params: String?){
        MyLog.d(JS_TAG, "asyncSelectImage :(params:$params) ")
    }

    /**
     * 选择多个图片
     * @param params String?
     */
    open fun asyncSelectImages(params: String?){
        MyLog.d(JS_TAG, "asyncSelectImages :(params:$params) ")
    }

    /**
     * 扫描二维码
     * @param params String?
     */
    open fun asyncScanQrCode(params: String?){
        MyLog.d(JS_TAG, "asyncScanQrCode :(params:$params) ")
    }

    /**
     * 扫描条形码
     * @param params String?
     */
    open fun asyncScanBarCode(params: String?){
        MyLog.d(JS_TAG, "asyncScanBarCode :(params:$params) ")
    }



    /**
     * 將結果告知js
     * @param responseId String
     */
    open fun responseToJs(responseId: String) {
        mContext.runOnUiThread {
            val script = String.format(
                WebJavascriptCommon.response,
                responseId,
                responseMap[responseId]
            )
            MyLog.d(JS_TAG, "回傳數據：$script")
            webView.evaluateJavascript(script, null)
        }
    }
}