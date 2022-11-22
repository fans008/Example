package com.common.ducis.exception

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import com.common.ducis.commonutil.MobileInfoUtils
import com.common.ducis.commonutil.log.MyLog
import java.io.*
import java.util.*

@Suppress("DEPRECATION")
class CrashCatchHandler private constructor() : Thread.UncaughtExceptionHandler {

    /*** 全局上下文  */
    private var mContext: Context? = null
    /*** 系统默认的UncaughtException处理类  */
    private var mDefHandler: Thread.UncaughtExceptionHandler? = null
    /*** 使用Properties来保存设备的信息和错误堆栈信息  */
    private val mProperties = java.util.Properties()
    /*** 存放路径  */
    private var rootPath: String? = null

    /**
     * 初始化
     *
     * @param cxt
     */
    fun init(cxt: Context) {
        this.mContext = cxt
        // 获取系统默认的UncaughtException处理器
        mDefHandler = Thread.getDefaultUncaughtExceptionHandler()
        // 设置当前CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this)
        // 日志存放路径
        rootPath = getRootPath() + PATH

    }

    private fun getRootPath(): String {
        return if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState() || !Environment.isExternalStorageRemovable()) {
            // 外部内存可用
            mContext!!.externalCacheDir!!.absolutePath
        } else {
            // 外部内存不可用(需要Root)
            mContext!!.cacheDir.path
        }
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    override fun uncaughtException(thread: Thread, ex: Throwable) {
        if (!handleException(ex) && mDefHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefHandler!!.uncaughtException(thread, ex)
        } else {
            try {
                Thread.sleep(TIME.toLong())
            } catch (e: InterruptedException) {
                if (DEBUG) {
                    Log.e(TAG, "Error :", e)
                }
            }
            // 杀死进程
            android.os.Process.killProcess(android.os.Process.myPid())
            System.exit(1)
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param e
     * @return 如果处理了该异常信息, 返回true;否则返回false.
     */
    private fun handleException(e: Throwable?): Boolean {
        if (e == null) {
            if (DEBUG) {
                Log.w(TAG, "handleException-ex == null")
            }
            return false
        }
        val msg = e.localizedMessage ?: return false
        MyLog.d("异常信息","localizedMessage:"+msg)
        MyLog.d("异常信息","message:"+e.cause)
        e.stackTrace.forEach {
            MyLog.d("异常信息","className:"+it.className)
            MyLog.d("异常信息","fileName:"+it.fileName)
            MyLog.d("异常信息","isNativeMethod:"+it.isNativeMethod)
            MyLog.d("异常信息","lineNumber:"+it.lineNumber)
            MyLog.d("异常信息","methodName:"+it.methodName)
        }
        // toast显示提醒
        toastShowMsg("The program is abnormal，about to exit:\r\n", msg)
        // 收集设备参数信息
        collectPhoneInfo()
        // 保存日志文件
        dumpExceptionToSDCard(e)
        // 发送错误报告到服务器
//        if (dumpExceptionToSDCard != null) {
//            uploadCrashReportsToServer(dumpExceptionToSDCard)
//        }
        return false
    }

    /**
     * 把错误报告发送给服务器,包含新产生的和以前没发送的.
     */
    fun uploadCrashReportsToServer(hostUrl:String,postUrl:String,exceptionInfo:ExceptionBean) {
//        var body = MNetWorkUtils.getRequestBody(exceptionInfo)
//
//        RetrofitUtils.initRetrofit(hostUrl, mContext!!).create(AppServerApi::class.java)
//            .logException(postUrl, body)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe(object : MBaseObserver<Int>() {
//                override fun onSuccess(value: Int) {
//                    MyLog.d("请求成功", String.format("logException：%s", value.toString()))
//                }
//
//                override fun onFailure(e: String) {
//                    MyLog.e("请求失败", String.format("logException：%s", e))
//                }
//
//                /**
//                 * 登录失效错误
//                 */
//                override fun onFailureLogout(value: String) {
//                }
//            })
        
//        val dir = File(rootPath)
//        if (dir.exists() && dir.isDirectory) {
//            val files = dir.listFiles()
//            if (files != null && files.size != 0) {
//                for (file in files) {
//                    if (file.exists() && file.isFile) {
//                        postReport(file)
//                    }
//                }
//            }
//        }
    }

    /**
     * 异步发送异常报告到服务器
     *
     * @param file
     */
    private fun postReport(file: File) {
        // TODO 异步发送异常报告到服务器
        Thread(Runnable {
            file.delete()// 发送之后删除
        }).start()
    }

    /**
     * 收集设备信息
     */
    private fun collectPhoneInfo() {
        val pm = mContext!!.packageManager
        try {
            val pi = pm.getPackageInfo(mContext!!.packageName,
                    PackageManager.GET_ACTIVITIES)
            if (pi != null) {
                mProperties[VERSION_NAME] = if (pi.versionName == null) "null" else pi.versionName
                mProperties[VERSION_CODE] = pi.versionCode.toString()
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        // 获得某个类的所有申明的字段，即包括public、private和proteced，
        val fields = Build::class.java.declaredFields
        for (field in fields) {
            // 反射 ,获取私有的信息;类中的成员变量为private,故必须进行此操作
            field.isAccessible = true
            try {
                mProperties[field.name] = field.get(null).toString()
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }

        }
    }

    /**
     * 导出异常信息到SD卡中
     *
     * @param e
     * @return 返回文件名称
     */
    private fun dumpExceptionToSDCard(e: Throwable): String? {
        /* 获得错误信息字符串 */
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        e.printStackTrace(pw)
        val result = sw.toString()

        var exceptionInfo = ExceptionBean()
//        exceptionInfo.clientId = MUserData.getAccountNumber(mContext!!)
        exceptionInfo.deviceName = MobileInfoUtils.getDevice()
        exceptionInfo.imei = MobileInfoUtils.getUUID()
        exceptionInfo.manufacturer = MobileInfoUtils.getDeviceManuFacturer()
        exceptionInfo.systemVersion = MobileInfoUtils.getSystemVersion()
        exceptionInfo.appVersionId = mProperties.getProperty(VERSION_CODE).toLong()
        exceptionInfo.time = Date(System.currentTimeMillis())
        exceptionInfo.exceptionName = result.split(":")[0]
        exceptionInfo.exceptionDetail = result
//
//        try {
//            val findAll = LitePal.findAll(ExceptionBean::class.java)
//            MyLog.d("异常汇总",findAll.size)
//            if (findAll.size > 0){
//                MyLog.d("异常汇总",findAll[0].toString())
//                findAll.forEach {
//                    // 发送错误报告到服务器
////                uploadCrashReportsToServer(it)
//                }
//            }
//        }catch (e: LitePalSupportException){
//            e.printStackTrace()
//        }
        mProperties[EXCEPTION] = result
        MyLog.e("异常信息", "result:$result")
        MyLog.e("异常信息","name:"+result.split(":")[0])
        try {
            sw.close()
        } catch (e1: IOException) {
            e1.printStackTrace()
        }

        pw.close()
        /* 写入文件 */
        // 日志生成时间
        val time = java.text.SimpleDateFormat(TIME_PATTERN, Locale.US)
                .format(java.util.Date())
        // 文件名称
        val crashFileName = StringBuilder()
        crashFileName.append("/").append(CRASH_REPORTER_PREFIX)
                .append(time).append(CRASH_REPORTER_EXTENSION)
        val cfn = crashFileName.toString()

        val dir = File(rootPath)
        if (!dir.exists()) {
            dir.mkdirs()
            if (dir.canWrite()) {
                return writtenToSDCard(cfn)
            } else {
                toastShowMsg("unable to write to log file", null)
            }
        } else if (dir.canWrite()) {
            return writtenToSDCard(cfn)
        } else {
            toastShowMsg("unable to write to log file", null)
        }
        return null
    }

    /**
     * 写入SD卡中
     *
     * @param fileName
     * @return 写入成功，返回文件名
     */
    fun writtenToSDCard(fileName: String): String? {
        val file = File(rootPath!! + fileName)
        try {
            file.createNewFile()
            if (file.exists() && file.canWrite()) {
                val fos = FileOutputStream(file, false)
                mProperties.store(fos, "")
                fos.flush()
                fos.close()

                // TODO 可以将文件名写入sharedPreferences中，方便下一次打开程序时操作日志
                /*SharedPreferences.Editor editor = mContext
                            .getSharedPreferences("log").edit();
                    editor.putString("lastCrashFileName", crashFileName);
                    editor.commit();*/
                return fileName
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }

    /**
     * 使用Toast显示异常信息
     *
     * @param describe
     * @param msg
     */
    private fun toastShowMsg(describe: String, msg: String?) {
        object : Thread() {
            override fun run() {
                val content = StringBuffer()
                content.append(describe)
                content.append("\r\n")
                if (msg != null)
                    content.append(msg)
                Looper.prepare()
                val toast = Toast.makeText(mContext, content.toString(), Toast.LENGTH_LONG)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
                Looper.loop()
            }
        }.start()
    }

    companion object {
        /**
         * 是否开启Log输出,在Debug状态下开启,
         * 在Release状态下关闭以提示程序性能
         */
        val DEBUG = true
        /*** Debug Log Tag  */
        val TAG = "CrashCatchHandler"
        private val VERSION_NAME = "VERSION_NAME"
        private val VERSION_CODE = "VERSION_CODE"
        private val EXCEPTION = "EXCEPTION"
        /*** 错误报告文件的扩展名  */
        private val CRASH_REPORTER_PREFIX = "crash"
        private val CRASH_REPORTER_EXTENSION = ".log"
        /*** 关闭崩溃程序倒计时  */
        private val TIME = 2000
        /*** 存放日志文件的文件夹名称  */
        private val PATH = "/log"
        /*** 日志名称时间模式  */
        private val TIME_PATTERN = "yyyy-MM-dd HH:mm:ss"
        /*** 单例饿汉模式  */
        val instance = CrashCatchHandler()
    }
}

