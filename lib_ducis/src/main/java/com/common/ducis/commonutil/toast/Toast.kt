package com.common.ducis.commonutil.toast

import android.annotation.SuppressLint
import androidx.annotation.StringRes
import com.common.ducis.commonutil.toast.ThreadUtils.runMain
/**
 * @ClassName: Toast
 * @Description:
 * @Author: Fan TaoTao
 * @Date: 2022/8/29
 */
/**
 * 短时间显示的吐司
 * @param msg 吐司内容
 * @param tag 标记, 标记用于[com.common.ducis.commonutil.toast]区分吐司
 */
@JvmOverloads
fun toast(@StringRes msg: Int, tag: Any? = null) {
    showToast(ToastConfig.context.getString(msg), 0, tag)
}

/**
 * 短时间显示的吐司
 * @param msg 吐司内容
 * @param tag 标记, 标记用于[com.common.ducis.commonutil.toast]区分吐司
 */
@JvmOverloads
fun toast(msg: CharSequence?, tag: Any? = null) {
    showToast(msg, 0, tag)
}

/**
 * 长时间显示的吐司
 * @param msg 吐司内容
 * @param tag 标记, 标记用于[com.common.ducis.commonutil.toast]区分吐司
 */
@JvmOverloads
fun longToast(@StringRes msg: Int, tag: Any? = null) {
    longToast(ToastConfig.context.getString(msg), tag)
}

/**
 * 长时间显示的吐司
 * @param msg 吐司内容
 * @param tag 标记, 标记用于[com.common.ducis.commonutil.toast]区分吐司
 */
@JvmOverloads
fun longToast(msg: CharSequence?, tag: Any? = null) {
    showToast(msg, 1, tag)
}

/**
 * 显示吐司
 * @param msg 吐司内容
 * @param duration 吐司显示时长 0 短时间显示 1 长时间显示
 * @param tag 标记, 标记用于[com.common.ducis.commonutil.toast]区分吐司
 */
@SuppressLint("ShowToast")
private fun showToast(msg: CharSequence?, duration: Int, tag: Any?) {
    msg ?: return
    ToastConfig.toast?.cancel()
    runMain {
        ToastConfig.toast = ToastConfig.toastFactory.onCreate(ToastConfig.context, msg, duration, tag)
        ToastConfig.toast?.show()
    }
}