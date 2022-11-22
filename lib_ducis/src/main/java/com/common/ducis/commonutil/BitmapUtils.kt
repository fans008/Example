package com.common.ducis.commonutil

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.database.Cursor
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.WindowManager
import com.common.ducis.commonutil.CommonUtils.dip2px
import java.io.*
import java.net.URL
import java.util.*


/**
 * @describe：图片处理工具
 * @author：ftt
 * @date：2019/2/28
 */
object BitmapUtils {
    private val bitmap: Bitmap? = null

    private val SD_PATH = Environment.getExternalStorageDirectory().toString() + "/Photo_Open/"
    private val CACHE_PATH = Environment.getDownloadCacheDirectory().toString() + "/Head_Cache/"
    const val TAG = "BitmapUtils"
    fun getWindowDisplayMetrics(context: Context): DisplayMetrics {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        wm.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics
    }

    fun getStatusBarHeight(context: Context): Int {
        var result = 0
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = context.resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    /**
     * 循环压缩处理直到合适的大小
     *
     * @param bmp
     * @param needRecycle
     * @return
     */
    fun bmpToByteArray(bmp: Bitmap, needRecycle: Boolean): ByteArray {
        var i: Int
        var j: Int
        if (bmp.height > bmp.width) {
            i = bmp.width
            j = bmp.width
        } else {
            i = bmp.height
            j = bmp.height
        }
        val localBitmap = Bitmap.createBitmap(i, j, Bitmap.Config.RGB_565)
        val localCanvas = Canvas(localBitmap)
        while (true) {
            localCanvas.drawBitmap(bmp, Rect(0, 0, i, j), Rect(0, 0, i, j), null)
            if (needRecycle) bmp.recycle()
            val localByteArrayOutputStream = ByteArrayOutputStream()
            localBitmap.compress(
                Bitmap.CompressFormat.JPEG, 100,
                localByteArrayOutputStream
            )
            localBitmap.recycle()
            val arrayOfByte = localByteArrayOutputStream.toByteArray()
            try {
                localByteArrayOutputStream.close()
                return arrayOfByte
            } catch (e: Exception) {
                //F.out(e);
            }
            i = bmp.height
            j = bmp.height
        }
    }

    fun saveBitmap(bm: Bitmap, picName: String): String {
        var path = ""
        try {
            if (!isFileExist("")) {
                val tempf = createSDDir("")
            }
            val f = File(CACHE_PATH, "$picName.jpg")
            if (f.exists()) {
                f.delete()
            }
            path = f.absolutePath
            val out = FileOutputStream(f)
            bm.compress(Bitmap.CompressFormat.JPEG, 90, out)
            out.flush()
            out.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return path
    }

    /**
     * 保存图片路径
     *
     * @param bm
     * @param picName
     * @return
     */
    fun saveBitmapToCache(bm: Bitmap, picName: String, dirName: String): String {
        var path = ""
        try {
            val dir = File(dirName)
            if (!dir.exists()) {
                dir.mkdirs()
            }
            val f = File(dirName, "$picName.png")
            if (f.exists()) {
                f.delete()
            }
            path = f.absolutePath
            val out = FileOutputStream(f)
            bm.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
            out.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return path
    }

    /**
     * 保存文件，文件名为当前日期
     */
    fun saveBitmap(context: Context, bitmap: Bitmap, bitName: String): Boolean {
        val fileName: String
        val file: File
        val brand = Build.BRAND
        fileName = if (brand == "xiaomi") { // 小米手机brand.equals("xiaomi")
            Environment.getExternalStorageDirectory().path + "/DCIM/Camera/" + bitName
        } else if (brand.equals("Huawei", ignoreCase = true)) {
            Environment.getExternalStorageDirectory().path + "/DCIM/Camera/" + bitName
        } else { // Meizu 、Oppo
            Environment.getExternalStorageDirectory().path + "/DCIM/" + bitName
        }
        file = if (Build.VERSION.SDK_INT >= 29) {
            saveSignImage(context, bitName, bitmap)
            return true
        } else {
            Log.v("saveBitmap brand", "" + brand)
            File(fileName)
        }
        if (file.exists()) {
            file.delete()
        }
        val out: FileOutputStream
        try {
            out = FileOutputStream(file)
            // 格式为 JPEG，照相机拍出的图片为JPEG格式的，PNG格式的不能显示在相册中
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)) {
                out.flush()
                out.close()
                // 插入图库
                if (Build.VERSION.SDK_INT >= 29) {
                    val values = ContentValues()
                    values.put(MediaStore.Images.Media.DATA, file.absolutePath)
                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                } else {
                    MediaStore.Images.Media.insertImage(context.contentResolver, file.absolutePath, bitName, null)
                }
            }
        } catch (e: FileNotFoundException) {
            Log.e("FileNotFoundException", "FileNotFoundException:" + e.message.toString())
            e.printStackTrace()
            return false
        } catch (e: IOException) {
            Log.e("IOException", "IOException:" + e.message.toString())
            e.printStackTrace()
            return false
        } catch (e: Exception) {
            Log.e("IOException", "IOException:" + e.message.toString())
            e.printStackTrace()
            return false
        }
        // 发送广播，通知刷新图库的显示
        context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://$fileName")))
        return true
    }

    /**
     * 将文件保存到公共的媒体文件夹
     *
     * @param context
     * @param fileName
     * @param bitmap
     */
    @JvmStatic
    fun saveSignImage(context: Context, fileName: String?, bitmap: Bitmap) {
        try {
            //设置保存参数到ContentValues中
            val contentValues = ContentValues()
            //设置文件名
            contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            //兼容Android Q和以下版本
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                //android Q中不再使用DATA字段，而用RELATIVE_PATH代替
                //RELATIVE_PATH是相对路径不是绝对路径
                //DCIM是系统文件夹，关于系统文件夹可以到系统自带的文件管理器中查看，不可以写没存在的名字
                contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/")
                //contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "Music/signImage");
            } else {
                contentValues.put(MediaStore.Images.Media.DATA, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path)
            }
            //设置文件类型
            contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/JPEG")
            //执行insert操作，向系统文件夹中添加文件
            //EXTERNAL_CONTENT_URI代表外部存储器，该值不变
            val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            if (uri != null) {
                //若生成了uri，则表示该文件添加成功
                //使用流将内容写入该uri中即可
                val outputStream = context.contentResolver.openOutputStream(uri)
                if (outputStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                    outputStream.flush()
                    outputStream.close()
                }
            }
        } catch (e: Exception) {
        }
    }

    @Throws(IOException::class)
    fun createSDDir(dirName: String): File {
        val dir = File(SD_PATH + dirName)
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            println("createSDDir:" + dir.absolutePath)
            println("createSDDir:" + dir.mkdir())
        }
        return dir
    }

    @Throws(IOException::class)
    fun createCacheDir(dirName: String): File {
        val dir = File(dirName)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    fun isFileExist(fileName: String): Boolean {
        val file = File(SD_PATH + fileName)
        file.isFile
        return file.exists()
    }

    /**
     * 把网络资源图片转化成bitmap
     *
     * @param url 网络资源图片
     * @return Bitmap
     */
    fun GetLocalOrNetBitmap(url: String?): Bitmap? {
        var bitmap: Bitmap? = null
        var `in`: InputStream? = null
        var out: BufferedOutputStream? = null
        return try {
            `in` = BufferedInputStream(URL(url).openStream(), 1024)
            val dataStream = ByteArrayOutputStream()
            out = BufferedOutputStream(dataStream, 1024)
            copy(`in`, out)
            out.flush()
            var data = dataStream.toByteArray()
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
            data = null
            bitmap
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    @Throws(IOException::class)
    private fun copy(`in`: InputStream, out: OutputStream) {
        val b = ByteArray(1024)
        var read: Int
        while (`in`.read(b).also { read = it } != -1) {
            out.write(b, 0, read)
        }
    }

    /**
     * 复制文件
     *
     * @param source 输入文件
     * @param target 输出文件
     */
    fun copy(source: File?, target: File?) {
        var fileInputStream: FileInputStream? = null
        var fileOutputStream: FileOutputStream? = null
        try {
            fileInputStream = FileInputStream(source)
            fileOutputStream = FileOutputStream(target)
            val buffer = ByteArray(1024)
            while (fileInputStream.read(buffer) > 0) {
                fileOutputStream.write(buffer)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        } finally {
            try {
                fileInputStream?.close()
                fileOutputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun getDataColumn(
        context: Context, uri: Uri?, selection: String?,
        selectionArgs: Array<String?>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(
            column
        )
        try {
            cursor = context.contentResolver.query(
                uri!!, projection, selection, selectionArgs,
                null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val column_index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(column_index)
            }
        } catch (ex: IllegalArgumentException) {
            Log.i(TAG, String.format(Locale.getDefault(), "getDataColumn: _data - [%s]", ex.message))
        } finally {
            cursor?.close()
        }
        return null
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     * @author paulburke
     */
    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     * @author paulburke
     */
    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     * @author paulburke
     */
    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    /**
     * 生成图片
     *
     * @param view   View
     * @param width  Int
     * @param height Int
     * @return Bitmap?
     */
    fun generateImageFromView(view: View, width: Int, height: Int, topBarHeight: Int = 0): Bitmap {
        view.isDrawingCacheEnabled = true
        view.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH
        view.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY))
        view.layout(0, topBarHeight, width, height)
        val image = Bitmap.createBitmap(view.drawingCache)
        view.destroyDrawingCache()
        return image
    }

    /**
     * 生成图片
     *
     * @param view   View
     * @param width  Int
     * @param height Int
     * @return Bitmap?
     */
    fun generateImageAllView(view: View, width: Int, height: Int): Bitmap {
        view.isDrawingCacheEnabled = true
        view.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_LOW
        view.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.AT_MOST), View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.AT_MOST))
        view.layout(0, 0, width, height)
        val image = Bitmap.createBitmap(view.drawingCache)
        view.destroyDrawingCache()
        return image
    }

    fun changeBitmapSize(bitmap: Bitmap): Bitmap {
        var bitmap = bitmap
        val width = bitmap.width
        val height = bitmap.height
        val newWidth = dip2px(320f)
        val newHeight = dip2px(320f)
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height
        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleHeight)
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
        bitmap.width
        bitmap.height
        return bitmap
    }

    fun bitmapToBytes(bitmap: Bitmap): ByteArray {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        return baos.toByteArray()
    }

    fun decodeBitmapFromResource(resources: Resources?, resId: Int, width: Int, height: Int): Bitmap {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeResource(resources, resId, options)
        //获取采样率
        options.inSampleSize = calculateInSampleSize(options, width, height)
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeResource(resources, resId, options)
    }

    fun resizeBitmap(bitmap: Bitmap, w: Int, h: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val scaleWidth = w.toFloat() / width
        val scaleHeight = h.toFloat() / height
        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleHeight)
        return Bitmap.createBitmap(
            bitmap, 0, 0, width,
            height, matrix, true
        )
    }

    /*
 * 计算采样率
 */
    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2
            while (halfHeight / inSampleSize > reqHeight && halfWidth / inSampleSize > reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    /**
     * 裁剪
     *
     * @param bitmap 原图
     * @return 裁剪后的图像
     */
    fun cropBitmap(bitmap: Bitmap, hRatioW: Float): Bitmap {
        val w = bitmap.width // 得到图片的宽，高
        val h = bitmap.height
        return Bitmap.createBitmap(bitmap, 0, 0, w, (w * hRatioW).toInt(), null, false)
    }

    /**
     * 按比例缩放图片
     *
     * @param origin 原图
     * @param ratio  比例
     * @return 新的bitmap
     */
    fun scaleBitmap(origin: Bitmap?, ratio: Float): Bitmap? {
        if (origin == null) {
            return null
        }
        val width = origin.width
        val height = origin.height
        val matrix = Matrix()
        matrix.preScale(ratio, ratio)
        return Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false)
    }

    /*
 * 质量压缩法：将图片文件压缩,压缩是耗时操作
 */
    fun compressFile(compressFileBean: CompressFileBean, compressFileCallback: CompressFileCallback) {
        CompressFileThread(compressFileBean, compressFileCallback).start()
    }

    /*
 * 由file转bitmap
 */
    fun decodeBitmapFromFilePath(path: String?, reqWidth: Int, reqHeight: Int): Bitmap {

        // First decode with inJustDecodeBounds=true to check dimensions
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true //如此，无法decode bitmap
        BitmapFactory.decodeFile(path, options)

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false //如此，方可decode bitmap
        return BitmapFactory.decodeFile(path, options)
    }

    /**
     * 根据路径 创建文件
     *
     * @param pathFile
     * @return
     * @throws IOException
     */
    @Throws(IOException::class)
    fun createFile(pathFile: String?): File {
        val fileDir = File(pathFile!!.substring(0, pathFile.lastIndexOf(File.separator)))
        val file = File(pathFile)
        if (!fileDir.exists()) fileDir.mkdirs()
        if (!file.exists()) file.createNewFile()
        return file
    }

    /**
     * 将图片按照某个角度进行旋转
     *
     * @param bm     需要旋转的图片
     * @param degree 旋转角度
     * @return 旋转后的图片
     */
    fun rotateBitmapByDegree(bm: Bitmap, degree: Int): Bitmap? {
        var returnBm: Bitmap? = null


        // 根据旋转角度，生成旋转矩阵
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        try {

            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.width, bm.height, matrix, true)
        } catch (e: OutOfMemoryError) {
        }
        if (returnBm == null) {
            returnBm = bm
        }
        if (bm != returnBm) {
            bm.recycle()
        }
        return returnBm
    }

    /**
     * 压缩bitmap,会被压缩到指定宽高
     */
    fun compressBitmap(bitmap: Bitmap?, reqWidth: Int, reqHeight: Int): Bitmap {
        return Bitmap.createScaledBitmap(bitmap!!, reqWidth, reqHeight, true)
    }

    class CompressFileThread(private val compressFileBean: CompressFileBean, private val compressFileCallback: CompressFileCallback) : Thread() {
        private val handler_deliver: Handler = Handler(Looper.getMainLooper())
        override fun run() {
            super.run()
            val bitmap = decodeBitmapFromFilePath(compressFileBean.pathSource, compressFileBean.reqWidth, compressFileBean.reqHeight)
            val byteArrayOutputStream = ByteArrayOutputStream()
            var quality = 80
            //压缩格式选取JPEG就行了，quality，压缩精度尽量不要低于50，否则影响清晰度
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)
            while (byteArrayOutputStream.toByteArray().size / 1024 > compressFileBean.kb_max && quality > compressFileBean.quality_max) {
                // 循环判断如果压缩后图片是否大于kb_max kb,大于继续压缩,
                byteArrayOutputStream.reset()
                quality -= 10
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)
            }
            try {
                val fileCompressed = createFile(compressFileBean.pathCompressed)
                val fileOutputStream = FileOutputStream(fileCompressed)
                fileOutputStream.write(byteArrayOutputStream.toByteArray()) //写入目标文件
                fileOutputStream.flush()
                fileOutputStream.close()
                byteArrayOutputStream.close()
                if (fileCompressed != null && fileCompressed.length() > 0) runOnUiThread { //压缩成功
                    compressFileCallback.onCompressFileFinished(fileCompressed, bitmap)
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                runOnUiThread { //压缩失败
                    compressFileCallback.onCompressFileFailed("压缩图片文件失败" + e.message)
                }
            }
        }

        private fun runOnUiThread(run: Runnable) {
            handler_deliver.post(run)
        }
    }

    class CompressFileBean private constructor(builder: Builder) {
        //原图文件路径
        val pathSource: String?

        //压缩后的图片文件路径
        val pathCompressed: String?
        var kb_max = 1000 //压缩到多少KB，不能精确，只能<=kb_max
        var quality_max = 80 //压缩精度，尽量>=50
        var reqWidth = 1000 //期望的图片宽度
        var reqHeight = 1000 //期望的图片高度

        class Builder {
            var fileSource //原图文件路径
                    : String? = null
                private set
            var fileCompressed //压缩后的图片文件路径
                    : String? = null
                private set
            var kb_max = 1000 //压缩到多少KB，不能精确，只能<=kb_max
                private set
            var quality_max = 80 //压缩精度，尽量>=50
                private set
            var reqWidth = 1000 //期望的图片宽度
                private set
            var reqHeight = 1000 //期望的图片高度
                private set

            fun setFileSource(pathSource: String?): Builder {
                fileSource = pathSource
                return this
            }

            fun setFileCompressed(pathCompressed: String?): Builder {
                fileCompressed = pathCompressed
                return this
            }

            fun setKb_max(kb_max: Int): Builder {
                this.kb_max = kb_max
                return this
            }

            fun setQuality_max(quality_max: Int): Builder {
                this.quality_max = quality_max
                return this
            }

            fun setReqWidth(reqWidth: Int): Builder {
                this.reqWidth = reqWidth
                return this
            }

            fun setReqHeight(reqHeight: Int): Builder {
                this.reqHeight = reqHeight
                return this
            }

            fun build(): CompressFileBean {
                return CompressFileBean(this)
            }
        }

        init {
            pathSource = builder.fileSource
            pathCompressed = builder.fileCompressed
            kb_max = builder.kb_max
            quality_max = builder.quality_max
            reqWidth = builder.reqWidth
            reqHeight = builder.reqHeight
        }
    }

    interface CompressFileCallback {
        //图片压缩成功
        fun onCompressFileFinished(file: File?, bitmap: Bitmap?)

        //图片压缩失败
        fun onCompressFileFailed(errorMsg: String?)
    }
}