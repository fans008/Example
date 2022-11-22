package com.common.ducis.commonutil.toast

import android.content.Context
import android.widget.Toast

/**
 * @ClassName: ToastFactory
 * @Description:
 * @Author: Fan TaoTao
 * @Date: 2022/8/29
 */
interface ToastFactory {
    companion object DEFAULT : ToastFactory {
        override fun onCreate(
            context: Context,
            message: CharSequence,
            duration: Int,
            tag: Any?
        ): Toast? {
            return Toast.makeText(ToastConfig.context, message, duration)
        }
    }

    /**
     * 创建吐司
     * @param context Application
     * @param message 吐司内容
     * @param tag 吐司标签
     */
    fun onCreate(
        context: Context,
        message: CharSequence,
        duration: Int,
        tag: Any? = null
    ): Toast?
}