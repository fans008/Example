package com.common.ducis.base

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import razerdp.basepopup.BasePopupWindow


abstract class BaseBindingPopupWindow<B : ViewDataBinding>(private val context: Context?,private val layoutId:Int) : BasePopupWindow(context) {

    protected lateinit var binding: B

    init {
        contentView = createPopupById(layoutId)
    }

    override fun onViewCreated(contentView: View) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), layoutId, null, false)
    }
}