package com.kozen.support.x.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import com.kozen.support.x.R
import com.kozen.support.x.config.SdkTypeConstants

class SdkMenuActivity : LocalizedAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sdk_menu_activity)

        val btnTerminalManagerSDK: Button = findViewById(R.id.btnTerminalManager)
        val btnComponentSDK: Button = findViewById(R.id.btnComponentManager)
        val btnFinancialSDK: Button = findViewById(R.id.btnFinancialManager)
        val btnPaymentDemo: Button = findViewById(R.id.btnPaymentDemo)
        val btnCustomerApiManager: Button = findViewById(R.id.btnCustomerApiManager)

        btnTerminalManagerSDK.setOnClickListener {
            val intentMain = Intent(this, ApiListActivity::class.java)
            intentMain.putExtra("SDK_TYPE", SdkTypeConstants.TERMINAL)
            startActivity(intentMain)
        }

        btnComponentSDK.setOnClickListener {
            val intentMain = Intent(this, ApiListActivity::class.java)
            intentMain.putExtra("SDK_TYPE", SdkTypeConstants.COMPONENT)
            startActivity(intentMain)
        }

        btnFinancialSDK.setOnClickListener {
            val intentMain = Intent(this, ApiListActivity::class.java)
            intentMain.putExtra("SDK_TYPE", SdkTypeConstants.FINANCIAL)
            startActivity(intentMain)
        }

        btnPaymentDemo.setOnClickListener {
            startActivity(Intent(this, PaymentActivity::class.java))
        }

        btnCustomerApiManager.setOnClickListener {
            val intentMain = Intent(this, ApiListActivity::class.java)
            intentMain.putExtra("SDK_TYPE", SdkTypeConstants.MDM)
            startActivity(intentMain)
        }
    }
}
