@file:Suppress("DEPRECATION", "NAME_SHADOWING")

package com.common.ducis.commonutil

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.res.Resources
import android.os.Environment
import android.text.TextUtils
import android.util.TypedValue
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern


/**
 *@describe：
 *@author：ftt
 *@date：2019/6/5
 */
object CommonUtils {
//手机号开头集合
//166，
//176，177，178
//180，181，182，183，184，185，186，187，188，189
//145，147
//130，131，132，133，134，135，136，137，138，139
//150，151，152，153，155，156，157，158，159
//198，1992、匹配手机号的规则：[3578]是手机号第二位可能出现的数字
    /**
     * 校验手机号
     */
    fun isMobile(mobile: String): Boolean {
        if (mobile.isEmpty())
            return false
        val regExp =
            "^((13[0-9])|(14[0-9])|(15[0-9])|(16[0-9])|(17[0-9])|(18[0-9])|(19[0-9]))\\d{8}$"
        val pattern = Pattern.compile(regExp)
        val matcher = pattern.matcher(mobile)
        return matcher.matches()
    }

    /**
         * 密码规则：必须同时包含大小写字母及数字
     * @param str
     * @return
     */
    fun isContainAll(str: String): Boolean {
        var isDigit = false//定义一个boolean值，用来表示是否包含数字
        var isLowerCase = false//定义一个boolean值，用来表示是否包含字母
        var isUpperCase = false
        for (i in 0 until str.length) {
            if (Character.isDigit(str[i])) {   //用char包装类中的判断数字的方法判断每一个字符
                isDigit = true
            } else if (Character.isLowerCase(str[i])) {  //用char包装类中的判断字母的方法判断每一个字符
                isLowerCase = true
            } else if (Character.isUpperCase(str[i])) {
                isUpperCase = true
            }
        }
        val regex = "^[a-zA-Z0-9]+$"
        return isDigit && isLowerCase && isUpperCase && str.matches(regex.toRegex())

    }

    /**
     * 密码规则：必须同时字母及数字大于八位
     * @param str
     * @return
     */
    fun isContainAccount(str: String,length:Int = 8): Boolean {
        var isDigit = false//定义一个boolean值，用来表示是否包含数字
        var isLowerCase = false//定义一个boolean值，用来表示是否包含字母
        var isUpperCase = false
        for (i in 0 until str.length) {
            if (Character.isDigit(str[i])) {   //用char包装类中的判断数字的方法判断每一个字符
                isDigit = true
            } else if (Character.isLowerCase(str[i]) || Character.isUpperCase(str[i])) {  //用char包装类中的判断字母的方法判断每一个字符
                isLowerCase = true
            }
            else if (Character.isUpperCase(str[i])) {
                isUpperCase = true
            }
        }
        val regex = "^[a-zA-Z0-9]+$"
        return isDigit && (isUpperCase || isLowerCase) && str.trim().matches(regex.toRegex()) && str.trim().length >= length

    }

    /**
     * 判断某个界面是否在前台
     *
     * @param activity 要判断的Activity
     * @return 是否在前台显示
     */
    fun isForeground(activity: Activity): Boolean {
        return isForeground(activity, activity.javaClass.name)
    }

    /**
     * 判断某个界面是否在前台
     *
     * @param context   Context
     * @param className 界面的类名
     * @return 是否在前台显示
     */
    fun isForeground(context: Context?, className: String): Boolean {
        if (context == null || TextUtils.isEmpty(className))
            return false
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val list = am.getRunningTasks(1)
        if (list != null && list.size > 0) {
            val cpn = list[0].topActivity
            if (className == cpn!!.className)
                return true
        }
        return false
    }

    /**
     *
     * @param time  1541569323155
     * @param pattern yyyy-MM-dd HH:mm:ss
     * @return 2018-11-07 13:42:03
     */
    fun getDateToString(time: Long, pattern: String): String {
        val date = Date(time)
        val format = SimpleDateFormat(pattern)
        return format.format(date)
    }

    /**
     *
     * @param time  1541569323155
     * @param pattern yyyy-MM-dd HH:mm:ss
     * @return 2018-11-07 13:42:03
     */
    fun getTimeString(time: Long): String {
        val date = Date(time)
        val pattern = "yyyy-MM-dd HH:mm:ss"
        val format = SimpleDateFormat(pattern)
        return format.format(date)
    }

    fun getTimeStandard(time: Long): String {
        val date = Date(time)
        val pattern = "yyyy-MM-dd"
        val format = SimpleDateFormat(pattern)
        return format.format(date)
    }

    fun getTimeStandards(time: Long): String {
        val date = Date(time)
        val pattern = "yyyy年MM月dd日"
        val format = SimpleDateFormat(pattern)
        return format.format(date)
    }

    /**
     *
     * @param time  1541569323155
     * @param pattern yyyy-MM-dd HH:mm:ss
     * @return 2018-11-07 13:42:03
     */
    fun getOnlyTimeString(time: Long): String {
        val date = Date(time)
        val pattern = "HH:mm:ss"
        val format = SimpleDateFormat(pattern)
        return format.format(date)
    }

    fun getTimeToDates(time: Long): String {
        val date = Date(time)
        val pattern = "MM/dd/yyyy "
        val format = SimpleDateFormat(pattern)
        return format.format(date)
    }

    fun getTimeToDateHour(time: Long): String {
        val date = Date(time)
        val pattern = "HH:mm:ss MM/dd/yyyy "
        val format = SimpleDateFormat(pattern)
        return format.format(date)
    }

    fun getTimeToDate(time: Long): String {
        val date = Date(time)
        val pattern = "MM-dd"
        val format = SimpleDateFormat(pattern)
        return format.format(date)
    }

    fun getTimeToHour(time: Long): String {
        val date = Date(time)
        val pattern = "HH:mm"
        val format = SimpleDateFormat(pattern)
        return format.format(date)
    }

    /**
     *
     * @param time  yyyy-MM-dd HH:mm:ss
     * @return 2018-11-07 13:42:03
     */
    fun getDateToString(time: String): String {
        val substring = time.substring(0, time.indexOf(" "))
        return substring
    }

    /**
     * 将时间戳转化为指定格式的String
     *
     * @param timeStamp 时间戳
     * @param dtFormat  格式
     * @return
     */
    fun fmtTimeStampToStr(time: String, dtFormat: String): String {
        var timeStamp = time
        if (timeStamp.length == 10) {
            val tmp = java.lang.Long.parseLong(timeStamp) * 1000
            timeStamp = tmp.toString()
        }
        val date = Date(timeStamp)
        val sdf = SimpleDateFormat(dtFormat)
        return sdf.format(date)
    }

    /**
     * 根据手机的分辨率从 dip 的单位 转成为 px(像素)
     */
    fun dip2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    /**
     * 根据手机的分辨率从 dip 的单位 转成为 px(像素)
     */
    fun dip2px(dpValue: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dpValue,
            Resources.getSystem().displayMetrics
        ).toInt()
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    fun px2dip(context: Context, pxValue: Float): Float {
        val scale = context.resources.displayMetrics.density
        return pxValue / scale + 0.5f
    }

    /**
     * 根据手机的分辨率从 sp 的单位 转成为 px(像素)
     */
    fun spToPx(context: Context, spValue: Float): Float {
        val scale: Float = context.resources.displayMetrics.scaledDensity
        return spValue * scale
    }

    /**
     * 添加请求头
     */
    fun addHeaders(context: Context): MutableMap<String, String> {
        var headers = mutableMapOf<String, String>()
        headers.put(
            "APP-version",
            context.packageManager.getPackageInfo(context.packageName, 0).versionCode.toString()
        )
        //添加请求头-手机唯一标示
        if (MySharedPreferences.getString(context, "imei", MySharedPreferences.APP_DATA)!!.isNotEmpty()) {
            headers.put(
                "APP-imei",
                MySharedPreferences.getString(context, "imei", MySharedPreferences.APP_DATA)!!
            )
        }
        return headers
    }

    /**
     * 计算文件大小
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指 定精度，以后的数字四舍五入。
     *
     * @param v1    被除数
     * @param scale 表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    fun div(v1: Int, v2: Int, scale: Int): String {
        if (scale < 0) {
            throw IllegalArgumentException("The scale must be a positive integer or zero")
        }
        val b1 = BigDecimal(v1.toDouble().toString())
        val b2 = BigDecimal(v2.toDouble().toString())
        b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).toDouble()
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).toDouble().toString()
    }

    /**
     * 计算文件大小
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指 定精度，以后的数字四舍五入。
     *
     * @param v1    被除数
     * @param scale 表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    fun div(v1: Int, scale: Int): String {
        if (scale < 0) {
            throw IllegalArgumentException("The scale must be a positive integer or zero")
        }
        val b1 = BigDecimal(v1.toDouble().toString())
        val b2 = BigDecimal((1024 * 1024).toDouble().toString())
        b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).toDouble()
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).toDouble().toString() + "MB"
    }

    /**
     * 计算文件大小
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指 定精度，以后的数字四舍五入。
     *
     * @param v1    被除数
     * @param scale 表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    fun divForSize(v1: Long, v2: Long, scale: Int): Double {
        if (scale < 0) {
            throw IllegalArgumentException("The scale must be a positive integer or zero")
        }
        if (v2 == 0L) {
            return 0.0
        }
        val b1 = BigDecimal(v1.toDouble().toString())
        val b2 = BigDecimal(v2.toDouble().toString())
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).toDouble()
    }

    /**
     * 隐藏手机中间四位
     *
     * @param phone
     * @return
     */
    fun hideAddress(phone: String): String {
        return if (TextUtils.isEmpty(phone))
            ""
        else
            phone.substring(0, 3) + "*****" + phone.substring(8, phone.length)
    }

    /**
     * 隐藏字符串固定位数
     *
     * @param phone
     * @return
     */
    fun hideString(content: String, num: Int): String {
        var temp = ""
        if (TextUtils.isEmpty(content)) {
            return ""
        }
        if (content.length <= num) {
            return content
        }
        temp = content.substring(0, num / 2) + ".." + content.substring(content.length - num / 2, content.length)
        return temp
    }

    /**
     * 隐藏余额
     *
     * @param amount
     * @return
     */
    fun hideAmount(amount: String): String {
        var tempAmount = StringBuffer()
        repeat(amount.length) {
            tempAmount.append("*")
        }
        return tempAmount.toString()
    }

    /**
     * 隐藏手机中间四位
     *
     * @param phone
     * @return
     */
    fun splitPhone(phone: String): String {
        return if (TextUtils.isEmpty(phone))
            ""
        else
            phone.substring(0, 3) + " " + phone.substring(3, 7) + " " + phone.substring(7, phone.length)
    }

    //getApplicationContext().getPackageName()
//小心修改application定义中通过android:process的特殊情况,一般直接写字符串就够用了
    private const val PROCESS_NAME = "com.ducis.youtiao"

    /**
     * 根据Pid得到进程名
     */
    fun getAppNameByPID(context: Context, pid: Int): String {
        val manager =
            context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (processInfo in manager.runningAppProcesses) {
            if (processInfo.pid == pid) { //主进程的pid是否和当前的pid相同,若相同,则对应的包名就是app的包名
                return processInfo.processName
            }
        }
        return ""
    }

    /**
     * 将字符串转化为DATE
     * def      如果格式化失败返回null
     *
     * @param dtFormat 格式yyyy-MM-dd HH:mm:ss 或 yyyy-MM-dd或 yyyy-M-dd或 yyyy-M-d或
     * yyyy-MM-d或 yyyy-M-dd
     * @param
     * @return
     */
    fun fmtStrToDate(dtFormat: String?): Date? {
        var dtFormat: String = dtFormat ?: return null
        return try {
            if (dtFormat.length == 9 || dtFormat.length == 8 || dtFormat.length == 18 || dtFormat.length == 17) {
                val dateStr = dtFormat.split("-").toTypedArray()
                dtFormat =
                    dateStr[0] + (if (dateStr[1].length == 1) "-0" else "-") + dateStr[1] + (if (dateStr[2].length == 1 || dateStr[2].length == 10) "-0" else "-") + dateStr[2]
            }
            if (dtFormat.length == 16) {
                dtFormat = "${dtFormat}:00"
            }
            if (dtFormat.length == 21 && dtFormat.contains(".0")) {
                dtFormat = dtFormat.substring(0, 19)
            }
            if (dtFormat.length != 10 && dtFormat.length != 19) {
                return null
            }
            if (dtFormat.length == 10) dtFormat = "$dtFormat 00:00:00"
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            dateFormat.parse(dtFormat)
        } catch (e: java.lang.Exception) {
            null
        }
    }

    /**
     * 类型转化为map
     */
    fun anyToMap(any: Any): MutableMap<String, String> {
        var mutableMap = mutableMapOf<String, String>()
        var clazz = any.javaClass
        clazz.declaredFields.forEach {
            it.isAccessible = true
            mutableMap[it.name] = it.get(any) as String
        }
        return mutableMap
    }

    /**
     * 清除缓存
     * @param context
     */
    fun clearAllCache(context: Context) {
        deleteDir(context.cacheDir)
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            deleteDir(context.externalCacheDir)
        }
    }

    private fun deleteDir(dir: File?): Boolean {
        if (dir != null && dir.isDirectory) {
            val children = dir.list()
            for (i in children.indices) {
                val success = deleteDir(File(dir, children[i]))
                if (!success) {
                    return false
                }
            }
        }
        return dir!!.delete()
    }

    /**
     * 截取字符串str中指定字符 strStart、strEnd之间的字符串
     *
     * @param string
     * @param str1
     * @param str2
     * @return
     */
    fun subString(
        str: String,
        strStart: String,
        strEnd: String
    ): String? {

        /* 找出指定的2个字符在 该字符串里面的 位置 */
        val strStartIndex = str.indexOf(strStart)
        val strEndIndex = str.indexOf(strEnd)

        /* index 为负数 即表示该字符串中 没有该字符 */
        if (strStartIndex < 0) {
            return str
        }
        return if (strEndIndex < 0) {
            str
        } else str.substring(strStartIndex, strEndIndex).substring(strStart.length)
    }

    /**
     * 获取assets目录下的文件转换为字符串
     * @param context Context
     * @param assetName String
     * @return String
     */
    fun getAssetsToString(context: Context, assetName: String): String {
        var result = ""
        try {
            val builder = StringBuilder()
            val inputStringReader = InputStreamReader(context.assets.open(assetName), "UTF-8")
            val br = BufferedReader(inputStringReader)
            var line = br.readLine()
            while (line != null) {
                builder.append(line)
                line = br.readLine()
            }
            br.close()
            inputStringReader.close()
            result = builder.toString()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return result
    }

    fun getJsonParam(jsonString: String?): String? {
        val map = getMapForJson(jsonString)
        var paramString = ""
        map?.forEach {
            paramString += "${it.key}=${it.value}&"
        }
        if (paramString.isNotEmpty()) {
            paramString = paramString.substring(0, paramString.length - 1)
        }
        return paramString
    }

    fun getMapForJson(jsonString: String?): Map<String, String>? {
        val jsonObject: JSONObject
        try {
            jsonObject = JSONObject(jsonString)
            val keyIter = jsonObject.keys()
            var key: String
            var value: String
            val valueMap: MutableMap<String, String> = HashMap()
            while (keyIter.hasNext()) {
                key = keyIter.next() as String
                value = jsonObject[key] as String
                valueMap[key] = value
            }
            return valueMap
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 根据毫秒值获取天
     */
    fun getDayByTime(time: Long): String {
        return ((System.currentTimeMillis() - time) / (1000 * 60 * 60 * 24)).toString()
    }

    /**
     * 根据毫秒值获取小时
     */
    fun getHourByTime(time: Long): String {
        return ((System.currentTimeMillis() - time) % (1000 * 60 * 60 * 24) / (1000 * 60 * 60)).toString()
    }

    fun getDayByTimes(time: Long): String {
        return ((time) / (1000 * 60 * 60 * 24)).toString()
    }

    /**
     * 根据毫秒值获取小时
     */
    fun getHourByTimes(time: Long): String {
        return ((time) % (1000 * 60 * 60 * 24) / (1000 * 60 * 60)).toString()
    }
}