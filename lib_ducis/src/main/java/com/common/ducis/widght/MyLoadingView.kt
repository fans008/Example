package com.common.ducis.widght

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.common.ducis.R

/**
 * @ClassName: MyLoadingView
 * @Description:
 * @Author: Fan TaoTao
 * @Date: 2020/7/28
 */
class MyLoadingView(context: Context) :
    Dialog(context, R.style.Loading) {
    override fun onBackPressed() {
        super.onBackPressed()
        dismiss()
    }

    init {
        // 加载布局
        setContentView(R.layout.view_loading)
        val ivLoading = findViewById<ImageView>(R.id.iv_loading)
        Glide.with(context).asGif().load(R.drawable.loading).into(ivLoading)
        // 设置Dialog参数
        val window = window
        val params = window!!.attributes
        params.gravity = Gravity.CENTER
        window.attributes = params
    }

}