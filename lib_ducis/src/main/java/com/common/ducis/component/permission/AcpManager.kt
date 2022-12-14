package com.common.ducis.component.permission

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import razerdp.basepopup.BasePopupWindow
import java.util.*

/**
 * Created by hupei on 2016/4/26.
 */
class AcpManager(private val mContext: Context) {
    private var mActivity: Activity? = null
    private val mService: AcpService
    private var mOptions: AcpOptions? = null
    private var mCallback: AcpListener? = null
    private val mDeniedPermissions: MutableList<String> =
        LinkedList()
    private val mManifestPermissions: MutableSet<String> =
        HashSet(1)

    @get:Synchronized
    private val manifestPermissions: Unit
        private get() {
            var packageInfo: PackageInfo? = null
            try {
                packageInfo = mContext.packageManager.getPackageInfo(
                    mContext.packageName, PackageManager.GET_PERMISSIONS
                )
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
            if (packageInfo != null) {
                val permissions = packageInfo.requestedPermissions
                if (permissions != null) {
                    for (perm in permissions) {
                        mManifestPermissions.add(perm)
                    }
                }
            }
        }

    /**
     * 开始请求
     *
     * @param options
     * @param acpListener
     */
    @Synchronized
    fun request(options: AcpOptions?, acpListener: AcpListener?) {
        mCallback = acpListener
        mOptions = options
        checkSelfPermission()
    }

    /**
     * 检查权限
     */
    @Synchronized
    private fun checkSelfPermission() {
        mDeniedPermissions.clear()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Log.i(
                TAG,
                "Build.VERSION.SDK_INT < Build.VERSION_CODES.M"
            )
            if (mCallback != null) mCallback!!.onGranted()
            onDestroy()
            return
        }
        val permissions = mOptions!!.permissions
        for (permission in permissions) {
            //检查申请的权限是否在 AndroidManifest.xml 中
            if (mManifestPermissions.contains(permission)) {
                val checkSelfPermission = mService.checkSelfPermission(mContext, permission)
                Log.i(
                    TAG,
                    "checkSelfPermission = $checkSelfPermission"
                )
                //如果是拒绝状态则装入拒绝集合中
                if (checkSelfPermission == PackageManager.PERMISSION_DENIED) {
                    mDeniedPermissions.add(permission)
                }
            }
        }
        //检查如果没有一个拒绝响应 onGranted 回调
        if (mDeniedPermissions.isEmpty()) {
            Log.i(TAG, "mDeniedPermissions.isEmpty()")
            if (mCallback != null) mCallback!!.onGranted()
            onDestroy()
            return
        }
        startAcpActivity()
    }

    /**
     * 启动处理权限过程的 Activity
     */
    @Synchronized
    private fun startAcpActivity() {
        val intent = Intent(mContext, AcpActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        mContext.startActivity(intent)
    }

    /**
     * 检查权限是否存在拒绝不再提示
     *
     * @param activity
     */
    @Synchronized
    fun checkRequestPermissionRationale(activity: Activity?) {
        mActivity = activity
        var rationale = false
        //如果有拒绝则提示申请理由提示框，否则直接向系统请求权限
        for (permission in mDeniedPermissions) {
            rationale =
                rationale || mService.shouldShowRequestPermissionRationale(mActivity, permission)
        }
        Log.i(TAG, "rationale = $rationale")
        val permissions =
            mDeniedPermissions.toTypedArray()
        if (rationale) {
            showRationalDialog(permissions)
        } else {
            requestPermissions(permissions)
        }
    }

    /**
     * 申请理由对话框
     *
     * @param permissions
     */
    @Synchronized
    private fun showRationalDialog(permissions: Array<String>) {
        val popupWindow = DefaultPopupWindow(mActivity)
        popupWindow.setTitleText(mOptions!!.rationalMessage.also { it })
        popupWindow.setOutSideDismiss(false)
        popupWindow.setBackPressEnable(false)
        popupWindow.setOnClickResultListener(object : DefaultPopupWindow.OnSelectedResultListener {
            override fun onSelected(isSure: Boolean) {
                if (isSure) {
                    requestPermissions(permissions)
                }
            }
        })
        popupWindow.showPopupWindow()
    }

    /**
     * 向系统请求权限
     *
     * @param permissions
     */
    @Synchronized
    private fun requestPermissions(permissions: Array<String>) {
        mService.requestPermissions(
            mActivity,
            permissions,
            REQUEST_CODE_PERMISSION
        )
    }

    /**
     * 响应向系统请求权限结果
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Synchronized
    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE_PERMISSION -> {
                val grantedPermissions =
                    LinkedList<String>()
                val deniedPermissions =
                    LinkedList<String>()
                var i = 0
                while (i < permissions.size) {
                    val permission = permissions[i]
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) grantedPermissions.add(
                        permission
                    ) else deniedPermissions.add(permission)
                    i++
                }
                //全部允许才回调 onGranted 否则只要有一个拒绝都回调 onDenied
                if (!grantedPermissions.isEmpty() && deniedPermissions.isEmpty()) {
                    if (mCallback != null) mCallback!!.onGranted()
                    onDestroy()
                } else if (!deniedPermissions.isEmpty()) showDeniedDialog(deniedPermissions)
            }
        }
    }

    /**
     * 拒绝权限提示框
     *
     * @param permissions
     */
    @Synchronized
    private fun showDeniedDialog(permissions: List<String>) {
        val popupWindow = DefaultPopupWindow(mActivity)
        popupWindow.setTitleText(mOptions!!.deniedMessage)
        popupWindow.setOutSideDismiss(false)
        popupWindow.setBackPressEnable(false)
        popupWindow.onDismissListener = object : BasePopupWindow.OnDismissListener() {
            override fun onDismiss() {
                if (mCallback != null) {
                    onDestroy()
                }
            }
        }
        popupWindow.setOnClickResultListener(object : DefaultPopupWindow.OnSelectedResultListener {
            override fun onSelected(isSure: Boolean) {
                if (isSure) {
                    startSetting()
                } else {
                    if (mCallback != null) mCallback!!.onDenied(permissions)
                    onDestroy()
                }
            }
        })
        popupWindow.showPopupWindow()
    }

    /**
     * 摧毁本库的 AcpActivity
     */
    private fun onDestroy() {
        if (mActivity != null) {
            mActivity!!.finish()
            mActivity = null
        }
        mCallback = null
    }

    /**
     * 跳转到设置界面
     */
    private fun startSetting() {
        if (MiuiOs.isMIUI()) {
            val intent = MiuiOs.getSettingIntent(mActivity)
            if (MiuiOs.isIntentAvailable(mActivity, intent)) {
                mActivity!!.startActivityForResult(intent, REQUEST_CODE_SETTING)
                return
            }
        }
        try {
            val intent =
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    .setData(Uri.parse("package:" + mActivity!!.packageName))
            mActivity!!.startActivityForResult(intent, REQUEST_CODE_SETTING)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            try {
                val intent =
                    Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS)
                mActivity!!.startActivityForResult(intent, REQUEST_CODE_SETTING)
            } catch (e1: Exception) {
                e1.printStackTrace()
            }
        }
    }

    /**
     * 响应设置权限返回结果
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Synchronized
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (mCallback == null || mOptions == null || requestCode != REQUEST_CODE_SETTING
        ) {
            onDestroy()
            return
        }
        checkSelfPermission()
    }

    companion object {
        private const val TAG = "AcpManager"
        private const val REQUEST_CODE_PERMISSION = 0x38
        private const val REQUEST_CODE_SETTING = 0x39
    }

    init {
        mService = AcpService()
        manifestPermissions
    }
}