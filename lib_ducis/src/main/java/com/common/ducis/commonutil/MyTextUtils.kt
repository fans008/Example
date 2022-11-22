package com.common.ducis.commonutil

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.common.ducis.commonutil.toast.toast

/**
 * Created by Administrator on 2018/1/19.
 */
object MyTextUtils {
    /**
     * 一段文字中多处需要修改颜色
     * @param context
     * @param textView
     * @param startPosition
     * @param endPosition
     * @param text
     * @param color
     */
    fun setSpannable(
        context: Context,
        textView: TextView,
        startPosition: IntArray,
        endPosition: IntArray,
        text: String?,
        color: Int
    ) {
        val span = SpannableStringBuilder(text)
        for (i in startPosition.indices) {
            span.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(context, color)),
                startPosition[i],
                endPosition[i],
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE
            )
        }
        textView.text = span
    }

    /**
     * 一段文字中指定位置修改颜色
     * @param context
     * @param textView
     * @param startPosition
     * @param endPosition
     * @param text
     * @param color
     */
    fun setSingleSpannable(
        context: Context,
        textView: TextView,
        startPosition: Int,
        endPosition: Int,
        text: String?,
        color: Int
    ) {
        val span = SpannableStringBuilder(text)
        span.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(context, color)),
            startPosition,
            endPosition,
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )
        textView.text = span
    }

    /**
     * 一段文字中指定位置修改颜色
     * @param context
     * @param startPosition
     * @param endPosition
     * @param text
     * @param color
     */
    fun getSingleSpannable(
        context: Context,
        startPosition: Int,
        endPosition: Int,
        text: String?,
        color: Int
    ): SpannableStringBuilder {
        val span = SpannableStringBuilder(text)
        span.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(context, color)),
            startPosition,
            endPosition,
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )
        return span
    }

    /**
     * 设置一段文字某段大小和加粗
     * @param textString
     * @param tv
     * @param start
     * @param end
     * @param dp
     */
    fun setSubscriptSizeAndBold(
        textString: String?,
        tv: TextView,
        start: Int,
        end: Int,
        dp: Int
    ) {
        val span: Spannable = SpannableString(textString)
        dp.let { span.setSpan(AbsoluteSizeSpan(it), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE) }
        span.setSpan(StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        tv.text = span
    }

    /**
     * 设置一段文字加粗
     * @param tv
     * @param isBold
     */
    fun setTextIsBold(
        tv: TextView,
        isBold: Boolean,
        size: Int
    ) {
        if (isBold) {
            tv.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
        } else {
            tv.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
        }
        val span: Spannable = SpannableString(tv.text)
        span.setSpan(AbsoluteSizeSpan(size), 0, tv.text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        tv.text = span
        tv.postInvalidate()
    }

    fun showCopyContent(context: Context, content: String, toastContext: String = "复制成功") {
        if (content.isEmpty()) {
            return
        }
        //获取剪贴板管理器：
        var cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        // 创建普通字符型ClipData
        val mClipData = ClipData.newPlainText("user_uid", content)
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData)
        toast(toastContext)
    }

    //设置输入小数位数过滤器
    fun setEditTextFilter(p0: CharSequence, editText: EditText, lengthFilter: Int) {
        if (p0.toString().contains(".")) {
            if (p0.length - 1 - p0.toString().indexOf(".") > lengthFilter) {
                var temp = p0.toString().subSequence(0, p0.toString().indexOf(".") + lengthFilter + 1)
                editText.setText(temp)
                editText.setSelection(temp.length)
            }
        }
        if (p0.toString().trim().substring(0) == ".") {
            var temp = "0$p0"
            editText.setText(temp)
            editText.setSelection(2)
        }
        if (p0.toString().startsWith("0")
            && p0.toString().trim().length > 1
        ) {
            if (!p0.toString().substring(1, 2).equals(".")) {
                editText.setText(p0.subSequence(0, 1))
                editText.setSelection(1)
                return
            }
        }
        editText.setSelection(editText.text.length)
    }
}