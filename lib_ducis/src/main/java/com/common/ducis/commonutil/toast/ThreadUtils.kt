package com.common.ducis.commonutil.toast

import android.os.Handler
import android.os.Looper

/**
 * @ClassName: ThreadUtils
 * @Description:
 * @Author: Fan TaoTao
 * @Date: 2022/8/29
 */
internal object ThreadUtils {
    fun runMain(block: () -> Unit) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            block()
        } else {
            Handler(Looper.getMainLooper()).post { block() }
        }
    }
}