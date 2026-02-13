package com.kozen.support.x.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.*
import androidx.recyclerview.widget.RecyclerView
import java.lang.reflect.Method
import com.kozen.support.x.R

class MdmMainActivity : AppCompatActivity() {
    private lateinit var adapter: ApiAdapter
    private var allMethods = mutableListOf<Method>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. 初始化 SDK
        MdmSDKManager.init(this)


        // 2. 动态构建布局（如果不使用 XML，也可以直接在代码中写）
        setContentView(R.layout.mdm_activity_main)

        // 2. 获取数据（确保此时 SDK 已初始化并能反射到方法）
        allMethods = MdmSDKManager.getAllApiMethods().toMutableList()

        // 3. UI 初始化
        findViewById<TextView>(R.id.tvSdkVersion).text = "SDK Version: ${MdmSDKManager.getSdkVersion()}"

        val rv = findViewById<RecyclerView>(R.id.rvApiList)
        rv.layoutManager = LinearLayoutManager(this)

        // 初始化 Adapter
        adapter = ApiAdapter(allMethods) { method ->
            val intent = Intent(this, MdmTestActivity::class.java)
            intent.putExtra("METHOD_NAME", method.name)
            startActivity(intent)
        }
        rv.adapter = adapter

        // 4. 搜索框监听
        val etSearch = findViewById<EditText>(R.id.etSearch)
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                // 实时过滤
                adapter.filter(s.toString())
            }
        })

        findViewById<Button>(R.id.btnHowToIntegrate).setOnClickListener {
            showIntegrationDialog()
        }

//        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val rv = findViewById<RecyclerView>(R.id.rvApiList)
        rv.layoutManager = LinearLayoutManager(this)
        val methods = MdmSDKManager.getAllApiMethods()

        rv.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            override fun onCreateViewHolder(p0: ViewGroup, p1: Int) = object : RecyclerView.ViewHolder(
                TextView(p0.context).apply {
                    layoutParams = ViewGroup.LayoutParams(-1, -2)
                    setPadding(50, 40, 50, 40)
                    textSize = 16f
                }
            ) {}

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, pos: Int) {
                val method = methods[pos]
                (holder.itemView as TextView).text = "${pos + 1}. ${method.name}"
                holder.itemView.setOnClickListener {
                    val intent = Intent(this@MdmMainActivity, MdmTestActivity::class.java)
                    // 传递方法名作为标识
                    intent.putExtra("METHOD_NAME", method.name)
                    startActivity(intent)
                }
            }
            override fun getItemCount() = methods.size
        }
    }

    private fun showIntegrationDialog() {
        AlertDialog.Builder(this)
            .setTitle("How To Integrate?")
            .setMessage("1. Import the jar file.\n2. Call `CustomAPI.init(context)` in  onCreate() method.\n3.Call any API you want.\n4. Call `CustomAPI.release()` in onDestroy().")
            .setPositiveButton("确定", null)
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        // 3. 释放资源
        MdmSDKManager.release()
    }


    // 内部类 Adapter
    class ApiAdapter(
        private val originalList: List<Method>,
        private val onClick: (Method) -> Unit
    ) : RecyclerView.Adapter<ApiAdapter.VH>() {

        // 这里必须使用可变列表，并且初始值是原始列表的副本
        private var displayList: MutableList<Method> = originalList.toMutableList()

        class VH(val tv: TextView) : RecyclerView.ViewHolder(tv)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val tv = TextView(parent.context).apply {
                layoutParams = ViewGroup.LayoutParams(-1, -2)
                setPadding(50, 40, 50, 40)
                textSize = 16f
                // 增加点击波纹效果
                val attrs = intArrayOf(android.R.attr.selectableItemBackground)
                val typedArray = context.obtainStyledAttributes(attrs)
                val backgroundResource = typedArray.getResourceId(0, 0)
                setBackgroundResource(backgroundResource)
                typedArray.recycle()
            }
            return VH(tv)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val method = displayList[position]
            holder.tv.text = "${position  + 1}. ${method.name}"
            holder.itemView.setOnClickListener { onClick(method) }
        }

        override fun getItemCount() = displayList.size

        // 核心过滤函数：确保每次搜索都是基于 originalList 进行筛选
        fun filter(query: String) {
            val filterPattern = query.lowercase().trim()

            displayList = if (filterPattern.isEmpty()) {
                originalList.toMutableList()
            } else {
                originalList.filter {
                    it.name.lowercase().contains(filterPattern)
                }.toMutableList()
            }

            // 必须通知观察者数据已改变
            notifyDataSetChanged()
        }
    }
}