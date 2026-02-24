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

    private const val LOG_TAG = "SDK MANAGER"
    private var SDK_TYPE = ""
    private var sdkClass: Class<*>? = null

    private var managerMap = mutableMapOf<String,Any>()

    private var methodMap = mutableMapOf<String,List<Method>>()

    fun init(context: Context, sdkType: String?) {
        try {
            if(sdkType == null || sdkType == ""){
                CommonTools.showMethodDialog(context,"Error!","Please check SDK type config!")
                return
            }
            SDK_TYPE = sdkType;
            initSKD(context,sdkType)
            getAllApiMethods()
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

    fun releaseComponent() {
        try {
            ComponentEngine.deInit()
            Log.i(LOG_TAG,"released : $SDK_TYPE")
        } catch (e: Exception) {
            Log.e(LOG_TAG,"released failed : $SDK_TYPE",e)
        }
    }

    fun releaseTerminal() {
        try {
//            TerminalManager.deInit()
            Log.i(LOG_TAG,"released : $SDK_TYPE")
        } catch (e: Exception) {
            Log.e(LOG_TAG,"released failed : $SDK_TYPE",e)
        }
    }

    fun releaseFinancial() {
        try {
//            FinancialEngine.deInit()
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

    fun getAllApiMethods(sdkType : String): List<Method>? {
        return methodMap[sdkType]
    }

    fun getAllApiMethods() {
        methodMap[SDK_TYPE] = buildList {
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
            val mlist = SdkTypeConstants.getMethodNameList(SDK_TYPE)
            (mlist.isEmpty() &&
                    Modifier.isPublic(it.modifiers) &&
                    it.name != "init" &&
                    it.name != "release") ||
//            Modifier.isStatic(it.modifiers) &&
                    (mlist.contains(it.name) &&
                    Modifier.isPublic(it.modifiers) &&
                    it.name != "init" &&
                    it.name != "release")
        }.sortedBy { it.name }


    }

    fun initSKD(context : Context, sdkType : String){
        if(sdkType == ""){
            CommonTools.showMethodDialog(context,"Error!","Please check SDK type config!")
            return
        }
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
            SdkTypeConstants.COMPONENT -> releaseComponent()
            SdkTypeConstants.TERMINAL -> releaseTerminal()
            SdkTypeConstants.FINANCIAL -> releaseFinancial()
            else -> CommonTools.showMethodDialog(context,"Error","No SdkType Info!")
        }
        Log.i(LOG_TAG,"released : $SDK_TYPE")
    }

    fun initMDM(context : Context){
        CustomAPI.init(context)
    }
    fun initComponentSDK(context : Context){
        ComponentEngine.init(context){code,msg->
            val message = if(code == 0){
                ComponentEngine.keyboardManager?.let { managerMap.put("com.kozen.component.keyboard.IKeyboard",it) }
                ComponentEngine.secondaryScreenManager?.let { managerMap.put("com.kozen.component.secondaryScreen.ISecondaryScreen",it) }
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
                TerminalManager.networkManager?.let { managerMap.put("com.kozen.terminalmanager.network.INetworkManager",it) }
                TerminalManager.logManager?.let { managerMap.put("com.kozen.terminalmanager.log.ILogManager",it) }
                TerminalManager.locationManager?.let { managerMap.put("com.kozen.terminalmanager.location.ILocationManager",it) }
                TerminalManager.certificationManager?.let { managerMap.put("com.kozen.terminalmanager.certification.ICertificationManager",it) }
                TerminalManager.deviceManager?.let { managerMap.put("com.kozen.terminalmanager.device.IDeviceManager",it) }
                TerminalManager.deviceInfoManager?.let { managerMap.put("com.kozen.terminalmanager.deviceinfo.IDeviceInfoManager",it) }
                TerminalManager.perceptionInfoManager?.let { managerMap.put("com.kozen.terminalmanager.perceptioninfo.IPerceptionInfoManager",it) }
                TerminalManager.resourceManager?.let { managerMap.put("com.kozen.terminalmanager.resource.IResourceManager",it) }

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
                FinancialEngine.ecrManager?.let { managerMap.put("com.kozen.financial.ecr.IEcrManager",it) }
                FinancialEngine.cardReaderManager?.let { managerMap.put("com.kozen.financial.cardreader.ICardReaderManager",it) }
                FinancialEngine.emvManager?.let { managerMap.put("com.kozen.financial.emv.IEmvManager",it) }
                FinancialEngine.generalManager?.let { managerMap.put("com.kozen.financial.general.IGeneralManager",it) }
                FinancialEngine.printerManager?.let { managerMap.put("com.kozen.financial.printer.IPrinterManager",it) }
                FinancialEngine.pinpadManager?.let { managerMap.put("com.kozen.financial.pinpad.IPinpadManager",it) }
                FinancialEngine.scannerManager?.let { managerMap.put("com.kozen.financial.scanner.IScannerManager",it) }
                FinancialEngine.securityManager?.let { managerMap.put("com.kozen.financial.security.ISecurityManager",it) }
                "Init FinancialSDK Success!"
            }else{
                "Init FinancialSDK Failed! $msg"
            }
            Log.i(LOG_TAG,message)
            Toast.makeText(context,message, Toast.LENGTH_SHORT).show()
        }
    }

    fun getProviderInstance(methodName: String): Any? {
        Log.d(LOG_TAG,"methodName : $methodName , managerMap: [$managerMap]")
        return managerMap.entries.find { (key, _) ->
            methodName.contains(key, ignoreCase = true)
        }?.value
    }
}