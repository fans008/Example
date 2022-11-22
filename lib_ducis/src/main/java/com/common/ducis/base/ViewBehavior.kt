package com.common.ducis.base

/**
 * @author: Albert Li
 * @contact: albertlii@163.com
 * @time: 2020/6/9 8:01 PM
 * @description: 页面的常用操作
 * @since: 1.0.0
 */
interface ViewBehavior {
    /**
     * 是否显示Loading视图
     */
    fun showLoadingView(isShow: Boolean) {}

    /**
     * 是否显示空白视图
     */
    fun showEmptyView(isShow: Boolean) {}

    /**
     * 弹出Toast提示
     */
    fun showToast(toast: String?) {}

    /**
     * 页面显示内容
     */
    fun showErrorPage(msg: String?) {}

    /**
     * 页面跳转
     */
    fun navigate(clazz: Class<*>, vararg data: Pair<String, Any?>) {}

    /**
     * 返回键点击
     */
    fun backPress(arg: Any?) {}

    /**
     * 关闭页面
     */
    fun finishPage(arg: Any?) {}
}