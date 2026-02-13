package com.kozen.support.x.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.custom.mdm.CustomAPI
import com.kozen.component_client.ComponentEngine
import com.kozen.financial.engine.FinancialEngine
import com.kozen.support.x.config.SdkTypeConstants
import com.kozen.terminalmanager.TerminalManager
import java.lang.reflect.Method
import java.lang.reflect.Modifier

object SdkManager {

    private val LOG_TAG = "SDK MANAGER"
    private var SDK_TYPE = "";
    private var sdkClass: Class<*>? = null

    fun init(context: Context, sdkType: String?) {
        try {
            if(sdkType == null || sdkType == ""){
                CommonTools.showMethodDialog(context,"Error!","Please check SDK type config!")
                return
            }
            SDK_TYPE = sdkType;
            initSKD(context,sdkType)
            Log.i(LOG_TAG,"init success : $SDK_TYPE")
        } catch (e: Exception) {
            Log.e(LOG_TAG,"init failed : $SDK_TYPE",e)
        }
    }

    fun releaseMDM() {
        try {
            CustomAPI.release()
            Log.i(LOG_TAG,"released : $SDK_TYPE")
        } catch (e: Exception) {
            Log.e(LOG_TAG,"released failed : $SDK_TYPE",e)
        }
    }

    fun getSdkVersion(): String {
        return try {
            // 尝试反射获取版本号字段或方法，若无则返回固定值
            val versionField = sdkClass?.getDeclaredField("VERSION")
            versionField?.get(null)?.toString() ?: "1.0.0"
        } catch (e: Exception) {
            Log.e(LOG_TAG,"CustomAPI getSdkVersion error : ",e)
            "Unknown"
        }
    }

    fun getAllApiMethods(): List<Method> {
        return buildList {
            SdkTypeConstants.getClassNameList(SDK_TYPE).forEach { className ->
                try {
                    val methods = Class.forName(className).declaredMethods
                    Log.i(LOG_TAG, "Methods : $className : [$methods.]")
                    addAll(methods)
                } catch (e: ClassNotFoundException) {
                    Log.e(LOG_TAG, "Class not found: $className", e)
                }
            }
        }.filter {
//            Modifier.isStatic(it.modifiers) &&
                    Modifier.isPublic(it.modifiers) &&
                    it.name != "init" &&
                    it.name != "release"
        }.sortedBy { it.name }
    }

    fun initSKD(context : Context, sdkType : String){
        when(sdkType){
            SdkTypeConstants.MDM ->  initMDM(context)
            SdkTypeConstants.COMPONENT -> initComponentSDK(context)
            SdkTypeConstants.TERMINAL -> initTerminalSDK(context)
            SdkTypeConstants.FINANCIAL -> initFinancialSDK(context)
            else -> CommonTools.showMethodDialog(context,"Error","No SdkType Info!")
        }
    }

    fun releaseSDK(context : Context, sdkType : String?){
        if(sdkType == null || sdkType == ""){
            CommonTools.showMethodDialog(context,"Error!","Please check SDK type config!")
            return
        }
        when(sdkType){
            SdkTypeConstants.MDM ->  releaseMDM()
//            SdkTypeConstants.Component -> initComponentSDK(context)
//            SdkTypeConstants.Terminal -> initTerminalSDK(context)
//            SdkTypeConstants.Financial -> initFinancialSDK(context)
//            else -> CommonTools.showMethodDialog(context,"Error","No SdkType Info!")
        }
        Log.i(LOG_TAG,"released : $SDK_TYPE")
    }

    fun initMDM(context : Context){
        CustomAPI.init(context)
    }
    fun initComponentSDK(context : Context){
        ComponentEngine.init(context){code,msg->
            val message = if(code == 0){
                "Init ComponentSDK Success!"
            }else{
                "Init ComponentSDK Failed! $msg"
            }
            Log.i(LOG_TAG,message)
            Toast.makeText(context,message, Toast.LENGTH_SHORT).show()
        }
    }

    fun initTerminalSDK(context : Context){
        TerminalManager.init(context){code,msg->
            val message = if(code == 0){
                "Init TerminalSDK Success!"
            }else{
                "Init TerminalSDK Failed! $msg"
            }
            Log.i(LOG_TAG,message)
            Toast.makeText(context,message, Toast.LENGTH_SHORT).show()
        }
    }

    fun initFinancialSDK(context : Context){
        FinancialEngine.init(context){ code, msg->
            val message = if(code == 0){
                "Init FinancialSDK Success!"
            }else{
                "Init FinancialSDK Failed! $msg"
            }
            Log.i(LOG_TAG,message)
            Toast.makeText(context,message, Toast.LENGTH_SHORT).show()
        }
    }
}