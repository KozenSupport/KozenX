package com.kozen.support.x.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.kozen.support.x.R

/**
 * 主页
 */
class MainPage: AppCompatActivity(){


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        val btnTerminalManagerSDK: Button = findViewById(R.id.btnTerminalManager)
        val btnComponentSDK: Button = findViewById(R.id.btnComponentManager)
        val btnFinancialSDK: Button = findViewById(R.id.btnFinancialManager)
        val btnCustomerApiManager: Button = findViewById(R.id.btnCustomerApiManager)

        btnTerminalManagerSDK.setOnClickListener {
            startActivity(Intent(this, TerminalManagerActivity::class.java))
        }

        btnComponentSDK.setOnClickListener {
            startActivity(Intent(this, ComponentManagerActivity::class.java))
        }

        btnFinancialSDK.setOnClickListener {
            startActivity(Intent(this, FinancialManagerActivity::class.java))
        }

        btnCustomerApiManager.setOnClickListener {
            startActivity(Intent(this, CustomerApiManagerActivity::class.java))
        }
    }
}