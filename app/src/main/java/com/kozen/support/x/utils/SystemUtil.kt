package com.kozen.support.x.utils

import android.app.Activity
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.provider.Settings.Global.AIRPLANE_MODE_ON
import android.util.Log
import com.kozen.support.x.constants.PermissionConstants


/**
 * SystemUtil
 * Static utility class for Android system related tests.
 */
object SystemUtil {


    val locationListener = object : LocationListener {

        override fun onLocationChanged(location: Location) {
            Log.d(
                "GPS",
                "lat=${location.latitude}, lon=${location.longitude}"
            )
        }
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
    }

    fun screenPowerTest(context: Context){
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP, "SYSTEM_UTIL:screenPowerTest");
        wakeLock?.acquire(3000L);
    }


    /**
     * Gets current screen timeout in milliseconds.
     * Test method: Read Settings.System.SCREEN_OFF_TIMEOUT
     */
    fun getScreenTimeout(context: Context): Int {
        return Settings.System.getInt(
            context.contentResolver,
            Settings.System.SCREEN_OFF_TIMEOUT
        )
    }




    /**
     * Sets screen timeout in milliseconds.
     * Test method: Requires WRITE_SETTINGS permission and user input.
     */
    fun setScreenTimeout(activity: Activity, timeoutMs: Int) {
        if(!PermissionUtil.hasPermission(activity,PermissionConstants.WRITE_SETTINGS)){
            PermissionUtil.requestWriteSettings(activity);
        }
        Settings.System.putInt(
            activity.contentResolver,
            Settings.System.SCREEN_OFF_TIMEOUT,
            timeoutMs
        )
    }


    /**
     * Checks whether airplane mode is enabled.
     * Test method: Read Settings.Global.AIRPLANE_MODE_ON
     */
    fun isAirplaneModeOn(context: Context): Boolean {
        return Settings.Global.getInt(
            context.contentResolver,
            AIRPLANE_MODE_ON, 0
        ) == 1
    }

    fun trackGpsLocation(context: Activity) {
        PermissionUtil.requestPermission(context, PermissionConstants.LOCATION, PermissionConstants.LOCATION_PERMISSION_CODE)
//        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
//        return locationManager.requestLocationUpdates(
//            LocationManager.GPS_PROVIDER,
//            100L,
//            10f,
//            locationListener
//        )
    }

    fun stopTrackGpsLocation(context: Context) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.removeUpdates(locationListener)
    }



    fun switchAirPlane(context: Context) {
        Settings.Global.putInt(context.contentResolver, AIRPLANE_MODE_ON, 1)
//        sendBroadcast(Intent(ACTION_AIRPLANE_MODE_CHANGED))
    }

    fun getInstalledApps(context: Context): List<ApplicationInfo?> {
        val packageManager = context.packageManager
        return packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
    }
}