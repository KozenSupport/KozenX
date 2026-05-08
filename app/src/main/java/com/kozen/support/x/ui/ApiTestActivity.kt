package com.kozen.support.x.ui

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.kozen.support.x.R
import com.kozen.support.x.model.MethodInfo
import com.kozen.support.x.utils.CommonTools
import com.kozen.support.x.utils.SdkManager
import java.lang.reflect.Method


class ApiTestActivity : LocalizedAppCompatActivity() {

    private val LOG_TAG = "API TEST"
    private lateinit var currentMethod: Method
    private val editTexts = mutableListOf<EditText>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.api_test_activity)
        getMethodInfo()
        initView()
    }

    private fun getMethodInfo(){
        val methodInfo = intent.getParcelableExtra<MethodInfo?>("METHOD_INFO")
        try {
            val method = methodInfo!!.toMethod()
            // 如果需要访问私有方法，可以设置：
            method.isAccessible = true
            // 现在你可以使用这个 method 对象进行反射操作了
            currentMethod = method
        } catch (e: Exception) {
            Log.e(LOG_TAG,"Get Method Info Error",e)
        }
    }
    private fun initView() {
        editTexts.clear()
        val tvInfo = findViewById<TextView>(R.id.tvApiInfo)
        val container = findViewById<LinearLayout>(R.id.containerInputs)
        val btnRun = findViewById<Button>(R.id.btnRunTest)
        val tvResult = findViewById<TextView>(R.id.tvResult)
        val progressOverlay = findViewById<View>(R.id.progressOverlay)
        // 展示接口详情
        val paramsInfo = currentMethod.parameterTypes.joinToString { it.simpleName }
        tvInfo.text = getString(
            R.string.api_method_info,
            currentMethod.name,
            currentMethod.returnType.simpleName,
            paramsInfo
        )

        // 动态生成输入框
        currentMethod.parameterTypes.forEachIndexed { index, type ->
            if (View::class.java.isAssignableFrom(type) || type.isInterface) {
                val tv = TextView(this).apply {
                    text = getString(R.string.api_auto_parameter, type.simpleName)
                    setTextColor(Color.BLUE)
                    setPadding(0, 20, 0, 10)
                }
                container.addView(tv)
                editTexts.add(EditText(this).apply { visibility = View.GONE }) // 占位
            }else{
                val et = EditText(this).apply {
                    hint = getString(R.string.api_input_hint, type.simpleName)
                    layoutParams = LinearLayout.LayoutParams(-1, -2)
                }
                editTexts.add(et)
                container.addView(et)
            }
        }

        btnRun.setOnClickListener {
            progressOverlay.visibility = View.VISIBLE
            try {
                tvResult.text = getString(R.string.api_calling)
                // 1. 转换参数
                val args = editTexts.mapIndexed { index, editText ->
                    castValue(editText.text.toString(), currentMethod.parameterTypes[index])
                }.toTypedArray()
                // 2. 确定调用主体 (Receiver)
                val isStatic = java.lang.reflect.Modifier.isStatic(currentMethod.modifiers)
                val receiver = if (isStatic) null else SdkManager.getProviderInstance(currentMethod.toString())

                if (!isStatic && receiver == null) {
                    CommonTools.showMethodDialog(
                        this,
                        getString(R.string.common_error),
                        getString(R.string.api_failed_sdk_instance)
                    )
                    return@setOnClickListener
                }
                // 3. 执行调用
                val result = if (args.isEmpty()) {
                    currentMethod.invoke(receiver)
                } else {
                    currentMethod.invoke(receiver, *args)
                }
                // 模拟耗时任务
                Handler(Looper.getMainLooper()).postDelayed({
                    progressOverlay.visibility = View.GONE
                    tvResult.text = getString(
                        R.string.api_return_result_value,
                        com.google.gson.Gson().toJson(result) ?: "void/null"
                    )
                }, 1000)
            } catch (e: Exception) {
                tvResult.text = getString(R.string.api_execute_error, e.message.orEmpty())
                progressOverlay.visibility = View.GONE
                Log.e(LOG_TAG,"Execute Method Error",e)
                val errorMsg = e.cause?.toString() ?: e.toString()
                CommonTools.showMethodDialog(this, getString(R.string.api_execute_error, ""), errorMsg)
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
                Log.e(LOG_TAG,"cast View type parameter error: ",e)
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
                    CommonTools.showMethodDialog(
                        this,
                        getString(R.string.api_json_warning_title),
                        getString(R.string.api_json_warning_message)
                    )
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
            val size = (resources.displayMetrics.density * 2000).toInt() // 200dp
            val params = FrameLayout.LayoutParams(size, size).apply {
                gravity = Gravity.CENTER // 居中显示
            }

            runOnUiThread {
                // 获取系统的根布局容器（每个 Activity 都有）
                val root = findViewById<FrameLayout>(android.R.id.content)

                // 为了防止多次调用产生堆叠，先移除之前的测试 View（可选）
                root.findViewWithTag<View>("SDK_TEST_VIEW")?.let { root.removeView(it) }

                view.tag = "SDK_TEST_VIEW"
                root.addView(view, params)

                // 点击这个 View 自动移除自己，防止挡住 UI
                view.setOnClickListener {
                    (it.parent as? ViewGroup)?.removeView(it)
                    Toast.makeText(this, getString(R.string.api_test_view_removed), Toast.LENGTH_SHORT).show()
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
                getString(R.string.api_callback_param, i, arg?.toString() ?: "null")
            }?.joinToString("\n") ?: getString(R.string.api_no_params)

            // 2. 在主线程弹出 Toast 或更新 UI
            runOnUiThread {
                android.app.AlertDialog.Builder(this)
                    .setTitle(getString(R.string.api_callback_title, serviceInterface.simpleName))
                    .setMessage(getString(R.string.api_callback_message, method.name, argsDetails))
                    .setPositiveButton(getString(R.string.common_ok), null)
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
