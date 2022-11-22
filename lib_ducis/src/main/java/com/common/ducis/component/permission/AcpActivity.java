package com.common.ducis.component.permission;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.WindowManager;


public class AcpActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //不接受触摸屏事件
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        getResources(this);
        if (savedInstanceState == null)
            Acp.getInstance(this).getAcpManager().checkRequestPermissionRationale(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getResources(this);
        Acp.getInstance(this).getAcpManager().checkRequestPermissionRationale(this);
    }

    @Override
    protected void onResume() {
        getResources(this);
        super.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Acp.getInstance(this).getAcpManager().onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Acp.getInstance(this).getAcpManager().onActivityResult(requestCode, resultCode, data);
    }

    public Resources getResources(Activity activity) {
        Resources res = activity.getResources();
        Configuration newConfig = new Configuration();
        newConfig.setToDefaults();
        res.updateConfiguration(newConfig,res.getDisplayMetrics());
        return res;
    }
}
