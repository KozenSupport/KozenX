package com.kozen.support.x.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kozen.support.x.R


/**
 * TestCenterActivity
 * Displays grouped buttons for System, Hardware, and Telephony test cases.
 */
class TestCenterActivity : AppCompatActivity() {




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_center)


        // HardWare Module
        // Detect GPS
//        findViewById<Button>(R.id.btnGpsInfo).setOnClickListener {
//            showMethodDialog(
//                "Detect GPS",
//                "context.packageManager.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS)."
//            )
//        }

//        findViewById<Button>(R.id.btnDetectGPS).setOnClickListener {
//            val result = HardwareUtil.detectGPS(this)
////            Toast.makeText(this, "result: $result", Toast.LENGTH_SHORT).show()
//            showMethodDialog("Yes","GPS exists");
//        }
//
//
//        findViewById<Button>(R.id.btnGpsStatus).setOnClickListener {
//            val result = HardwareUtil.isGpsEnabled(this)
//            showMethodDialog("GPS enable:","" + result);
//        }
//
//        findViewById<Button>(R.id.btnScreenPower).setOnClickListener {
////            if (!Settings.System.canWrite(this)) {
////                PermissionUtil.requestWriteSettings(this)
////            }
////            try {
////                Settings.System.putInt(contentResolver,
////                    Settings.System.SCREEN_OFF_TIMEOUT,
////                    2000) // 2秒
////                Toast.makeText(this, "Screen timeout set to 2 seconds", Toast.LENGTH_SHORT).show()
////                Thread.sleep(3000);
////                val current = SystemUtil.screenPowerTest(this)
////                Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
////            } catch (e: Exception) {
////                e.printStackTrace()
////                Toast.makeText(this, "Screen timeout Error$e", Toast.LENGTH_SHORT).show()
////            }
//            showMethodDialog("Power On of Off Screen:","Not Support");
//        }
//
//        findViewById<Button>(R.id.btnScreenTimeout).setOnClickListener {
//            val result = SystemUtil.getScreenTimeout(this)
//            showMethodDialog("Screen Timeout","" + result + "ms");
//        }
//
//        findViewById<Button>(R.id.btnSetScreenTimeout).setOnClickListener {
//            val result = SystemUtil.setScreenTimeout(this,3000)
//            showMethodDialog("Set Screen Timeout","Success");
//        }
//
//        findViewById<Button>(R.id.btnTrackGpsLocation).setOnClickListener {
//            val result = SystemUtil.trackGpsLocation(this)
//            showMethodDialog("Track Gps Location","Success");
//        }
//
//        findViewById<Button>(R.id.btnStopTrackGpsLocation).setOnClickListener {
//            val result = SystemUtil.stopTrackGpsLocation(this)
//            showMethodDialog("Stop Track Gps Location","Success");
//        }
//
//        findViewById<Button>(R.id.btnDetectBluetooth).setOnClickListener {
//            val result = HardwareUtil.hasBluetooth()
//            showMethodDialog("Detect Bluetooth","Exists");
//        }
//
//        findViewById<Button>(R.id.btnDetectFlashlight).setOnClickListener {
//            val result = HardwareUtil.hasFlashlight(this)
//            showMethodDialog("Detect Flashlight","Exists");
//        }
//
//        findViewById<Button>(R.id.btnCheckFlashlightStatus).setOnClickListener {
//            val result = HardwareUtil.isFlashlightOn(this)
//            showMethodDialog("Flashlight Status","" + result);
//        }
//
//        findViewById<Button>(R.id.btnCheckAirPlaneStatus).setOnClickListener {
//            val result = SystemUtil.isAirplaneModeOn(this);
//            showMethodDialog("Airplane Status","" + result);
//        }
//
//        findViewById<Button>(R.id.btnSwitchAirplane).setOnClickListener {
////            val result = SystemUtil.switchAirPlane(this);
//            showMethodDialog("Warning","The official system only allows users to be guided to set it up themselves.");
//        }
//
//        findViewById<Button>(R.id.btnGetSlotNumbers).setOnClickListener {
//            val result = TelephonyUtil.getSimSlotCount(this);
//            showMethodDialog("SimSlot Count","" + result);
//        }
//
//        findViewById<Button>(R.id.btnGetSIM_ICCID).setOnClickListener {
////            val result = TelephonyUtil.getIccid(this);
//            showMethodDialog("Warning","The official system only allows users to be guided to set it up themselves.");
//        }
//
//        findViewById<Button>(R.id.btnGetEsimEid).setOnClickListener {
//            val result = TelephonyUtil.getEid(this);
//            showMethodDialog("Esim Eid","" + result);
//        }
//
//        findViewById<Button>(R.id.btwSwitchSIMSlot).setOnClickListener {
//            showMethodDialog("Warning","The official system only allows users to be guided to set it up themselves.");
////            startActivity(
////                Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS)
////            )
//        }
//
//        findViewById<Button>(R.id.btnSetActiveDataSim).setOnClickListener {
//            showMethodDialog("Warning","This function requires authorization.");
//        }
//
//        findViewById<Button>(R.id.btwApnsForSimSlot).setOnClickListener {
//            showMethodDialog("Warning","This function requires authorization.");
//        }
//
//        findViewById<Button>(R.id.btnGetActiveAPNForSimSlot).setOnClickListener {
//            showMethodDialog("Warning","This function requires authorization.");
//        }
//
//        findViewById<Button>(R.id.btwGetDataUsageOfApps).setOnClickListener {
//            showMethodDialog("Warning","This function requires authorization.");
//        }
//
//        findViewById<Button>(R.id.btnGetInstalledApps).setOnClickListener {
//            val result = SystemUtil.getInstalledApps(this);
//            showMethodDialog("AppList",result.toString());
//        }
//
//        findViewById<Button>(R.id.btnInstallModeControl).setOnClickListener {
//            CustomAPI.installWithMode("sdcard/Download/test.apk", 1, object : InstallListener {
//                override fun onInstallSuccess(pkgName: String?) {
//                    Log.i("HENRY_TEST_INSTALL_APP SUCCESS","\"----INSTALL\\n\" + \"pkgName: \" + pkgName")
//                    showMethodDialog("INSTALL SUCCESS", "----INSTALL\npkgName: $pkgName");
//                }
//
//                override fun onInstallFail(pkgName: String?, msg: String?) {
//                    Log.e("HENRY_TEST_INSTALL_APP FAIL",
//                        "----INSTALL\npkgName: $pkgName Status Message: $msg"
//                    )
//                    CommonTools.showMethodDialog(this,"INSTALL FAIL",
//                        "----INSTALL\npkgName: $pkgName Status Message: $msg"
//                    );
//                }
//            })
//        }
    }
}