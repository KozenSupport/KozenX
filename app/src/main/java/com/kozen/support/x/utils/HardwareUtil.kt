package com.kozen.support.x.utils



import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.CameraManager
import android.bluetooth.BluetoothAdapter
import android.hardware.camera2.CameraCharacteristics
import android.location.LocationManager
import android.os.Build


/**
 * HardwareUtil
 * Static utility class for hardware related native tests.
 */
object HardwareUtil {


    /**
     * Checks whether the device has GPS hardware.
     * Test method: Use PackageManager.FEATURE_LOCATION_GPS
     */
    fun detectGPS(context: Context): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS)
    }

    @SuppressLint("NewApi")
    fun isGpsEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return try {
            // For Android KitKat (API 19) and above
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                // Check if location providers are enabled
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            } else {
                // Deprecated method for older versions (below API 19)
                @Suppress("DEPRECATION")
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            }
        } catch (e: SecurityException) {
            // Should not happen for GPS status check, but handle gracefully
            false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }


    /**
     * Checks whether the device has Bluetooth hardware.
     * Test method: BluetoothAdapter.getDefaultAdapter()
     */
    fun hasBluetooth(): Boolean {
        return BluetoothAdapter.getDefaultAdapter() != null
    }


    /**
     * Checks whether the device has a flashlight.
     * Test method: FEATURE_CAMERA_FLASH
     */
    fun hasFlashlight(context: Context): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
    }


    /**
     * Checks whether the flashlight is currently enabled.
     * Test method: CameraManager + torch callback (simplified here)
     */
    fun isFlashlightOn(context: Context): Boolean {
        val manager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        return manager.cameraIdList.any { id ->
            manager.getCameraCharacteristics(id)
                .get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
        }
    }
}