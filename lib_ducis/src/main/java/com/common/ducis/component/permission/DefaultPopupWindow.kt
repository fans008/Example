package com.common.ducis.component.permission

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import com.common.ducis.R
import com.common.ducis.databinding.PopupCommonBinding
import razerdp.basepopup.BasePopupWindow


/**
 *@describe：清理缓存弹窗
 *@author：ftt
 *@date：2020/4/1
 */
class DefaultPopupWindow(context: Context?) : BasePopupWindow(context) {

    private var binding: PopupCommonBinding? = null
    var mListener: OnSelectedResultListener? = null

    init {
        initView()
    }
    private fun initView() {
        setContentView(R.layout.popup_common)
        popupGravity = Gravity.CENTER
        binding?.apply {
            setViewClickListener({ v: View -> click(v) },tvPopupSure,tvPopupCancel)
        }
    }

   override fun onViewCreated(contentView: View) {
        binding = PopupCommonBinding.bind(contentView)
    }

    fun click(v: View) {
        when (v.id) {
            R.id.tv_popup_sure -> {
                mListener?.onSelected(true)
            }
            R.id.tv_popup_cancel -> {
                mListener?.onSelected(false)
            }
        }
        dismiss()
    }

    fun setTitleText(title: String,sureText:String = "确定",cancelText:String = "取消"){
        popupGravity = Gravity.CENTER
        binding?.apply {
            tvPopupTitle.text = title
            tvPopupSure.text = sureText
            tvPopupCancel.text = cancelText
        }
    }

    fun setTextColor(titleColor:Int ,sureColor:Int,cancelColor:Int){
        binding?.apply {
            tvPopupTitle.setTextColor(ContextCompat.getColor(context,titleColor))
            tvPopupSure.setTextColor(ContextCompat.getColor(context,sureColor))
            tvPopupCancel.setTextColor(ContextCompat.getColor(context,cancelColor))
        }
    }

    fun setBackgroundForResource(backResource:Int){
        contentView.findViewById<RelativeLayout>(R.id.rl_popup).setBackgroundResource(backResource)
    }

    fun setOnClickResultListener(listener: OnSelectedResultListener){
        this.mListener = listener
    }

    interface OnSelectedResultListener {
        fun onSelected(
            isSure: Boolean
        )
    }
}