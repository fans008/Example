package com.common.ducis.ui.permission

import android.content.Context
import java.lang.NullPointerException

class Acp private constructor(context: Context) {
    val acpManager: AcpManager

    init {
        acpManager = AcpManager(context.applicationContext)
    }

    /**
     * 开始请求
     *
     * @param options
     * @param acpListener
     */
    fun request(options: AcpOptions?, acpListener: AcpListener?) {
        if (options == null) NullPointerException("AcpOptions is null...")
        if (acpListener == null) NullPointerException("AcpListener is null...")
        acpManager.request(options, acpListener)
    }

    companion object {
        private var mInstance: Acp? = null
        @JvmStatic
        fun getInstance(context: Context): Acp {
            if (mInstance == null) synchronized(Acp::class.java) {
                if (mInstance == null) {
                    mInstance = Acp(context)
                }
            }
            return mInstance!!
        }
    }
}