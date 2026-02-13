package com.kozen.support.x.ui

import android.content.Context
import android.util.Log
import com.custom.mdm.CustomAPI
import java.lang.reflect.Method
import java.lang.reflect.Modifier

object MdmSDKManager {
    private const val SDK_CLASS_NAME = "com.custom.mdm.CustomAPI"
    private var sdkClass: Class<*>? = null

    fun init(context: Context) {
        try {
            CustomAPI.init(context);
            sdkClass = Class.forName(SDK_CLASS_NAME)
//            // 调用静态初始化方法 MdmAPI.init(this)
//            val initMethod = sdkClass?.getDeclaredMethod("init", Context::class.java)
//            initMethod?.invoke(null, context)
            Log.i("MDM SDK MANAGER","CustomAPI init success : $SDK_CLASS_NAME")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun release() {
        try {
//            val releaseMethod = sdkClass?.getDeclaredMethod("release")
//            releaseMethod?.invoke(null)
            CustomAPI.release()
            Log.i("MDM SDK MANAGER","CustomAPI released : $SDK_CLASS_NAME")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getSdkVersion(): String {
        return try {
            // 尝试反射获取版本号字段或方法，若无则返回固定值
            val versionField = sdkClass?.getDeclaredField("VERSION")
            versionField?.get(null)?.toString() ?: "1.0.0"
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("MDM SDK MANAGER","CustomAPI getSdkVersion error : ${e.toString()}")
            "Unknown"
        }
    }

    fun getAllApiMethods(): List<Method> {
        val ms = sdkClass?.declaredMethods
        Log.i("MDM SDK MANAGER","methods : [$ms]")
        return ms?.filter {
            val modifiers = it.modifiers
            // 过滤掉静态方法以外的，以及 init/release 本身
            Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers) && it.name != "init" && it.name != "release"
        }?.sortedBy { it.name } ?: emptyList()
    }
}