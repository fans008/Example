package com.common.ducis.databinding

import android.text.SpannableStringBuilder
import android.widget.TextView
import androidx.databinding.BindingAdapter

/**
 * @ClassName: SpanDataBindingComponent
 * @Description:
 * @Author: Fan TaoTao
 * @Date: 2022/11/17
 */
object SpanDataBindingComponent {

    /**
     * 加载富文本格式
     * @param url 图片来源
     * @param holder 占位图
     * @param corner 设置圆角, 如果设置参数则默认为圆形
     */
    @BindingAdapter(value = ["span"], requireAll = false)
    @JvmStatic
    @Deprecated("使用span")
    fun loadImageCornerWithHolder(v: TextView, span: SpannableStringBuilder?) {
        if (span == null)
            return
        v.text = span
    }
}