package com.kozen.support.x.utils

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.kozen.support.x.config.SdkTypeConstants

object CommonTools {

    fun showMethodDialog(context : Context, title: String, message: String) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    fun showHowToIntegrateMDM(context : Context) {
        AlertDialog.Builder(context)
            .setTitle("How To Integrate?")
            .setMessage(
                "1.Import the aar/jar file.\n" +
                        "2.Call `CustomAPI.init(context)`in onCreate() method.\n" +
                        "3.Call any API you want.\n" +
                        "4.Call `CustomAPI.release()`in onDestroy()."
            )
            .setPositiveButton("确定", null)
            .show()
    }

    fun showHowToIntegrateComponent(context : Context) {
        AlertDialog.Builder(context)
            .setTitle("How To Integrate?")
            .setMessage(
                "1.Import the aar/jar file.\n" +
                        "2.Call `ComponentEngine.init(context)`in onCreate() method.\n" +
                        "3.Obtain the corresponding manager to execute API calls."
            )
            .setPositiveButton("确定", null)
            .show()
    }

    fun showHowToIntegrateTerminal(context : Context) {
        AlertDialog.Builder(context)
            .setTitle("How To Integrate?")
            .setMessage(
                "1.Import the aar/jar file.\n" +
                        "2.Call `TerminalManager.init(context)`in onCreate() method.\n" +
                        "3.Obtain the corresponding manager to execute API calls."
            )
            .setPositiveButton("确定", null)
            .show()
    }

    fun showHowToIntegrateFincial(context : Context) {
        AlertDialog.Builder(context)
            .setTitle("How To Integrate?")
            .setMessage(
                "1.Import the aar/jar file.\n" +
                        "2.Call `FinancialEngine.init(context)`in onCreate() method.\n" +
                        "3.Obtain the corresponding manager to execute API calls."
            )
            .setPositiveButton("确定", null)
            .show()
    }

    fun showHowToIntegrate(sdkType: String?, context: Context) {
        when(sdkType){
            SdkTypeConstants.MDM -> this.showHowToIntegrateMDM(context)
            SdkTypeConstants.COMPONENT -> this.showHowToIntegrateComponent(context)
            SdkTypeConstants.TERMINAL -> this.showHowToIntegrateTerminal(context)
            SdkTypeConstants.FINANCIAL -> this.showHowToIntegrateFincial(context)
            else -> this.showMethodDialog(context,"Error!","Please check the SDK type config!")
        }
    }
}