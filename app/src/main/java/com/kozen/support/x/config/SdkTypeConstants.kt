package com.kozen.support.x.config

object SdkTypeConstants {

    const val MDM = "com.custom.mdm.CustomAPI"
    const val Component = "com.kozen.component.engine.IComponentEngine"
    const val Terminal = "com.kozen.terminalmanager.ITerminalManager"
    const val Financial = "com.kozen.financial.engine.IFinancialEngine"

    fun getClassNameList(sdkType : String) : ArrayList<String>{
        when(sdkType){
            MDM -> return arrayListOf(
                "com.custom.mdm.CustomAPI"
            )
            Component -> return arrayListOf(
                "com.kozen.component.keyboard.IKeyboard",
                "com.kozen.component.secondaryScreen.ISecondaryScreen"
            )
            Terminal -> return arrayListOf(
                "com.kozen.terminalmanager.certification.ICertificationManager",
                "com.kozen.terminalmanager.deviceinfo.IDeviceInfoManager",
                "com.kozen.terminalmanager.device.IDeviceManager",
                "com.kozen.terminalmanager.location.ILocationManager",
                "com.kozen.terminalmanager.network.INetworkManager",
                "com.kozen.terminalmanager.perceptioninfo.IPerceptionInfoManager",
                "com.kozen.terminalmanager.resource.IResourceManager",
                "com.kozen.terminalmanager.log.ILogManager"

            )
            Financial -> return arrayListOf(
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
}