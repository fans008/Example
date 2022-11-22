package com.common.ducis.base

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import android.os.Parcelable
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import com.common.ducis.DucisLibrary
import com.common.ducis.commonutil.LocalManageUtil
import com.common.ducis.commonutil.MyActivityManager
import com.common.ducis.commonutil.log.MyLog
import com.gyf.immersionbar.ImmersionBar
import com.luck.picture.lib.immersive.ImmersiveManager
import org.greenrobot.eventbus.EventBus
import java.io.Serializable
import java.util.*


abstract class BaseActivity : AppCompatActivity(),ViewBehavior {

    private lateinit var TAG: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TAG = this.javaClass.simpleName
        MyLog.i(TAG,"$TAG is onCreate")
        initContentView()
        ImmersionBar.with(this).statusBarDarkFont(DucisLibrary.isDarkFont).init()
        getResources(this)
        initView()
        initData()
        MyActivityManager.pushActivity(this)
    }

    protected open fun initContentView() {
        setContentView(getLayoutId())
    }

    @LayoutRes
    protected abstract fun getLayoutId(): Int

    protected abstract fun initView()

    protected abstract fun initData()

    override fun navigate(clazz: Class<*>, vararg data: Pair<String, Any?>) {
        val application = application
        val intent = Intent(application, clazz)
        data.forEach {
            when (it.second) {
                is Boolean -> intent.putExtra(it.first, it.second as Boolean)
                is Byte -> intent.putExtra(it.first, it.second as Byte)
                is Int -> intent.putExtra(it.first, it.second as Int)
                is Short -> intent.putExtra(it.first, it.second as Short)
                is Long -> intent.putExtra(it.first, it.second as Long)
                is Float -> intent.putExtra(it.first, it.second as Float)
                is Double -> intent.putExtra(it.first, it.second as Double)
                is Char -> intent.putExtra(it.first, it.second as Char)
                is String -> intent.putExtra(it.first, it.second as String)
                is Serializable -> intent.putExtra(it.first, it.second as Serializable)
                is Parcelable -> intent.putExtra(it.first, it.second as Parcelable)
            }
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        application.startActivity(intent)
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

    override fun onStart() {
        super.onStart()
        MyLog.i(TAG,"$TAG is onStart")
    }

    override fun onRestart() {
        super.onRestart()
        MyLog.i(TAG,"$TAG is onRestart")
    }

    override fun onResume() {
        super.onResume()
        MyLog.i(TAG,"$TAG is onResume")
    }

    override fun onPause() {
        super.onPause()
        MyLog.i(TAG,"$TAG is onPause")
    }

    override fun onStop() {
        super.onStop()
        MyLog.i(TAG,"$TAG is onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        MyLog.i(TAG,"$TAG is onRestart")
        MyActivityManager.popActivity(this)
    }
}