package com.common.ducis.commonutil.toast

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.LayoutRes
import com.common.ducis.R

/**
 * @ClassName: ToastGravityFactory
 * @Description:屏幕居中显示吐司
 * @Author: Fan TaoTao
 * @Date: 2022/8/29
 */
open class ToastGravityFactory @JvmOverloads constructor(
    val gravity: Int = Gravity.CENTER,
    @LayoutRes val layout: Int = R.layout.layout_toast_gravity,
    val xOffset: Int = 0,
    val yOffset: Int = 0,
) : ToastFactory {

    /**
     * 创建吐司
     * @param context Application
     * @param message 吐司内容
     * @param tag 吐司标签
     */
    override fun onCreate(
        context: Context,
        message: CharSequence,
        duration: Int,
        tag: Any?
    ): Toast? {
        val toast = Toast.makeText(context, message, duration)
        val view = View.inflate(context, layout, null)
        view.findViewById<TextView>(android.R.id.message).text = message
        toast.view = view
        toast.setGravity(gravity, xOffset, yOffset)
        return toast
    }
}