package com.common.ducis.download

import io.reactivex.Observer
import io.reactivex.disposables.Disposable

/**
 * @ClassName: DownLoadObserver
 * @Description:
 * @Author: Fan TaoTao
 * @Date: 2020/9/15
 */
abstract class DownLoadObserver : Observer<DownloadInfo> {

    protected var d //可以用于取消注册的监听者
            : Disposable? = null
    var downloadInfo: DownloadInfo? = null
    override fun onSubscribe(d: Disposable) {
        this.d = d
    }

    override fun onNext(t: DownloadInfo) {
        this.downloadInfo = downloadInfo
    }

    override fun onError(e: Throwable) {
        e.printStackTrace()
    }
}