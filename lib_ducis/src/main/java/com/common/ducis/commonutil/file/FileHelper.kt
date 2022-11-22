package com.common.ducis.commonutil.file

import android.annotation.SuppressLint
import android.os.Environment
import java.io.*
import java.lang.Exception
import java.util.*

/**
 * 类功能描述：文件操作辅助类
 *
 */
object FileHelper {
    private var userID = "ducis"
    private val baseFilePath = Environment.getExternalStorageDirectory().toString() + "/filedownloader"

    /** 创建文件 */
    @JvmStatic
    fun newFile(f: File) {
        if (!f.exists()) {
            try {
                f.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }


    /**
     * 获取默认文件存放路径
     */
    var fileDefaultPath = baseFilePath + "/" + userID + "/FILETEMP"
        private set
    /**获取下载文件的临时路径 */
    /**下载文件的临时路径 */
    @JvmStatic
    var tempDirPath = baseFilePath + "/" + userID + "/TEMPDir"
        private set
    private val wrongChars = arrayOf(
        "/", "\\", "*", "?", "<", ">", "\"", "|"
    )

    /**创建目录
     * @param
     */
    fun newDirFile(f: File) {
        if (!f.exists()) {
            f.mkdirs()
        }
    }

    // 获取一个文件列表的里的总文件大小
    fun getSize(willupload: List<String?>): Double {
        return getSizeUnitByte(willupload).toDouble() / (1024 * 1024)
    }

    /**
     * 计算文件的大小，单位是字节
     * @param willupload
     * @return
     */
    fun getSizeUnitByte(willupload: List<String?>): Long {
        var allfilesize: Long = 0
        for (i in willupload.indices) {
            val newfile = File(willupload[i])
            if (newfile.exists() && newfile.isFile) {
                allfilesize = allfilesize + newfile.length()
            }
        }
        return allfilesize
    }

    /**
     * 复制单个文件
     * @param  oldPath  String  原文件路径  如：c:/fqf.txt
     * @param  newPath  String  复制后路径  如：f:/fqf.txt
     * @return  boolean
     */
    fun copyFile(oldPath: String?, newPath: String?): Boolean {
        var iscopy = false
        var inStream: InputStream? = null
        var fs: FileOutputStream? = null
        try {
            var byteread = 0
            val oldfile = File(oldPath)
            if (oldfile.exists()) {  //文件存在时
                inStream = FileInputStream(oldPath) //读入原文件
                fs = FileOutputStream(newPath)
                val buffer = ByteArray(1024)
                while (inStream.read(buffer).also { byteread = it } != -1) {
                    fs.write(buffer, 0, byteread)
                }
                iscopy = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                inStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            try {
                fs?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return iscopy
    }

    @JvmStatic
    fun setUserID(newUserID: String) {
        userID = newUserID
        fileDefaultPath = baseFilePath + "/" + userID + "/FILETEMP"
        tempDirPath = baseFilePath + "/" + userID + "/TEMPDir"
    }

    fun getUserID(): String {
        return userID
    }

    /**
     * 过滤附件ID中某些不能存在在文件名中的字符
     */
    @JvmStatic
    fun filterIDChars(attID: String?): String? {
        var attID = attID
        if (attID != null) {
            for (i in wrongChars.indices) {
                val c = wrongChars[i]
                if (attID!!.contains(c)) {
                    attID = attID.replace(c.toRegex(), "")
                }
            }
        }
        return attID
    }

    /**
     * 获取过滤ID后的文件名
     */
    fun getFilterFileName(flieName: String?): String? {
        if (flieName == null || "" == flieName) {
            return flieName
        }
        val isNeedFilter = flieName.startsWith("(")
        val index = flieName.indexOf(")")
        if (isNeedFilter && index != -1) {
            val startIndex = index + 1
            val endIndex = flieName.length
            if (startIndex < endIndex) {
                return flieName.substring(startIndex, endIndex)
            }
        }
        return flieName
    }

    /**
     * 文件分割方法
     * @param bean 分割的文件
     * @param cutSize 分割文件的大小
     * @return int 文件切割的个数
     */
//    @SuppressLint("NewApi")
//    fun getSplitFile(bean: UploadFileInfo, cutSize: Long): MutableMap<Int,String> {
//        val targetFile = File(bean.filePath)
//        require(targetFile.exists()){"文件不存在"}
//        require(targetFile.isFile){"文件不存在"}
//        //计算切割文件大小
//        val count: Int = if (targetFile.length() % cutSize == 0L) (targetFile.length() / cutSize).toInt() else (targetFile.length() / cutSize + 1).toInt()
//        var raf: RandomAccessFile? = null
//        //存放切片数据
//        val temps: MutableMap<Int,String> = mutableMapOf()
//        try {
//            //获取目标文件 预分配文件所占的空间 在磁盘中创建一个指定大小的文件   r 是只读
//            raf = RandomAccessFile(targetFile, "r")
//            val length = raf.length() //文件的总长度
//            var offSet = 0L //初始化偏移量
//            val lastSize = (length - cutSize * (count - 1)).toInt()
//            for (i in 0 until count - 1) { //最后一片单独处理
//                val bt = ByteArray(cutSize.toInt())
//                raf.seek(offSet)
//                raf.read(bt)
//                temps[i] = Base64.getEncoder().encodeToString(bt)
//                offSet = (i + 1) * cutSize.toLong()
//            }
//            if (length - offSet > 0) {
//                val bt = ByteArray(lastSize)
//                raf.seek(offSet)
//                raf.read(bt)
//                temps[count-1] = Base64.getEncoder().encodeToString(bt)
//            }
//        } catch (e: FileNotFoundException) {
//            e.printStackTrace()
//        } catch (e: IOException) {
//            e.printStackTrace()
//        } finally {
//            try {
//                raf!!.close()
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
//        }
//        return temps
//    }
}