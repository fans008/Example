@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "DEPRECATION")

package com.common.ducis.file.download

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import com.common.ducis.commonutil.Md5Utils
import com.common.ducis.commonutil.log.MyLog
import com.common.ducis.commonutil.toast.toast
import com.common.ducis.component.network.bean.CommonVersionView
import com.common.ducis.component.permission.Acp
import com.common.ducis.component.permission.AcpListener
import com.common.ducis.component.permission.AcpOptions
import java.io.File
import java.math.BigDecimal

/**
 *@describe：
 *@author：ftt
 *@date：2019/11/28
 */
object DownLoadApk {
    //下载进度监听
    private var progressListener: DownLoadProgressListener? = null

    //apk文件uri
    internal lateinit var apkUri: Uri

    var downloadBean: DownloadInfo? = null

    //校驗md5
    var checkNum: String? = null

    fun downloadApk(
        mContext: Context,
        versionBean: CommonVersionView,
        checkNum: String?
    ) {
        DownLoadApk.checkNum = checkNum
        //默认更新
        Acp.getInstance(mContext).request(
            AcpOptions.Builder().setPermissions(
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).build(),
            object : AcpListener {
                override fun onGranted() {
                    //权限通过开始下载
                    MyLog.d("断点下载", "下载地址${versionBean?.toString()}")
                    startDown(mContext, url = versionBean.downloadUrl)
                }

                override fun onDenied(permissions: List<String>) {
                    toast(permissions.toString() + "权限拒绝")
                }
            })
    }

    fun startDown(context: Context, url: String?) {
        DownloadManager.getInstance().download(context, url, object : DownLoadObserver() {
            override fun onComplete() {
                //下载完成
                if (downloadInfo != null) {
                    downloadBean = downloadInfo
                    MyLog.d("断点下载", "下载详情${downloadInfo?.toString()}")
                    downloadInfo!!.fileUri?.let { apkUri = it }
                    if (downloadInfo!!.isSuccess || downloadInfo!!.total == downloadInfo!!.progress) {
                        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), downloadInfo!!.fileName)
                        if (checkMd5(file)) {
                            progressListener?.downloadFinish(apkUri)
                        } else {
                            file.delete()
                            startDown(context, url)
                        }
                    }
                }
            }

            override fun onNext(downloadInfo: DownloadInfo) {
                super.onNext(downloadInfo)
                downloadInfo?.let {
                    downloadBean = it
                    var tempProcess = it.progress.toDouble() / it.total.toDouble()
//                    MyLog.d("断点下载", "当前进度${tempProcess}")
                    if (it.isSuccess || tempProcess == 1.0) {
                        it.fileUri?.let { it -> apkUri = it }

                        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), it.fileName)
                        if (checkMd5(file)) {
                            progressListener?.downloadFinish(apkUri)
                        } else {
                            file.delete()
                            startDown(context, url)
                        }
                    } else {
                        progressListener?.showProgress(tempProcess.toBigDecimal().multiply(BigDecimal(100)).toInt())
                    }
                }

            }
        })
    }

    /**
     * 校驗MD5
     * @param file File
     * @return Boolean
     */
    fun checkMd5(file: File): Boolean {
        MyLog.e("版本更新", "文件hash：${Md5Utils.getFileMD5(file)}")
        if (checkNum.isNullOrEmpty()) {
            return true
        }
        return Md5Utils.getFileMD5(file).toUpperCase() == checkNum!!.toUpperCase()
    }

    /**
     * 开始安装
     */
    fun installApk(mContext: Context, apkUri: Uri) {
        val install = Intent(Intent.ACTION_VIEW)
        install.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//判读版本是否在7.0以上
            install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)//添加这一句表示对目标应用临时授权该Uri所代表的文件
            install.setDataAndType(apkUri, "application/vnd.android.package-archive")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //判读版本是否在8.0以上
                val hasInstallPermission = mContext.packageManager.canRequestPackageInstalls()
                if (!hasInstallPermission) {
                    //注意这个是8.0新API
                    var packageName = mContext.packageName
                    val packageURI = Uri.parse("package:$packageName")//设置包名，可直接跳转当前软件的设置页面
                    val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    mContext.startActivity(intent)
                }
            }
        } else {
            install.setDataAndType(apkUri, "application/vnd.android.package-archive")
        }
        mContext.startActivity(install)
    }

    fun setLoadLoadProgressListener(listener: DownLoadProgressListener) {
        progressListener = listener
    }

    interface DownLoadProgressListener {
        /**
         * 下载进度
         */
        fun showProgress(progress: Int) {}

        /**
         * 下载完成
         */
        fun downloadFinish(apkUri: Uri)
    }

}