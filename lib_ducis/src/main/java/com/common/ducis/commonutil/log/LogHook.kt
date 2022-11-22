package com.common.ducis.commonutil.log

/**
 * @ClassName: LogHook
 * @Description:拦截日志
 * @Author: Fan TaoTao
 * @Date: 2022/8/29
 */
interface LogHook {

    fun hook(info: LogInfo)

}