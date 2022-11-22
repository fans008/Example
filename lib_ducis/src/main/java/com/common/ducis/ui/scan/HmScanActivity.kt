package com.common.ducis.ui.scan

import android.Manifest
import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.animation.TranslateAnimation
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.common.ducis.DucisLibrary
import com.common.ducis.R
import com.common.ducis.commonutil.toast.toast
import com.common.ducis.ui.permission.Acp
import com.common.ducis.ui.permission.AcpListener
import com.common.ducis.ui.permission.AcpOptions
import com.gyf.immersionbar.ImmersionBar
import com.huawei.hms.hmsscankit.RemoteView
import com.huawei.hms.hmsscankit.ScanUtil
import com.huawei.hms.ml.scan.HmsScan
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions

/**
 * 扫一扫页面
 */

class HmScanActivity : Activity(), View.OnClickListener {

    companion object {
        private var remoteView: RemoteView? = null
        var mScreenWidth = 0
        var mScreenHeight = 0
        const val SCAN_FRAME_SIZE = 300
        const val DEVICE_PHOTO_REQUEST = 10001
        const val DEFAULT_CODE = 999
        private var animation: TranslateAnimation? = null

        fun startAction(context: Activity) {
            val intent = Intent(context, HmScanActivity::class.java)
            context.startActivity(intent)
        }
        fun startActionForResult(context: Activity,intentCode:Int) {
            val intent = Intent(context, HmScanActivity::class.java).apply {
                putExtra("intentCode",intentCode)
            }
            context.startActivityForResult(intent,intentCode)
        }
        fun startActionForResult(context: Fragment,intentCode:Int) {
            val intent = Intent(context.requireContext(), HmScanActivity::class.java).apply {
                putExtra("intentCode",intentCode)
            }
            context.startActivity(intent)
        }
    }

    private var intentCode: Int = DEFAULT_CODE
    private lateinit var imgBack: ImageView
    private lateinit var ivPhoto: ImageView
    private lateinit var scanQrLine: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hm_scan)
        ImmersionBar.with(this).statusBarDarkFont(DucisLibrary.isDarkFont).init()
        intentCode = intent.getIntExtra("intentCode",DEFAULT_CODE)
        imgBack = findViewById(R.id.img_back)
        ivPhoto = findViewById(R.id.iv_photo)
        scanQrLine = findViewById(R.id.scan_qr_line)
        imgBack.setOnClickListener(this)
        ivPhoto.setOnClickListener(this)
        val dm = resources.displayMetrics
        val density = dm.density
        mScreenWidth = dm.widthPixels
        mScreenHeight = dm.heightPixels
        var scanFrameSize = (SCAN_FRAME_SIZE * density)
        val rect = Rect()
        apply {
            rect.left = (mScreenWidth / 2 - scanFrameSize / 2).toInt()
            rect.right = (mScreenWidth / 2 + scanFrameSize / 2).toInt()
            rect.top = (mScreenHeight / 2 - scanFrameSize / 2).toInt()
            rect.bottom = (mScreenHeight / 2 + scanFrameSize / 2).toInt()
        }
        remoteView = RemoteView.Builder().setContext(this).setBoundingBox(rect).setFormat(HmsScan.ALL_SCAN_TYPE).build()
        remoteView?.onCreate(savedInstanceState)
        remoteView?.setOnResultCallback { result ->
            if (result != null && result.isNotEmpty() && result[0] != null && !TextUtils.isEmpty(result[0].getOriginalValue())) {
                setVibrator()
                val scanResult = result[0].getOriginalValue()
                if (scanResult != null) {
                    val intent = Intent()
                    intent.putExtra("data",scanResult)
                    setResult(intentCode,intent)
                } else {
                    toast("暂无识别")
                }
                this.finish()
            }
        }
        val params = FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        val frameLayout = findViewById<FrameLayout>(R.id.frame)
        frameLayout.addView(remoteView, params)
    }

    /**
     *  震动
     */
    private fun setVibrator() {
        val vibrator = this.getSystemService(VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(300)
    }

    override fun onStart() {
        super.onStart()
        remoteView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        remoteView?.onResume()
        if (animation == null) {
            animation = TranslateAnimation(0F, 0F, 150F, 1500F)
        }
        scanQrLine.visibility = View.VISIBLE
        animation?.run {
            duration = 2000
            repeatCount = -1
        }
        scanQrLine.startAnimation(animation)
        animation?.start()
    }

    override fun onPause() {
        super.onPause()
        remoteView?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        remoteView?.onDestroy()
    }

    override fun onStop() {
        super.onStop()
        remoteView?.onStop()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.img_back -> {
                finish()
            }
            R.id.iv_photo -> {
                openPhoto()
            }
        }
    }

    private fun openPhoto() {
        Acp.getInstance(this).request(
            AcpOptions.Builder()
                .setPermissions(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).build(),
            object : AcpListener {
                /**
                 * 同意
                 */
                override fun onGranted() {
                    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(intent, DEVICE_PHOTO_REQUEST)
                }

                /**
                 * 拒绝
                 */
                override fun onDenied(permissions: MutableList<String>?) {
                    toast("全选拒绝")
                }
            })
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK || data == null) {
            return
        }
        if (requestCode == DEVICE_PHOTO_REQUEST) {
            val path = getImagePath(this, data)
            if (TextUtils.isEmpty(path)) {
                return
            }
            val bitmap = ScanUtil.compressBitmap(this, path)
            val result1 = ScanUtil.decodeWithBitmap(this, bitmap, HmsScanAnalyzerOptions.Creator().setHmsScanTypes(0).setPhotoMode(false).create())
            if (result1 != null && result1.isNotEmpty()) {
                if (!TextUtils.isEmpty(result1[0].getOriginalValue())) {
                    val scanResult = result1[0].getOriginalValue()
                    if (scanResult != null) {
                        val intent = Intent()
                        intent.putExtra("data",scanResult)
                        setResult(intentCode,intent)
                    } else {
                        toast("暂无识别")
                    }
                    this.finish()
                }
            }
        }
    }


    /** getImagePath form intent
     */
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun getImagePath(context: Context, data: Intent): String? {
        var imagePath: String? = null
        val uri = data.data
        //get api version
        val currentapiVersion = Build.VERSION.SDK_INT
        if (currentapiVersion > Build.VERSION_CODES.KITKAT) {
            if (DocumentsContract.isDocumentUri(context, uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                if ("com.android.providers.media.documents" == uri!!.authority) {
                    val id = docId.split(":").toTypedArray()[1]
                    val selection = MediaStore.Images.Media._ID + "=" + id
                    imagePath = getImagePath(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection)
                } else if ("com.android.providers.downloads.documents" == uri.authority) {
                    val contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), docId.toLong())
                    imagePath = getImagePath(context, contentUri, null)
                } else {
                    Log.i("TAG", "getImagePath  uri.getAuthority():" + uri.authority)
                }
            } else if ("content".equals(uri!!.scheme, ignoreCase = true)) {
                imagePath = getImagePath(context, uri, null)
            } else {
                Log.i("TAG", "getImagePath  uri.getScheme():" + uri.scheme)
            }
        } else {
            imagePath = getImagePath(context, uri, null)
        }
        return imagePath
    }

    /**
     * get image path from system album by uri
     */
    private fun getImagePath(context: Context, uri: Uri?, selection: String?): String? {
        var path: String? = null
        val cursor = context.contentResolver.query(uri!!, null, selection, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            }
            cursor.close()
        }
        return path
    }
}