package com.common.ducis.commonutil.toast

import android.app.Application
import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast

/**
 * @ClassName: ToastConfig
 * @Description:
 * @Author: Fan TaoTao
 * @Date: 2022/8/29
 */
@SuppressLint("StaticFieldLeak")
object ToastConfig {

    internal var toast: Toast? = null
    internal lateinit var context: Context

    /** 构建吐司 */
    @JvmField
    var toastFactory: ToastFactory = ToastFactory

    /**
     * 初始化
     * 如果应用存在多进程使用则必须使用本方法初始化, 否则是可选
     * @param toastFactory 构建吐司
     */
    @JvmOverloads
    @JvmStatic
    fun initialize(application: Application, toastFactory: ToastFactory? = null) {
        this.context = application
        if (toastFactory != null) {
            this.toastFactory = toastFactory
        }
    }

    /** 取消吐司显示 */
    @JvmStatic
    fun cancel() {
        toast?.cancel()
    }

}