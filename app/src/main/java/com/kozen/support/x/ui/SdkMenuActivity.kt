package com.kozen.support.x.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.kozen.support.x.R
import com.kozen.support.x.config.SdkTypeConstants

/**
 * 主页
 */
class SdkMenuActivity: AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sdk_menu_activity)

        val intentMain = Intent(this, ApiListActivity::class.java)

        val btnTerminalManagerSDK: Button = findViewById(R.id.btnTerminalManager)
        val btnComponentSDK: Button = findViewById(R.id.btnComponentManager)
        val btnFinancialSDK: Button = findViewById(R.id.btnFinancialManager)
        val btnCustomerApiManager: Button = findViewById(R.id.btnCustomerApiManager)

        btnTerminalManagerSDK.setOnClickListener {
            intentMain.putExtra("SDK_TYPE", SdkTypeConstants.Terminal)
            startActivity(intentMain)
        }

        btnComponentSDK.setOnClickListener {
            intentMain.putExtra("SDK_TYPE", SdkTypeConstants.Component)
            startActivity(intentMain)
        }

        btnFinancialSDK.setOnClickListener {
            intentMain.putExtra("SDK_TYPE", SdkTypeConstants.Financial)
            startActivity(intentMain)
        }

        btnCustomerApiManager.setOnClickListener {
            intentMain.putExtra("SDK_TYPE", SdkTypeConstants.MDM)
            startActivity(intentMain)
        }
    }
}