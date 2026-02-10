package com.kozen.support.x.ui

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Patterns
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.kozen.support.x.R
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var etRegisterUsername: EditText
    private lateinit var etRegisterPassword: EditText
    private lateinit var etRegisterConfirmPassword: EditText
    private lateinit var btnRegisterSubmit: Button
    private lateinit var btnBackToLogin: Button
    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // 初始化视图
        initViews()

        // 设置点击事件
        setupClickListeners()
    }

    private fun initViews() {
        etRegisterUsername = findViewById(R.id.etRegisterUsername)
        etRegisterPassword = findViewById(R.id.etRegisterPassword)
        etRegisterConfirmPassword = findViewById(R.id.etRegisterConfirmPassword)
        btnRegisterSubmit = findViewById(R.id.btnRegisterSubmit)
        btnBackToLogin = findViewById(R.id.btnBackToLogin)
    }

    private fun setupClickListeners() {
        // 返回登录页面
        btnBackToLogin.setOnClickListener {
            // 直接关闭当前页面，返回登录页面
            finish()
        }

        // 注册按钮点击事件
        btnRegisterSubmit.setOnClickListener {
            // 隐藏软键盘
            hideKeyboard()
            // 尝试注册
            attemptRegister()
        }

        // 输入框焦点变化监听（清除错误提示）
        etRegisterUsername.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                etRegisterUsername.error = null
            }
        }

        etRegisterPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                etRegisterPassword.error = null
            }
        }

        etRegisterConfirmPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                etRegisterConfirmPassword.error = null
            }
        }
    }

    private fun attemptRegister() {
        // 获取输入值
        val username = etRegisterUsername.text.toString().trim()
        val password = etRegisterPassword.text.toString().trim()
        val confirmPassword = etRegisterConfirmPassword.text.toString().trim()

        // 验证输入
        if (validateInput(username, password, confirmPassword)) {
            // 显示加载对话框
            showProgressDialog("In progress ...")

            // 模拟网络请求延迟
            Handler(Looper.getMainLooper()).postDelayed({
                // 执行注册逻辑
                performRegister(username, password)
            }, 1500)
        }
    }

    private fun validateInput(
        username: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        var isValid = true

        // 检查用户名是否为空
        if (username.isEmpty()) {
            etRegisterUsername.error = getString(R.string.register_username_empty)
            etRegisterUsername.requestFocus()
            isValid = false
        }else if (!Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
            etRegisterUsername.error = getString(R.string.register_username_format_error)
            etRegisterUsername.requestFocus()
            isValid = false
        }

        // 检查密码是否为空
        if (password.isEmpty()) {
            etRegisterPassword.error = getString(R.string.register_password_empty)
            if (isValid) {
                etRegisterPassword.requestFocus()
            }
            isValid = false
        } else if (password.length < 8) {
            // 检查密码长度
            etRegisterPassword.error = getString(R.string.register_min_password_length)
            if (isValid) {
                etRegisterPassword.requestFocus()
            }
            isValid = false
        }

        // 检查确认密码是否为空
        if (confirmPassword.isEmpty()) {
            etRegisterConfirmPassword.error = getString(R.string.register_confirm_password_empty)
            if (isValid) {
                etRegisterConfirmPassword.requestFocus()
            }
            isValid = false
        }

        // 检查两次输入的密码是否一致
        if (password.isNotEmpty() && confirmPassword.isNotEmpty() && password != confirmPassword) {
            etRegisterConfirmPassword.error = getString(R.string.register_password_not_match)
            if (isValid) {
                etRegisterConfirmPassword.requestFocus()
            }
            isValid = false
        }

        return isValid
    }

    private fun performRegister(username: String, password: String) {
        // 隐藏加载对话框
        hideProgressDialog()

        // 这里添加实际的注册逻辑
        // 例如：调用注册API、保存到本地数据库等

        // 模拟检查用户名是否已存在
        val usernameExists = checkIfUsernameExists(username)

        if (usernameExists) {
            // 用户名已存在
            showToast(getString(R.string.register_username_exists))
            etRegisterUsername.error = getString(R.string.register_username_exists)
            etRegisterUsername.requestFocus()
        } else {
            // 注册成功
            showToast(getString(R.string.register_success))

            // 将注册的用户名和密码传递回登录页面
            val resultIntent = Intent()
            resultIntent.putExtra("registered_username", username)
            resultIntent.putExtra("registered_password", password)
            setResult(RESULT_OK, resultIntent)

            // 延迟1秒后返回登录页面
            Handler(Looper.getMainLooper()).postDelayed({
                finish()
            }, 1000)
        }
    }

    private fun checkIfUsernameExists(username: String): Boolean {
        // TODO: 这里添加实际检查用户名是否存在的逻辑
        // 例如：查询数据库或调用API

        // 模拟检查，这里假设用户名为"admin"的已存在
        return username.equals("admin", ignoreCase = true)
    }

    private fun showProgressDialog(message: String) {
        progressDialog = ProgressDialog(this).apply {
            setMessage(message)
            setCancelable(false)
            show()
        }
    }

    private fun hideProgressDialog() {
        progressDialog?.dismiss()
        progressDialog = null
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun hideKeyboard() {
        val view = currentFocus
        if (view != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        hideProgressDialog()
    }
}