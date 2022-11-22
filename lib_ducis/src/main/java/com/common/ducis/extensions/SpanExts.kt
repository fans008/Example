package com.common.ducis.extensions

import android.graphics.Typeface
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.widget.TextView
import androidx.core.content.ContextCompat

/**
 *Span相关
 */

/**
 * 设置文字大小
 */
fun TextView.setSpanTextSize(fullStr: String, target: String, textSize: Int) {
    text = SpannableString(fullStr).apply {
        val start = fullStr.indexOf(target)
        if (start < 0) {
            return@apply
        }
        setSpan(
            AbsoluteSizeSpan(textSize, true),
            start,
            start + target.length,
            SpannableString.SPAN_INCLUSIVE_EXCLUSIVE
        )
    }
    this.movementMethod = LinkMovementMethod.getInstance()
}

/**
 * 设置文字颜色
 */
fun TextView.setSpanTextColor(fullStr: String, target: String, color: Int) {
    text = SpannableString(fullStr).apply {
        val start = fullStr.indexOf(target)
        if (start < 0) {
            return@apply
        }
        setSpan(
            ForegroundColorSpan(ContextCompat.getColor(context, color)),
            start,
            start + target.length,
            SpannableString.SPAN_INCLUSIVE_EXCLUSIVE
        )
    }
    this.movementMethod = LinkMovementMethod.getInstance()
}


/**
 * 设置文字样式
 */
fun TextView.setSpanTextStyle(fullStr: String, target: String, style: Int = Typeface.BOLD) {
    text = SpannableString(fullStr).apply {
        val start = fullStr.indexOf(target)
        if (start < 0) {
            return@apply
        }
        setSpan(
            StyleSpan(style),
            start,
            start + target.length,
            SpannableString.SPAN_INCLUSIVE_EXCLUSIVE
        )
    }
    this.movementMethod = LinkMovementMethod.getInstance()
}