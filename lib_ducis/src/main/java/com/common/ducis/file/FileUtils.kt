package com.common.ducis.file

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.TextUtils
import androidx.core.content.FileProvider
import com.common.ducis.DucisLibrary
import com.common.ducis.file.OutsideFileHelper.fileDefaultPath
import java.io.*
import java.lang.Exception
import java.math.BigInteger
import java.security.MessageDigest
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

object FileUtils {
    private val baseFilePath = Environment.getExternalStorageDirectory().toString() + "/fileDownloader"
    const val DOCUMENTS_DIR = "documents"
    const val SIZETYPE_B = 1 //获取文件大小单位为B的double值
    const val SIZETYPE_KB = 2 //获取文件大小单位为KB的double值
    const val SIZETYPE_MB = 3 //获取文件大小单位为MB的double值
    const val SIZETYPE_GB = 4 //获取文件大小单位为GB的double值
    var APP_DIR = fileDefaultPath
    var RECORD_DIR = "$APP_DIR/record/"
    var RECORD_DOWNLOAD_DIR = "$APP_DIR/record/download/"
    var VIDEO_DOWNLOAD_DIR = "$APP_DIR/video/download/"
    var IMAGE_BASE_DIR = "$APP_DIR/image/"
    var IMAGE_DOWNLOAD_DIR = IMAGE_BASE_DIR + "download/"
    var MEDIA_DIR = "$APP_DIR/media"
    var FILE_DOWNLOAD_DIR = "$APP_DIR/file/download/"
    var CRASH_LOG_DIR = "$APP_DIR/crash/"
    fun initPath() {
        var f = File(MEDIA_DIR)
        if (!f.exists()) {
            f.mkdirs()
        }
        f = File(RECORD_DIR)
        if (!f.exists()) {
            f.mkdirs()
        }
        f = File(RECORD_DOWNLOAD_DIR)
        if (!f.exists()) {
            f.mkdirs()
        }
        f = File(VIDEO_DOWNLOAD_DIR)
        if (!f.exists()) {
            f.mkdirs()
        }
        f = File(IMAGE_DOWNLOAD_DIR)
        if (!f.exists()) {
            f.mkdirs()
        }
        f = File(FILE_DOWNLOAD_DIR)
        if (!f.exists()) {
            f.mkdirs()
        }
        f = File(CRASH_LOG_DIR)
        if (!f.exists()) {
            f.mkdirs()
        }
    }

    fun saveBitmap(dir: String?, b: Bitmap): String {
        val jpegName = MEDIA_DIR + File.separator + "picture_" + SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date()) + ".jpg"
        return try {
            val fout = FileOutputStream(jpegName)
            val bos = BufferedOutputStream(fout)
            b.compress(Bitmap.CompressFormat.JPEG, 100, bos)
            bos.flush()
            bos.close()
            jpegName
        } catch (e: IOException) {
            e.printStackTrace()
            ""
        }
    }

    fun deleteFile(url: String?): Boolean {
        var result = false
        val file = File(url)
        if (file.exists()) {
            result = file.delete()
        }
        return result
    }

    val isExternalStorageWritable: Boolean
        get() {
            val state = Environment.getExternalStorageState()
            return Environment.MEDIA_MOUNTED == state
        }

    fun getPathFromUri(uri: Uri): String {
        var path: String? = ""
        try {
            val sdkVersion = Build.VERSION.SDK_INT
            path = if (sdkVersion >= 19) {
                getPath(DucisLibrary.appContext, uri)
            } else {
                getRealFilePath(uri)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (path == null) {
            path = ""
        }
        return path
    }

    fun getRealFilePath(uri: Uri?): String? {
        if (null == uri) {
            return null
        }
        val scheme = uri.scheme
        var data: String? = null
        if (scheme == null) {
            data = uri.path
        } else if (ContentResolver.SCHEME_FILE == scheme) {
            data = uri.path
        } else if (ContentResolver.SCHEME_CONTENT == scheme) {
            val cursor = DucisLibrary.appContext.contentResolver.query(uri, arrayOf(MediaStore.Images.ImageColumns.DATA), null, null, null)
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    val index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                    if (index > -1) {
                        data = cursor.getString(index)
                    }
                }
                cursor.close()
            }
        }
        return data
    }

    fun getUriFromPath(path: String?): Uri? {
        try {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                FileProvider.getUriForFile(DucisLibrary.appContext, DucisLibrary.appContext.packageName + "fileprovider", File(path))
            } else {
                Uri.fromFile(File(path))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun checkAudioExist(fileName: String): Boolean {
        val file = File(RECORD_DOWNLOAD_DIR)
        if (!file.exists()) return false
        val files = file.list()
        for (i in files.indices) {
            if (files[i] == fileName) return true
        }
        return false
    }

    /**
     * 专为Android4.4以上设计的从Uri获取文件路径
     */
    fun getPath(context: Context, uri: Uri): String? {
        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                return if ("primary".equals(type, ignoreCase = true)) {
                    Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                } else {
                    getPathByCopyFile(context, uri)
                }
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                if (id.startsWith("raw:")) {
                    return id.replaceFirst("raw:".toRegex(), "")
                }
                val contentUriPrefixesToTry = arrayOf(
                    "content://downloads/public_downloads",
                    "content://downloads/my_downloads",
                    "content://downloads/all_downloads"
                )
                for (contentUriPrefix in contentUriPrefixesToTry) {
                    val contentUri = ContentUris.withAppendedId(Uri.parse(contentUriPrefix), id.toLong())
                    try {
                        val path = getDataColumn(context, contentUri, null, null)
                        if (path != null && Build.VERSION.SDK_INT < 29) {
                            return path
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                // 在某些android8+的手机上，无法获取路径，所以用拷贝的方式，获取新文件名，然后把文件发出去
                return getPathByCopyFile(context, uri)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])
                var path = getDataColumn(context, contentUri, selection, selectionArgs)
                if (TextUtils.isEmpty(path) || Build.VERSION.SDK_INT >= 29) {
                    path = getPathByCopyFile(context, uri)
                }
                return path
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            var path = getDataColumn(context, uri, null, null)
            if (TextUtils.isEmpty(path) || Build.VERSION.SDK_INT >= 29) {
                // 在某些华为android9+的手机上，无法获取路径，所以用拷贝的方式，获取新文件名，然后把文件发出去
                path = getPathByCopyFile(context, uri)
            }
            return path
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    private fun getPathByCopyFile(context: Context, uri: Uri): String? {
        val fileName = getFileName(context, uri)
        val cacheDir = getDocumentCacheDir(context)
        val file = generateFileName(fileName, cacheDir)
        var destinationPath: String? = null
        if (file != null) {
            destinationPath = file.absolutePath
            saveFileFromUri(context, uri, destinationPath)
        }
        return destinationPath
    }

    fun generateFileName(name: String?, directory: File?): File? {
        var name = name ?: return null
        var file = File(directory, name)
        if (file.exists()) {
            var fileName = name
            var extension = ""
            val dotIndex = name.lastIndexOf('.')
            if (dotIndex > 0) {
                fileName = name.substring(0, dotIndex)
                extension = name.substring(dotIndex)
            }
            var index = 0
            while (file.exists()) {
                index++
                name = "$fileName($index)$extension"
                file = File(directory, name)
            }
        }
        try {
            if (!file.createNewFile()) {
                return null
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
        return file
    }

    fun getFileName(context: Context, uri: Uri): String? {
        val mimeType = context.contentResolver.getType(uri)
        var filename: String? = null
        if (mimeType == null && context != null) {
            filename = getName(uri.toString())
        } else {
            val returnCursor = context.contentResolver.query(
                uri, null,
                null, null, null
            )
            if (returnCursor != null) {
                val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                returnCursor.moveToFirst()
                filename = returnCursor.getString(nameIndex)
                returnCursor.close()
            }
        }
        return filename
    }

    private fun getName(filename: String?): String? {
        if (filename == null) {
            return null
        }
        val index = filename.lastIndexOf('/')
        return filename.substring(index + 1)
    }

    fun getDocumentCacheDir(context: Context): File {
        val dir = File(context.cacheDir, DOCUMENTS_DIR)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    private fun saveFileFromUri(context: Context, uri: Uri, destinationPath: String?) {
        var `is`: InputStream? = null
        var bos: BufferedOutputStream? = null
        try {
            `is` = context.contentResolver.openInputStream(uri)
            bos = BufferedOutputStream(FileOutputStream(destinationPath, false))
            val buf = ByteArray(1024)
            `is`!!.read(buf)
            do {
                bos.write(buf)
            } while (`is`.read(buf) != -1)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                `is`?.close()
                bos?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    fun getDataColumn(
        context: Context, uri: Uri?, selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)
        try {
            cursor = context.contentResolver.query(
                uri!!, projection, selection, selectionArgs,
                null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val column_index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(column_index)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
        }
        return null
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    /**
     * 获取文件指定文件的指定单位的大小
     *
     * @param filePath 文件路径
     * @param sizeType 获取大小的类型1为B、2为KB、3为MB、4为GB
     * @return double值的大小
     */
    fun getFileOrFilesSize(filePath: String?, sizeType: Int): Double {
        val file = File(filePath)
        var blockSize: Long = 0
        try {
            blockSize = if (file.isDirectory) {
                getFileSizes(file)
            } else {
                getFileSize(file)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return FormetFileSize(blockSize, sizeType)
    }

    /**
     * 调用此方法自动计算指定文件或指定文件夹的大小
     *
     * @param filePath 文件路径
     * @return 计算好的带B、KB、MB、GB的字符串
     */
    fun getAutoFileOrFilesSize(filePath: String?): String {
        val file = File(filePath)
        var blockSize: Long = 0
        try {
            blockSize = if (file.isDirectory) {
                getFileSizes(file)
            } else {
                getFileSize(file)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return FormetFileSize(blockSize)
    }

    /**
     * 获取指定文件大小
     *
     * @param file
     * @return
     * @throws Exception
     */
    fun getFileSize(file: File): Long {
        var size: Long = 0
        try {
            if (file.exists()) {
                var fis: FileInputStream? = null
                fis = FileInputStream(file)
                size = fis.available().toLong()
            } else {
                file.createNewFile()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return size
    }

    /**
     * 获取指定文件夹
     *
     * @param f
     * @return
     */
    private fun getFileSizes(f: File): Long {
        var size: Long = 0
        val flist = f.listFiles()
        for (i in flist.indices) {
            size = if (flist[i].isDirectory) {
                size + getFileSizes(flist[i])
            } else {
                size + getFileSize(flist[i])
            }
        }
        return size
    }

    /**
     * 转换文件大小
     *
     * @param fileS
     * @return
     */
    fun FormetFileSize(fileS: Long): String {
        val df = DecimalFormat("#.00")
        var fileSizeString = ""
        val wrongSize = "0B"
        if (fileS == 0L) {
            return wrongSize
        }
        fileSizeString = if (fileS < 1024) {
            df.format(fileS.toDouble()) + "B"
        } else if (fileS < 1048576) {
            df.format(fileS.toDouble() / 1024) + "KB"
        } else if (fileS < 1073741824) {
            df.format(fileS.toDouble() / 1048576) + "MB"
        } else {
            df.format(fileS.toDouble() / 1073741824) + "GB"
        }
        return fileSizeString
    }

    /**
     * 转换文件大小,指定转换的类型
     *
     * @param fileS
     * @param sizeType
     * @return
     */
    private fun FormetFileSize(fileS: Long, sizeType: Int): Double {
        val df = DecimalFormat("#.00")
        var fileSizeLong = 0.0
        when (sizeType) {
            SIZETYPE_B -> fileSizeLong = java.lang.Double.valueOf(df.format(fileS.toDouble()))
            SIZETYPE_KB -> fileSizeLong = java.lang.Double.valueOf(df.format(fileS.toDouble() / 1024))
            SIZETYPE_MB -> fileSizeLong = java.lang.Double.valueOf(df.format(fileS.toDouble() / 1048576))
            SIZETYPE_GB -> fileSizeLong = java.lang.Double.valueOf(df.format(fileS.toDouble() / 1073741824))
            else -> {}
        }
        return fileSizeLong
    }

    fun reNameFile(file: File, fileName: String): String {
        val filePath = FILE_DOWNLOAD_DIR + fileName
        if (File(filePath).exists()) {
            val baseFile = File(FILE_DOWNLOAD_DIR)
            val fileFilter = FileFilter { pathname -> pathname.name.startsWith(fileName) }
            val files = baseFile.listFiles(fileFilter)
            val fileList = Arrays.asList(*files)
            Collections.sort(fileList) { o1, o2 -> o1.name.compareTo(o2.name) }
            val lastFile = fileList[0]
            val indexStr = lastFile.name.split(fileName.toRegex()).toTypedArray()
            var index = 0
            if (indexStr.size > 1) {
                indexStr[1].split("\\(".toRegex()).toTypedArray()[1].split("\\)".toRegex()).toTypedArray()[0].toInt()
                index++
            }
            val newName = "$fileName($index)"
            val dest = File(FILE_DOWNLOAD_DIR + newName)
            file.renameTo(dest)
            return newName
        } else {
            val dest = File(filePath)
            file.renameTo(dest)
        }
        return fileName
    }

    // 修复 android.webkit.MimeTypeMap 的 getFileExtensionFromUrl 方法不支持中文的问题
    fun getFileExtensionFromUrl(url: String): String {
        var url = url
        if (!TextUtils.isEmpty(url)) {
            val fragment = url.lastIndexOf('#')
            if (fragment > 0) {
                url = url.substring(0, fragment)
            }
            val query = url.lastIndexOf('?')
            if (query > 0) {
                url = url.substring(0, query)
            }
            val filenamePos = url.lastIndexOf('/')
            val filename = if (0 <= filenamePos) url.substring(filenamePos + 1) else url

            // if the filename contains special characters, we don't
            // consider it valid for our matching purposes:
            // 去掉正则表达式判断以添加中文支持
//          if (!filename.isEmpty() && Pattern.matches("[a-zA-Z_0-9\\.\\-\\(\\)\\%]+", filename))
            if (!filename.isEmpty()) {
                val dotPos = filename.lastIndexOf('.')
                if (0 <= dotPos) {
                    // 后缀转为小写
                    return filename.substring(dotPos + 1).lowercase(Locale.getDefault())
                }
            }
        }
        return ""
    }

    /**
     * 获取单个文件的MD5值！
     *
     * @param file
     * @return
     */
    fun getFileMD5(file: File): String? {
        if (!file.isFile) {
            return null
        }
        var digest: MessageDigest? = null
        var `in`: FileInputStream? = null
        val buffer = ByteArray(1024)
        var len: Int
        try {
            digest = MessageDigest.getInstance("MD5")
            `in` = FileInputStream(file)
            while (`in`.read(buffer, 0, 1024).also { len = it } != -1) {
                digest.update(buffer, 0, len)
            }
            `in`.close()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        val bigInt = BigInteger(1, digest.digest())
        return bigInt.toString(16)
    }
}