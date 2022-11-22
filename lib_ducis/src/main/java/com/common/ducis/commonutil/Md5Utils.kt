package com.common.ducis.commonutil

import java.io.*
import java.math.BigInteger
import java.nio.channels.FileChannel
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * @ClassName: Md5Utils
 * @Description:
 * @Author: Fan TaoTao
 * @Date: 2021/3/30
 */
object Md5Utils {

    private val HEX_DIGITS = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        'A', 'B', 'C', 'D', 'E', 'F')
    /**
     * 获取文件的MD5校验码
     *
     * @param filePath 文件
     * @return 文件的MD5校验码
     */
    @JvmStatic
    fun getFileMD5(filePath: String?): String {
        return getFileMD5(getFileByPath(filePath))
    }

    /**
     * 获取文件的MD5校验码
     *
     * @param file 文件
     * @return 文件的MD5校验码
     */
    @JvmStatic
    fun getFileMD5(file: File?): String {
        return encryptMD5File2String(file!!)
    }

    /**
     * 根据文件路径获取文件
     *
     * @param filePath 文件路径
     * @return 文件
     */
    @JvmStatic
    fun getFileByPath(filePath: String?): File? {
        return if (filePath.isNullOrEmpty()) null else File(filePath)
    }

    /**
     * MD5加密文件
     *
     * @param file 文件
     * @return 文件的16进制密文
     */
    @JvmStatic
    fun encryptMD5File2String(file: File): String {
        return if (encryptMD5File(file) != null) bytes2HexString(encryptMD5File(file)!!) else ""
    }

    /**
     * byteArr转hexString
     *
     * 例如：
     * bytes2HexString(new byte[] { 0, (byte) 0xa8 }) returns 00A8
     *
     * @param bytes byte数组
     * @return 16进制大写字符串
     */
    @JvmStatic
    fun bytes2HexString(bytes: ByteArray): String {
        val ret = CharArray(bytes.size shl 1)
        var i = 0
        var j = 0
        while (i < bytes.size) {
            ret[j++] = HEX_DIGITS[bytes[i].toInt() ushr 4 and 0x0f]
            ret[j++] = HEX_DIGITS[bytes[i].toInt() and 0x0f]
            i++
        }
        return String(ret)
    }

    /**
     * MD5加密文件
     *
     * @param file 文件
     * @return 文件的MD5校验码
     */
    @JvmStatic
    fun encryptMD5File(file: File): ByteArray? {
        var fis: FileInputStream? = null
        try {
            fis = FileInputStream(file)
            val channel = fis.channel
            val buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, file.length())
            val md = MessageDigest.getInstance("MD5")
            md.update(buffer)
            return md.digest()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            closeIO(fis)
        }
        return null
    }

    /**
     * 关闭IO
     *
     * @param closeables closeable
     */
    @JvmStatic
    fun closeIO(vararg closeables: Closeable?) {
        if (closeables == null) {
            return
        }
        try {
            for (closeable in closeables) {
                closeable?.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}