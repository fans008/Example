package com.common.ducis.widght

import android.app.Activity
import android.widget.TextView
import androidx.core.content.ContextCompat
import java.util.*

/**
 *@describe：
 *@author：ftt
 *@date：2020/3/17
 */
class MyTimerTask(
    private val context: Activity,
    private val textView: TextView,
    private val normalColor: Int,
    private val cancelColor: Int
) : TimerTask() {

    var recLen = 60
    var content = ""
    var resetGet = ""
    override fun run() {
        recLen--
        context.runOnUiThread {
            textView.setTextColor(ContextCompat.getColor(context, cancelColor))
            textView.text =  String.format(content, recLen)
            if (recLen == 0) {
                recLen = 60
                this.cancel()
                textView.setTextColor(ContextCompat.getColor(context, normalColor))
                textView.isClickable = true
                textView.text = "重新获取"
            }
        }
    }

    fun downTime(content: String,resetGet:String = "重新获取") {
        this.content = content
        this.resetGet = resetGet
        val timer = Timer()
        textView.text =  String.format(content, recLen)
        textView.setTextColor(ContextCompat.getColor(context, cancelColor))
        textView.isClickable = false
        timer.schedule(this, 1000, 1000) // timeTask
    }
}