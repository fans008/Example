package com.common.ducis.download

import android.net.Uri

/**
 * @ClassName: DownloadInfo
 * @Description:
 * @Author: Fan TaoTao
 * @Date: 2020/9/15
 */
data class DownloadInfo(var url:String) {

    companion object {
        const val TOTAL_ERROR = -1
    }

    var total: Long = 0
    var progress: Long = 0
    var fileName: String = ""
    var fileUri: Uri? = null
    var isSuccess = false
}