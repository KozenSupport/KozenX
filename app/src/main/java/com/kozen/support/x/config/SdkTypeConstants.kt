package com.kozen.support.x.config

object SdkTypeConstants {

    const val MDM = "com.custom.mdm.CustomAPI"
    const val COMPONENT = "com.kozen.component.engine.IComponentEngine"
    const val TERMINAL = "com.kozen.terminalmanager.ITerminalManager"
    const val FINANCIAL = "com.kozen.financial.engine.IFinancialEngine"

    fun getClassNameList(sdkType : String) : ArrayList<String>{
        when(sdkType){
            MDM -> return arrayListOf(
                "com.custom.mdm.CustomAPI"
            )
            COMPONENT -> return arrayListOf(
                "com.kozen.component.keyboard.IKeyboard",
                "com.kozen.component.secondaryScreen.ISecondaryScreen"
            )
            TERMINAL -> return arrayListOf(
                "com.kozen.terminalmanager.certification.ICertificationManager",
                "com.kozen.terminalmanager.deviceinfo.IDeviceInfoManager",
                "com.kozen.terminalmanager.device.IDeviceManager",
                "com.kozen.terminalmanager.location.ILocationManager",
                "com.kozen.terminalmanager.network.INetworkManager",
                "com.kozen.terminalmanager.perceptioninfo.IPerceptionInfoManager",
                "com.kozen.terminalmanager.resource.IResourceManager",
                "com.kozen.terminalmanager.log.ILogManager"

            )
            FINANCIAL -> return arrayListOf(
                "com.kozen.financial.cardreader.ICardReaderManager",
                "com.kozen.financial.ecr.IEcrManager",
                "com.kozen.financial.emv.IEmvManager",
                "com.kozen.financial.general.IGeneralManager",
                "com.kozen.financial.pinpad.IPinpadManager",
                "com.kozen.financial.printer.IPrinterManager",
                "com.kozen.financial.scanner.IScannerManager",
                "com.kozen.financial.security.ISecurityManager",
                )
            else -> return arrayListOf()
        }
    }

    fun getMethodNameList(sdkType : String) : ArrayList<String> {
        when (sdkType) {
            MDM -> return arrayListOf(

            )

            COMPONENT -> return arrayListOf(
                "startPhysicalKeyboard",
                "stopPhysicalKeyboard",
                "isKeyButtonVoiceEnable",
                "switchKeyButtonVoiceEnable",

                "getScreenResolution",
                "power",
                "setBrightness",
                "setBootLogo",
                "show",
                "showPic",
                "showVideo",
                "showWallpaper",
                "getPowerOnStatus",
                "getBrightness"
            )

            TERMINAL -> return arrayListOf(
                // Certification module
                "deleteAppSignature",
                "getAppSignatureInfo",
                "updateAppSignature",

                // Device information module
                "getEmvKernelVersion",
                "getHardwareVersion",
                "getImei",
                "getImsi",
                "getKernelVersion",
                "getMcuVersion",
                "getOsVersion",
                "getSdkServiceVersion",
                "getSerialNo",
                "getDeviceModel",
                "getVendorName",
                "getCSN",
                "getTUSN",

                // Device module
                "cancelPCIReboot",
                "getSystemTime",
                "getTimeZone",
                "reboot",
                "setPCIReboot",
                "setSystemTime",
                "setTimeZone",
                "shutdown",
                "forcePermission",
                "setSilentInstall",
                "setScreenTimeOut",
                "sleep",
                "wakeUp",

                // Location module
                "addToBlockOpenAppList",
                "getBlockOpenAppList",
                "isInBlockOpenAppList",
                "onDestroy",
                "open",
                "registerGeoFenceCreateListener",
                "registerLocationListener",
                "removeAllGeoFence",
                "removeFromBlockOpenAppList",
                "setGeoFenceResultAction",
                "setLocationOption",
                "startOnceLocation",
                "stopLocation",
                "unRegisterLocationListener",
                "addGeoFence",

                // Network module
                "addApn",
                "enableApn",

                // Resource module
                "installOrUpdate",
                "unInstall",
                "updateCustomRes",
                "updateOTA",
                "installOrUpdateWithListener",
                "updateCustomResWithListener",
                "updateOTAWithListener",

                // Device Log module
                "getDeviceLogsPath",

                // Perception Info module
                "collectPerceptionData",
                "getBatteryCurrentMaxCapacity",
                "getBatteryCycleCount",
                "getBatteryDesignCapacity",
                "getBatteryHealthPercent",
                "getBatteryHealthStatus",
                "getPrintDistance",
                "getSmallBatteryVoltage"
            )

            FINANCIAL -> return arrayListOf(
                // Scanner Operation module
                "close",
                "isBarcodeEnabled",
                "open",
                "registerResultCallback",
                "setBarcodeEnable",
                "startScan",
                "stopScan",

                // Card Reader Operation module
                "powerOff",
                "powerOn",
                "checkCard",
                "getCardExistStatus",
                "stopCheck",
                "transmitApdu",
                "hceWrite",
                "hceRead",
                "detectCard",
                "detectContactlessCard",
                "detectFelicaCard",

                // EMV Operation module
                "deleteAid",
                "deleteAppleMerchant",
                "deleteCapk",
                "deleteDRL",
                "deleteExceptionFile",
                "deleteRevocationIPK",
                "deleteService",
                "getAid",
                "getAppleMerchant",
                "getAppleTerminal",
                "getCapk",
                "getDRL",
                "getExceptionFile",
                "getKernel",
                "getRevocationIPK",
                "getService",
                "getTerminal",
                "getVersion",
                "setAid",
                "setAppleMerchant",
                "setAppleTerminal",
                "setCapk",
                "setCardInfoResponse",
                "setDRL",
                "setExceptionFile",
                "setKernel",
                "setOnlineResponse",
                "setPinResponse",
                "setRevocationIPK",
                "setSelectApplicationResponse",
                "setService",
                "setTerminal",
                "startTransaction",
                "stopTransaction",
                // General Operation function
                "setTime",
                "getTime",
                "setTimeZone",
                "getTimeZone",
                "setBeep",
                "setNavigationBar",
                "setStatusBar",
                "wakeUp",
                "shutdown",
                "getSystemProperty",
                "setSystemProperty",
                "reboot",
                "setScreenRotation",
                "led",
                "setLedVisible",
                "checkDependencyVersion",
                "setNotificationShade",
                "setNfcLogoVisible",
                // PINPAD Operation module
                "cancelInputPin",
                "startInputPin",
                "isBlindModeEnable",
                "switchBlindMode",
                // Printing Operation module
                "addBarcode",
                "addBitmap",
                "addText",
                "close",
                "feedPaper",
                "getPrinterStatus",
                "open",
                "setFont",
                "setGray",
                "setLineSpace",
                "startPrint",
                "wrapLine",
                "setClearPrintCacheOnPaperOut",
                "setGrayByPercent",
                "getGlobalFontSize",
                "getGrayByPercent",
                "getLineSpaceByMultiplier",
                "setGlobalFontSize",
                "setLineSpaceByMultiplier",
                // Security module
                "calcDes",
                "calcDukpt",
                "calcMac",
                "calcMacDukpt",
                "calcRsa",
                "generateRsaKey",
                "getKCV",
                "getKsnDukpt",
                "getRandom",
                "increaseKsnDukpt",
                "readRsaKey",
                "writeKey",
                "writeKeyDukpt",
                "writeRsaKey",
                "calcMacDukptDes",
                "eraseAllKey",
                "writeKeyDukptDes",
                "writeKeyMKSK",
                "calcRsaDecrypt",
                "calcRsaEncrypt",
                "writeKeyTR31",
                "writeKeyDukptAes",
                "calcDukptAes",
                "calcMacDukptAes",
                "calcSM4",
                // ECR module
                "close",
                "getLocal",
                "openOrConnect",
                "hideHost",
                "registerConnectionListener",
                "unregisterConnectionListener",
                "showClientByQR",
                "showHostByQR",
                "getPrinterMaster",
                "isOpenOrConnect",
                "showClientByNFC",
                "showHostByNFC",
                // Scanner module
                "close",
                "isBarcodeEnabled",
                "open",
                "registerResultCallback",
                "setBarcodeEnable",
                "startScan",
                "stopScan",
                "switchLight",
                "setAFModeEnable",
                "startDecoding",
                "stopDecoding"
            )

            else -> return arrayListOf()
        }
    }
}