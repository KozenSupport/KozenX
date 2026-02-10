package com.kozen.support.x.ui;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.custom.mdm.ApnConfig;
import com.custom.mdm.CustomAPI;
import com.custom.mdm.InstallReceiver;
import com.kozen.support.x.R;

public class Test extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Henry Test";
    private int mApnID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //startService(new Intent(getApplication(), MyService.class));


//        findViewById(R.id.btnInstall).setOnClickListener(v -> {
//            CustomAPI.installWithMode("/sdcard/Download/test.apk", 0,  new InstallReceiver.InstallListener() {
//                @Override
//                public void onInstallSuccess(String pkgName) {
//                    Log.d(TAG, "++++INSTALL\n" + "pkgName: " + pkgName);
//                    Toast.makeText(Test.this, "Install Success", Toast.LENGTH_SHORT).show();
//                }
//
//                @Override
//                public void onInstallFail(String pkgName, String msg) {
//                    Log.d(TAG, "----INSTALL\n" + "pkgName: " + pkgName + " Status Message: " + msg);
//                    Toast.makeText(Test.this, "Install Fail: " + msg, Toast.LENGTH_SHORT).show();
//                }
//            });
//        });
//
//        findViewById(R.id.btnInstallAndOpen).setOnClickListener(v -> {
//            CustomAPI.installWithMode("/sdcard/Download/test.apk", 1, new InstallReceiver.InstallListener() {
//                @Override
//                public void onInstallSuccess(String pkgName) {
//                    Log.d(TAG, "++++INSTALL\n" + "pkgName: " + pkgName);
//                    Toast.makeText(Test.this, "Install Success", Toast.LENGTH_SHORT).show();
//                }
//
//                @Override
//                public void onInstallFail(String pkgName, String msg) {
//                    Log.d(TAG, "----INSTALL\n" + "pkgName: " + pkgName + " Status Message: " + msg);
//                    Toast.makeText(Test.this, "Install Fail: " + msg, Toast.LENGTH_SHORT).show();
//                }
//            });
//        });
    }

    public void onClick(View v) {
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyDown: keyCode=" + keyCode);
        Log.d(TAG, "onKeyDown: event=" + event.toString());
        switch (keyCode) {
            case KeyEvent.KEYCODE_HOME:
                Log.d(TAG, "onKeyDown: KEYCODE_HOME");
                return true;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.d(TAG, "dispatchKeyEvent: event=" + event.toString());
        if (event.getKeyCode() == KeyEvent.KEYCODE_HOME) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                Log.d(TAG, "Home key pressed (dispatchKeyEvent)");
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onResume() {
        super.onResume();
        CustomAPI.init(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CustomAPI.release();
    }
}
