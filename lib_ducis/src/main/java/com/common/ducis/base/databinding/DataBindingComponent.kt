package com.common.ducis.base.databinding

/**
 * @ClassName: DataBindingComponent
 * @Description:
 * @Author: Fan TaoTao
 * @Date: 2022/3/15
 */
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContextWrapper
import android.graphics.Paint
import android.os.Build
import android.util.TypedValue
import android.view.View
import android.view.View.NO_ID
import android.webkit.WebView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.core.view.ViewCompat
import androidx.databinding.BindingAdapter
import androidx.databinding.BindingMethod
import androidx.databinding.BindingMethods
import com.common.ducis.commonutil.extensions.removeEndZeros
import com.common.ducis.commonutil.extensions.singleClick
import java.math.RoundingMode
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*


@BindingMethods(
    BindingMethod(type = View::class, attribute = "android:enabled", method = "enabled"),
    BindingMethod(type = View::class, attribute = "android:selected", method = "selected"),
    BindingMethod(type = View::class, attribute = "android:activated", method = "activated"),
)
object DataBindingComponent {


    /**
     * 水平布局设置权重
     * @param view View
     * @param weight Float
     */
    @BindingAdapter("android:layout_weight_horizontal")
    @JvmStatic
    fun setLayoutHeight(view:View, weight:Float) {
        val params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, weight)
        view.layoutParams = params
    }

    //<editor-fold desc="间距">
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @BindingAdapter("paddingStart", "paddingEnd", requireAll = false)
    @JvmStatic
    fun setPaddingHorizontal(v: View, start: View?, end: View?) {
        v.post {
            val startFinal = (start?.width ?: 0) + v.paddingStart
            val endFinal = (end?.width ?: 0) + v.paddingEnd
            v.setPaddingRelative(startFinal, v.paddingTop, endFinal, v.paddingBottom)
        }
    }
    //</editor-fold>

    // <editor-fold desc="图片">

    @BindingAdapter(
        value = ["leftDrawable", "topDrawable", "rightDrawable", "bottomDrawable"],
        requireAll = false
    )
    @JvmStatic
    fun setImageDrawable(
        v: TextView,
        leftDrawable: Int, topDrawable: Int, rightDrawable: Int, bottomDrawable: Int
    ) {
        v.setCompoundDrawablesWithIntrinsicBounds(leftDrawable, topDrawable, rightDrawable, bottomDrawable)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @BindingAdapter(
        value = ["startDrawable", "topDrawable", "endDrawable", "bottomDrawable"],
        requireAll = false
    )
    @JvmStatic
    fun setImageDrawableRelative(
        v: TextView,
        startDrawable: Int, topDrawable: Int, endDrawable: Int, bottomDrawable: Int
    ) {
        v.setCompoundDrawablesRelativeWithIntrinsicBounds(startDrawable, topDrawable, endDrawable, bottomDrawable)
    }

    @BindingAdapter("android:background")
    @JvmStatic
    fun setBackgroundRes(v: View, drawableId: Int) {
        if (drawableId > NO_ID) v.setBackgroundResource(drawableId) else v.background = null
    }

    @BindingAdapter("android:src")
    @JvmStatic
    fun setImageDrawable(v: ImageView, @DrawableRes drawableId: Int) {
        if (drawableId > NO_ID) v.setImageResource(drawableId) else v.setImageDrawable(null)
    }

    // </editor-fold>


    // <editor-fold desc="隐藏">

    /**
     * 隐藏控件
     * @param isVisible 当为true则显示[View.VISIBLE], 否则隐藏[View.INVISIBLE]
     */
    @BindingAdapter("invisible")
    @JvmStatic
    fun setVisibleOrInvisible(v: View, isVisible: Boolean) {
        v.visibility = if (isVisible) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }
    }

    /**
     * 隐藏控件
     * @param isVisible 当不为null则显示[View.VISIBLE], 否则隐藏[View.INVISIBLE]
     */
    @BindingAdapter("invisible")
    @JvmStatic
    fun setVisibleOrInvisible(v: View, isVisible: Any?) {
        v.visibility = if (isVisible != null) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }
    }

    /**
     * 隐藏控件
     * @param isVisible 当为true则隐藏[View.GONE], 否则显示[View.VISIBLE]
     */
    @BindingAdapter("gone")
    @JvmStatic
    fun setVisibleOrGone(v: View, isVisible: Boolean) {
        v.visibility = if (isVisible) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    // </editor-fold>


    // <editor-fold desc="阴影">

    @BindingAdapter("android:elevation")
    @JvmStatic
    fun setElevation(v: View, dp: Int) {
        ViewCompat.setElevation(v, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), v.resources.displayMetrics))
    }

    // </editor-fold>

    // <editor-fold desc="状态">

    @BindingAdapter("android:enabled")
    @JvmStatic
    fun setEnabled(v: View, enable: Any?) {
        v.isEnabled = enable != null
    }

    @BindingAdapter("selected")
    @JvmStatic
    fun setSelected(v: View, selected: Any?) {
        v.isSelected = selected != null
    }

    @BindingAdapter("selected")
    @JvmStatic
    fun setSelected(v: View, selected: Boolean) {
        v.isSelected = selected
    }

    @BindingAdapter("activated")
    @JvmStatic
    fun setActivated(v: View, activated: Any?) {
        v.isActivated = activated != null
    }

    // </editor-fold>


    // <editor-fold desc="点击事件">

    /**
     * 防止暴力点击
     */
    @SuppressLint("CheckResult")
    @BindingAdapter("click")
    @JvmStatic
    fun setThrottleClickListener(v: View, onClickListener: View.OnClickListener?) {
        if (onClickListener != null) {
            v.singleClick { onClickListener.onClick(v) }
        }
    }


    /**
     * 自动将点击事件映射到Activity上
     * @param throttle 是否只支持快速点击
     */
    @SuppressLint("CheckResult")
    @BindingAdapter("hit")
    @JvmStatic
    fun hit(v: View, throttle: Boolean = true) {
        var context = v.context
        while (context is ContextWrapper) {
            if (context is View.OnClickListener) {
                val clickListener = context as View.OnClickListener
                if (throttle) {
                    v.singleClick { clickListener.onClick(v) }
                } else {
                    v.setOnClickListener(clickListener)
                }
            }
            context = context.baseContext
        }
    }


    /**
     * 关闭当前界面
     * @param enabled 是否启用
     */
    @SuppressLint("CheckResult", "ObsoleteSdkInt")
    @BindingAdapter("finish")
    @JvmStatic
    fun finishActivity(v: View, enabled: Boolean = true) {
        if (enabled) {
            var temp = v.context
            var activity: Activity? = null

            while (temp is ContextWrapper) {
                if (temp is Activity) {
                    activity = temp
                }
                temp = temp.baseContext
            }

            val finalActivity = activity

            v.singleClick {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    finalActivity!!.finishAfterTransition()
                } else {
                    finalActivity!!.finish()
                }
            }
        }
    }

    // </editor-fold>


    // <editor-fold desc="货币">

    /**
     * 求总价
     */
    @BindingAdapter(value = ["price", "count", "unit"], requireAll = false)
    @JvmStatic
    fun setTotal(v: TextView, price: Double,count:Int, unit: String?) {
        v.text = "${(price * count).removeEndZeros()}$unit"
    }
    /**
     * 格式化RMB
     * @param number 货币数量
     * @param unit 货币种类, 默认为 ¥
     */
    @SuppressLint("SetTextI18n")
    @BindingAdapter("rmb", "rmbUnit", requireAll = false)
    @JvmStatic
    fun formatCNY(v: TextView, number: String?, unit: String?) {
        if (!number.isNullOrEmpty() && v.text.contentEquals(number)) {
            val format = "${unit ?: "¥"}${number.format()}"
            if (format != v.text.toString()) v.text = format
        }
    }

    /**
     * 格式化RMB
     * @param number 货币数量
     * @param prefix 货币种类, 默认为 ¥
     * @param roundingMode 货币保留2位小数时的规则, 默认为[java.math.RoundingMode.UP]
     */
    @SuppressLint("SetTextI18n")
    @BindingAdapter("rmb", "rmbUnit", "roundingMode", requireAll = false)
    @JvmStatic
    fun formatCNY(v: TextView, number: Double?, prefix: String?, roundingMode: RoundingMode?) {
        val numberFormat = NumberFormat.getInstance()
        numberFormat.minimumFractionDigits = 2
        numberFormat.maximumFractionDigits = 2
        numberFormat.roundingMode = roundingMode ?: RoundingMode.UP
        val format = "${prefix ?: "¥"}${numberFormat.format(number ?: 0.0)}"
        if (format != v.text.toString()) v.text = format
    }

    /**
     * 设置rmb，默认看做 "分" 处理(除以100)
     * @param number 货币数量
     * @param prefix 货币种类, 默认为 ¥
     * @param roundingMode 货币保留2位小数时的规则, 默认为[java.math.RoundingMode.UP]
     */
    @SuppressLint("SetTextI18n")
    @BindingAdapter("rmb", "rmbUnit", "roundingMode", requireAll = false)
    @JvmStatic
    fun formatCNY(v: TextView, number: Long?, prefix: String?, roundingMode: RoundingMode?) {
        val numberFormat = NumberFormat.getInstance()
        numberFormat.minimumFractionDigits = 2
        numberFormat.maximumFractionDigits = 2
        numberFormat.roundingMode = roundingMode ?: RoundingMode.UP
        val format = "${prefix ?: "¥"}${numberFormat.format(number ?: 0 / 100.0)}"
        if (format != v.text.toString()) v.text = format
    }

    // </editor-fold>

    // <editor-fold desc="时间">

    /**
     * 根据时间产生格式化字符串
     * @param milli 指定时间, 单位毫秒, 如果小于0将设置为空字符串
     * @param format 格式化文本
     */
    @BindingAdapter(value = ["dateMilli", "dateFormat"], requireAll = false)
    @JvmStatic
    fun setDateFromMillis(v: TextView, milli: Long, format: String? = "yyyy-MM-dd") {
        if (milli < 0) {
            v.text = ""
            return
        }
        val finalFormat = if (format.isNullOrBlank()) "yyyy-MM-dd" else format
        val date = Date(milli)
        val sf = SimpleDateFormat(finalFormat, Locale.CHINA)
        val formatText = sf.format(date)
        if (v.text.contentEquals(formatText)) return
        v.text = formatText
    }


    /**
     * 根据时间产生格式化字符串
     * @param milli 指定时间, 单位毫秒, 如果小于0将设置为空字符串
     * @param format 格式化文本
     */
    @BindingAdapter(value = ["dateMilli", "dateFormat"], requireAll = false)
    @JvmStatic
    fun setDateFromMillis(v: TextView, milli: String?, format: String? = "yyyy-MM-dd") {
        val finalFormat = if (format.isNullOrBlank()) "yyyy-MM-dd" else format
        val finalMilli = milli?.toLongOrNull() ?: return
        if (finalMilli < 0 || milli.isNullOrBlank()) {
            v.text = ""
            return
        }
        val date = Date(finalMilli)
        val sf = SimpleDateFormat(finalFormat, Locale.CHINA)
        val formatText = sf.format(date)
        if (v.text.contentEquals(formatText)) return
        v.text = formatText
    }

    /**
     * 根据时间产生格式化字符串
     * @param second 指定时间, 单位秒, 如果小于0将设置为空字符串
     * @param format 格式化文本
     */
    @BindingAdapter(value = ["dateSecond", "dateFormat"], requireAll = false)
    @JvmStatic
    fun setDateFromSecond(v: TextView, second: Long, format: String? = "yyyy-MM-dd") {
        if (second < 0) {
            v.text = ""
            return
        }
        val finalFormat = if (format.isNullOrBlank()) "yyyy-MM-dd" else format
        val date = Date(second * 1000)
        val sf = SimpleDateFormat(finalFormat, Locale.CHINA)
        val formatText = sf.format(date)
        if (v.text.contentEquals(formatText)) return
        v.text = formatText
    }

    /**
     * 根据时间产生格式化字符串
     * @param second 指定时间, 单位秒, 如果小于0将设置为空字符串
     * @param format 格式化文本
     */
    @BindingAdapter(value = ["dateSecond", "dateFormat"], requireAll = false)
    @JvmStatic
    fun setDateFromSecond(v: TextView, second: String?, format: String? = "yyyy-MM-dd") {
        val finalFormat = if (format.isNullOrBlank()) "yyyy-MM-dd" else format
        val finalSecond = second?.toLongOrNull() ?: return
        if (finalSecond < 0 || second.isNullOrBlank()) {
            v.text = ""
            return
        }
        val date = Date(finalSecond * 1000)
        val sf = SimpleDateFormat(finalFormat, Locale.CHINA)
        val formatText = sf.format(date)
        if (v.text.contentEquals(formatText)) return
        v.text = formatText
    }

    // </editor-fold>

    //<editor-fold desc="字符串">
    @BindingAdapter("del")
    @JvmStatic
    fun setDel(v: TextView, isAdd: Boolean) {
        if (isAdd) {
            v.paint.flags = Paint.STRIKE_THRU_TEXT_FLAG or Paint.ANTI_ALIAS_FLAG   // 设置中划线并加清晰
        }
    }

    @BindingAdapter("android:text")
    @JvmStatic
    fun setTextOfNumber(v: TextView, number: Int) {
        val finalText = number.toString()
        if (!v.text.contentEquals(finalText)) {
            v.text = finalText
        }
    }

    @BindingAdapter("android:text")
    @JvmStatic
    fun setTextOfNumber(v: TextView, number: Long) {
        val finalText = number.toString()
        if (!v.text.contentEquals(finalText)) {
            v.text = finalText
        }
    }

    @BindingAdapter("android:text")
    @JvmStatic
    fun setTextOfNumber(v: TextView, number: Double) {
        val finalText = number.toString()
        if (!v.text.contentEquals(finalText)) {
            v.text = finalText
        }
    }

    @BindingAdapter("android:text")
    @JvmStatic
    fun setTextOfNumber(v: TextView, number: Float) {
        val finalText = number.toString()
        if (!v.text.contentEquals(finalText)) {
            v.text = finalText
        }
    }

    //</editor-fold>

    //<editor-fold desc="网页">
    @BindingAdapter("url")
    @JvmStatic
    fun setUrl(v: WebView, url: String?) {
        if (!url.isNullOrEmpty()) {
            v.loadDataWithBaseURL(null, url, "text/html", "UTF-8", null)
        }
    }
    //</editor-fold>

    //<editor-fold desc="回调">
    /**
     * 在绑定视图时可以用于Model来处理UI, 由于破坏视图和逻辑解耦的规则不是很建议使用
     *
     * @see OnBindListener 该接口支持泛型定义具体视图
     *
     * @receiver View
     * @param listener OnBindListener<View>
     */
    @BindingAdapter("onBind")
    @JvmStatic
    fun setOnBindListener(v: View, listener: OnBindListener) {
        listener.onBind(v)
    }

    /**
     * 在绑定视图时可以用于Model来处理UI, 由于破坏视图和逻辑解耦的规则不是很建议使用
     */
    interface OnBindListener {
        fun onBind(v: View)
    }
    //</editor-fold>
}