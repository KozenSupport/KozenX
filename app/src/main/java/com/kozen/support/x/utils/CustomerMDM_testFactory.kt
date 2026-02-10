package com.kozen.support.x.utils

import android.content.Context
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.custom.mdm.CustomAPI

/**
 * CustomerMDM test Units
 */
object CustomerMDM_testFactory {

    fun showHowToIntegrate(context: Context,btnShowIntegrateModal: Button){
        btnShowIntegrateModal.setOnClickListener {
            CommonTools.showMethodDialog(context,"How to integrate？",
                "1.Add the jar file into your project.\n" +
                        "2.Add dependcy and sync project\n" +
                        "3.Init CustomMDM Service in onCreate() method.[CustomAPI.init()]\n" +
                        "4.Add API calls in appropriate places\n" +
                        "5.Release CustomMDM Service in onDestroy() method.[CustomAPI.release()]")
        }
    }

    fun getConnectTypeByEth(context: Context,btn : TextView) {
        btn.setOnClickListener {
            val res = CustomAPI.getConnectTypeByEth()
            when (res){
                1 -> CommonTools.showMethodDialog(context,"CustomAPI.getConnectTypeByEth()","Connect Type : 1 = USB connection")
                2 -> CommonTools.showMethodDialog(context,"CustomAPI.getConnectTypeByEth()","Connect Type : 2 = Dock connection")
                3 -> CommonTools.showMethodDialog(context,"CustomAPI.getConnectTypeByEth()","Connect Type : 3 = HUB connection")
                0 -> CommonTools.showMethodDialog(context,"CustomAPI.getConnectTypeByEth()","Connect Type : 0 = Exception (used in other projects, etc.)")
                -1 -> CommonTools.showMethodDialog(context,"CustomAPI.getConnectTypeByEth()","Connect Type : -1 = Exception (interface exception)")
                else -> CommonTools.showMethodDialog(context,"Sorry","Internal Exception, Please contact FAE.")
            }
        }
    }

    fun startLogging(context: Context,btn: Button )  {
        btn.setOnClickListener {
            val customerDir = Environment.getExternalStorageDirectory().path + "/Download"
            val customName = "KozenX_log";
            val res = CustomAPI.startLogging(customerDir,customName,null)
            if(res){
                CommonTools.showMethodDialog(context,"CustomAPI.startLogging(customerDir,customName,filter)","SUCCESS : customerDir:$customerDir, customName:$customName,filter:null")
            }else{
                CommonTools.showMethodDialog(context,"CustomAPI.startLogging(customerDir,customName,filter)","FAILED : customerDir:$customerDir, customName:$customName,filter:null")
            }
        }
    }

    fun stopLogging(context: Context, btn: Button) {
        btn.setOnClickListener {
            CustomAPI.stopLogging()
            CommonTools.showMethodDialog(context,"CustomAPI.stopLogging()","SUCCESS ：Logging Stopped")
        }
    }

    fun systemUpdate(context: Context,btn: Button) {
        btn.setOnClickListener {
            val updateFilePath = Environment.getExternalStorageDirectory().path + "/Download/update.zip"
            Log.d("com.custom.mdm.systemUpdate", "systemUpdate received : $updateFilePath")
            try {
                CustomAPI.systemUpdate(updateFilePath)
//            CommonTools.showMethodDialog(this,"CustomAPI.systemUpdate(updateFilePath)","Upgrade Success!")
            } catch (e : NullPointerException){
                Log.d("com.custom.mdm.systemUpdate NullPointerException",e.toString())
                CommonTools.showMethodDialog(context,"CustomAPI.systemUpdate(updateFilePath)","Failed : Please check the path!")
            } catch (e: Exception){
                Log.e("com.custom.mdm.systemUpdate Exception",e.toString())
                CommonTools.showMethodDialog(context,"CustomAPI.systemUpdate(updateFilePath)","Failed : Internal Exception! Please contact FAE")
            }
        }
    }

    fun exportAllLogs(context: Context, btn: Button) {
        val filePath = Environment.getExternalStorageDirectory().path + "/Download"
        btn.setOnClickListener {
            val res = CustomAPI.exportAllLogs(filePath)
            if(res){
                CommonTools.showMethodDialog(context,"CustomAPI.exportAllLogs(filePath)","Export Success at : $filePath")
            }else{
                CommonTools.showMethodDialog(context,"CustomAPI.exportAllLogs(filePath)","Export Failed at : $filePath")
            }
        }
    }

    fun enableChargeLimit(context: Context, btn: Button) {
        btn.setOnClickListener {
            CustomAPI.setChargeLimit(true)
            CommonTools.showMethodDialog(context,"CustomAPI.setChargeLimit(true)","Set Success")
        }
    }

    fun disableChargeLimit(context: Context, btn: Button) {
        btn.setOnClickListener {
            CustomAPI.setChargeLimit(false)
            CommonTools.showMethodDialog(context,"CustomAPI.setChargeLimit(false)","Set Success")
        }
    }

    fun getChargeLimit(context: Context, btn: Button) {
        btn.setOnClickListener {
            CommonTools.showMethodDialog(context,"CustomAPI.getChargeLimit()",CustomAPI.getChargeLimit())
        }
    }

    fun updateTPFw(context: Context, btn: Button) {
        btn.setOnClickListener {
            val path = Environment.getExternalStorageDirectory().path + "/Download"
            val res = CustomAPI.updateSPFirmware(path)
            CommonTools.showMethodDialog(context,"CustomAPI.updateSPFirmware(path)","RESULT : $res\n Path : $path")
        }
    }
}