package com.kozen.support.x.ui

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.kozen.support.x.R
import java.lang.reflect.Method

class MdmTestActivity : AppCompatActivity() {
    private lateinit var currentMethod: Method
    private val editTexts = mutableListOf<EditText>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mdm_activity_test)

        val methodName = intent.getStringExtra("METHOD_NAME")
        currentMethod = MdmSDKManager.getAllApiMethods().find { it.name == methodName } ?: return

        initView()
    }

    private fun initView() {
        val tvInfo = findViewById<TextView>(R.id.tvApiInfo)
        val container = findViewById<LinearLayout>(R.id.containerInputs)
        val btnRun = findViewById<Button>(R.id.btnRunTest)
        val tvResult = findViewById<TextView>(R.id.tvResult)

        // 展示接口详情
        val paramsInfo = currentMethod.parameterTypes.joinToString { it.simpleName }
        tvInfo.text = "Method : ${currentMethod.name}\nReturn Type: ${currentMethod.returnType.simpleName}\nParameter Type: [$paramsInfo]"

        // 动态生成输入框
        currentMethod.parameterTypes.forEachIndexed { index, type ->
            val et = EditText(this).apply {
                hint = "please input : (${type.simpleName})"
                layoutParams = LinearLayout.LayoutParams(-1, -2)
            }
            editTexts.add(et)
            container.addView(et)
        }

        btnRun.setOnClickListener {
            try {
                // 转换参数
                val args = editTexts.mapIndexed { index, editText ->
                    castValue(editText.text.toString(), currentMethod.parameterTypes[index])
                }.toTypedArray()

                // 执行静态方法调用 (第一个参数为 null 因为是静态方法)
                val result = if (args.isEmpty()) {
                    currentMethod.invoke(null)
                } else {
                    currentMethod.invoke(null, *args)
                }

                tvResult.text = "Return result:\n${result ?: "void/null"}"
            } catch (e: Exception) {
                // 捕获反射异常并展示 Cause
                val errorMsg = e.cause?.toString() ?: e.toString()
                showErrorDialog(errorMsg)
            }
        }
    }

    private fun castValue(value: String, type: Class<*>): Any? {
        if (value.isEmpty() && type != String::class.java) return null
        return when (type) {
            String::class.java -> value
            Int::class.java, java.lang.Integer::class.java -> value.toInt()
            Boolean::class.java, java.lang.Boolean::class.java -> value.toBoolean()
            Long::class.java, java.lang.Long::class.java -> value.toLong()
            else -> value // 复杂对象建议此处扩展 JSON 解析
        }
    }

    private fun showErrorDialog(msg: String) {
        AlertDialog.Builder(this)
            .setTitle("Internal Exception")
            .setMessage(msg)
            .setPositiveButton("close", null)
            .show()
    }
}