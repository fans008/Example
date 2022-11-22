package com.common.ducis.download

import java.io.Closeable
import java.io.IOException

/**
 * @ClassName: IOUtilse
 * @Description:
 * @Author: Fan TaoTao
 * @Date: 2020/9/15
 */
object IOUtil {
    fun closeAll(vararg closeables: Closeable?) {
        if (closeables == null) {
            return
        }
        for (closeable in closeables) {
            if (closeable != null) {
                try {
                    closeable.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
}