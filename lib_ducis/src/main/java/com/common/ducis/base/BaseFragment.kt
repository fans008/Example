package com.common.ducis.base

import android.app.Activity
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.common.ducis.commonutil.LocalManageUtil
import com.common.ducis.commonutil.log.MyLog
import java.util.*

/**
 *    author : plm
 *    date   : 2021/9/1
 *    desc   :
 */
abstract class BaseFragment : Fragment() {
    private var rootView: View? = null

    private lateinit var TAG: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TAG = this::class.java.name
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        MyLog.i(TAG,"$TAG is onCreateView")
        if (rootView != null) {
            return rootView
        }
        rootView = inflater.inflate(getLayoutId(), container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        MyLog.i(TAG,"$TAG is onViewCreated")
        try {
            initView()
            initData()
        } catch (e: Exception) {
            MyLog.e(TAG, "初始化失败")
            e.printStackTrace()
        }
    }

    protected fun getRootView(): View? {
        return rootView
    }

    protected fun setRootView(view: View) {
        this.rootView = view
    }

    /**
     * 重写getResources()方法，让APP的字体不受系统设置字体大小影响
     */
    open fun getResources(activity: Activity): Resources? {
        val res = activity.resources
        val newConfig = Configuration()
        newConfig.setToDefaults() //设置默认
        val locale = LocalManageUtil.getSetLanguageLocale(activity)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val localeList = LocaleList(locale)
            LocaleList.setDefault(localeList)
            Locale.setDefault(locale)
        } else {
            newConfig.locale = locale
        }
        res.updateConfiguration(newConfig, res.displayMetrics)
        return res
    }


    @LayoutRes
    protected abstract fun getLayoutId(): Int

    protected abstract fun initView()
    protected abstract fun initData()
}