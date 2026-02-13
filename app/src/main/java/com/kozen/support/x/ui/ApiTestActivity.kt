package com.kozen.support.x.ui

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.kozen.support.x.R
import com.kozen.support.x.utils.CommonTools
import com.kozen.support.x.utils.SdkManager
import java.lang.reflect.Method

class ApiTestActivity : AppCompatActivity() {

    private val LOG_TAG = "API TEST";
    private lateinit var currentMethod: Method
    private val editTexts = mutableListOf<EditText>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.api_test_activity)

        val methodName = intent.getStringExtra("METHOD_NAME")
        currentMethod = SdkManager.getAllApiMethods().find { it.name == methodName } ?: return

        initView()
    }

    private fun initView() {
        editTexts.clear()
        val tvInfo = findViewById<TextView>(R.id.tvApiInfo)
        val container = findViewById<LinearLayout>(R.id.containerInputs)
        val btnRun = findViewById<Button>(R.id.btnRunTest)
        val tvResult = findViewById<TextView>(R.id.tvResult)
        // 展示接口详情
        val paramsInfo = currentMethod.parameterTypes.joinToString { it.simpleName }
        tvInfo.text = "Method : ${currentMethod.name}\nReturn Type: ${currentMethod.returnType.simpleName}\nParameter Type: [$paramsInfo]"

        // 动态生成输入框
        currentMethod.parameterTypes.forEachIndexed { index, type ->
            if (View::class.java.isAssignableFrom(type)) {
                val tv = TextView(this).apply {
                    text = "Parameter: ${type.simpleName} (Auto-created & Injected)"
                    setTextColor(Color.BLUE)
                    setPadding(0, 20, 0, 10)
                }
                container.addView(tv)
                editTexts.add(EditText(this).apply { visibility = View.GONE }) // 占位
            }else{
                val et = EditText(this).apply {
                    hint = "please input : (${type.simpleName})"
                    layoutParams = LinearLayout.LayoutParams(-1, -2)
                }
                editTexts.add(et)
                container.addView(et)
            }
        }

        btnRun.setOnClickListener {
            try {
                // 1. 转换参数
                val args = editTexts.mapIndexed { index, editText ->
                    castValue(editText.text.toString(), currentMethod.parameterTypes[index])
                }.toTypedArray()

                // 2. 确定调用主体 (Receiver)
                val isStatic = java.lang.reflect.Modifier.isStatic(currentMethod.modifiers)
                val receiver = if (isStatic) null else SdkManager.getProviderInstance(currentMethod.toString())

                if (!isStatic && receiver == null) {
                    CommonTools.showMethodDialog(this,"Error","Failed to get SDK Instance (it's a non-static method)")
                    return@setOnClickListener
                }

                // 3. 执行调用
                val result = if (args.isEmpty()) {
                    currentMethod.invoke(receiver)
                } else {
                    currentMethod.invoke(receiver, *args)
                }

                tvResult.text = "Return result:\n${com.google.gson.Gson().toJson(result)?: "void/null"}"
            } catch (e: Exception) {
                Log.e(LOG_TAG,"Execute Method Error",e)
                val errorMsg = e.cause?.toString() ?: e.toString()
                CommonTools.showMethodDialog(this,"Execute Method Error",errorMsg)
            }
        }
    }

    private fun castValue(value: String, type: Class<*>): Any? {
        if (type.isInterface) {
            return createDynamicProxy(type)
        }
        // 2. 处理 View 类型参数
        if (View::class.java.isAssignableFrom(type)) {
            return try {
                createTestView(type)
            } catch (e: Exception) {
                // 兜底：如果具体子类创建失败，直接给一个基础 View
                val baseView = View(this)
                findViewById<FrameLayout>(android.R.id.content).addView(baseView)
                baseView
            }
        }
//        if (value.isEmpty() && type != String::class.java) return null
        return when (type) {
            String::class.java -> value.trim()
            Int::class.java, Integer::class.java -> value.toInt()
            Boolean::class.java, java.lang.Boolean::class.java -> value.toBoolean()
            Long::class.java, java.lang.Long::class.java -> value.toLong()
            else ->
                try {
                    com.google.gson.Gson().fromJson(value, type)
                } catch (e: Exception) {
                    Log.e(LOG_TAG,"JSON invalid",e)
                    CommonTools.showMethodDialog(this,"Warning!","The parameter should be as JSON format!")
                }
        }
    }

    private fun createTestView(type: Class<*>): View {
        return try {
            // 反射创建 View 实例
            val constructor = type.getConstructor(Context::class.java)
            val view = constructor.newInstance(this) as View

            // 设置一个显眼的背景色和宽高，方便测试时一眼看到它
            view.setBackgroundColor(Color.parseColor("#80FF0000")) // 半透明红色
            val size = (resources.displayMetrics.density * 200).toInt() // 200dp
            val params = FrameLayout.LayoutParams(size, size).apply {
                gravity = Gravity.CENTER // 居中显示
            }

            runOnUiThread {
                // 获取系统的根布局容器（每个 Activity 都有）
                val root = findViewById<FrameLayout>(android.R.id.content)

                // 为了防止多次调用产生堆叠，先移除之前的测试 View（可选）
                // root.findViewWithTag<View>("SDK_TEST_VIEW")?.let { root.removeView(it) }

                view.tag = "SDK_TEST_VIEW"
                root.addView(view, params)

                // 点击这个 View 自动移除自己，防止挡住 UI
                view.setOnClickListener {
                    (it.parent as? ViewGroup)?.removeView(it)
                    Toast.makeText(this, "Test View Removed", Toast.LENGTH_SHORT).show()
                }
            }
            view
        } catch (e: Exception) {
            View(this)
        }
    }
    /**
     * 核心：动态代理生成回调接口实例
     */
    private fun createDynamicProxy(serviceInterface: Class<*>): Any {
        return java.lang.reflect.Proxy.newProxyInstance(
            serviceInterface.classLoader,
            arrayOf(serviceInterface)
        ) { proxy, method, args ->
            // 1. 获取回调参数详情
            val argsDetails = args?.mapIndexed { i, arg ->
                "Param[$i]: ${arg?.toString() ?: "null"}"
            }?.joinToString("\n") ?: "No params"

            // 2. 在主线程弹出 Toast 或更新 UI
            runOnUiThread {
                android.app.AlertDialog.Builder(this)
                    .setTitle("Callback: ${serviceInterface.simpleName}")
                    .setMessage("Method: ${method.name}\n\n$argsDetails")
                    .setPositiveButton("OK", null)
                    .show()
            }

            // 3. 处理基本类型返回值（防止 SDK 内部 NPE）
            when (method.returnType) {
                Boolean::class.java, java.lang.Boolean::class.javaPrimitiveType -> true
                Int::class.java, Integer::class.javaPrimitiveType -> 0
                else -> null
            }
        }
    }


}