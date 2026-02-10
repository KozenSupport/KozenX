package com.kozen.support.x.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.custom.mdm.CustomAPI
import com.kozen.support.x.R
import com.kozen.support.x.utils.CustomerMDM_testFactory

class CustomerApiManagerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_api_manager)

        this.initMDM()
        this.bindTestUnits()
    }

    override fun onDestroy() {
        super.onDestroy()
        this.releaseMDM()
    }
    fun initMDM(){
        CustomAPI.init(this)
    }
    fun releaseMDM(){
        CustomAPI.release()
    }
    fun bindTestUnits(){
        // show How To Integrate customerMDM
        CustomerMDM_testFactory.showHowToIntegrate(this,findViewById(R.id.btnShowIntegrateModal))
        //
        CustomerMDM_testFactory.systemUpdate(this,findViewById(R.id.btnShowTestUpdateFWModal))
        // Obtain the current device connection type
        CustomerMDM_testFactory.getConnectTypeByEth(this,findViewById(R.id.btnGetConnectTypeByEth))
        // Enable Logcat logging
        CustomerMDM_testFactory.startLogging(this,findViewById(R.id.btnStartLogging))
        // Stop logging
        CustomerMDM_testFactory.stopLogging(this,findViewById(R.id.btnStopLogging))

        CustomerMDM_testFactory.exportAllLogs(this,findViewById(R.id.btnExportAllLogs))

        CustomerMDM_testFactory.enableChargeLimit(this,findViewById(R.id.btnEnableChargeLimit))

        CustomerMDM_testFactory.disableChargeLimit(this,findViewById(R.id.btnDisableChargeLimit))

        CustomerMDM_testFactory.getChargeLimit(this,findViewById(R.id.btnGetChargeLimit))

        CustomerMDM_testFactory.updateTPFw(this,findViewById(R.id.btnUpdateTPFw))
    }
}

