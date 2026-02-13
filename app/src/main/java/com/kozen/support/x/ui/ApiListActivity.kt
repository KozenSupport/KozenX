package com.kozen.support.x.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.kozen.support.x.R
import com.kozen.support.x.utils.CommonTools
import com.kozen.support.x.utils.SdkManager
import com.kozen.support.x.utils.ApiAdapter

class ApiListActivity : AppCompatActivity() {

    private lateinit var apiAdapter: ApiAdapter

    private var sdkType: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sdkType = intent.getStringExtra("SDK_TYPE")

        // 初始化 SDK
        SdkManager.init(this,sdkType)

        setContentView(R.layout.api_list_activity)

        findViewById<TextView>(R.id.tvSdkVersion).text = "SDK Version: ${SdkManager.getSdkVersion()}"

        findViewById<Button>(R.id.btnHowToIntegrate).setOnClickListener {
            CommonTools.showHowToIntegrate(sdkType,this)
        }

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        apiAdapter = ApiAdapter(SdkManager.getAllApiMethods().toMutableList()) { method ->
            // 点击跳转逻辑
            val intent = Intent(this, ApiTestActivity::class.java)
            intent.putExtra("METHOD_NAME", method.name)
            startActivity(intent)
        }
        val rv = findViewById<RecyclerView>(R.id.rvApiList)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = apiAdapter

        val etSearch = findViewById<EditText>(R.id.etSearch)
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                apiAdapter.filter(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        // 释放资源
        SdkManager.releaseSDK(this,sdkType)
    }
}