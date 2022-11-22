package com.common.ducis.commonutil.toast

import android.content.Context
import androidx.startup.Initializer

/**
 * @ClassName: TooltipInitializer
 * @Description:
 * @Author: Fan TaoTao
 * @Date: 2022/8/29
 */
internal class TooltipInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        ToastConfig.context = context
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf()
    }
}