package com.kozen.support.x.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.kozen.support.x.R
import com.kozen.support.x.utils.CommonTools

/**
 * LoginActivity
 * Simple login page using static credentials from config.
 */
class LoginActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        val emailInput = findViewById<EditText>(R.id.etEmail)
        val passwordInput = findViewById<EditText>(R.id.etPassword)
        val loginBtn = findViewById<Button>(R.id.btnLogin)
        val forgotPwd = findViewById<TextView>(R.id.tvForgot)
        var register = findViewById<TextView>(R.id.tvRegister)
        loginBtn.setOnClickListener {
//            val email = emailInput.text.toString().trim()
//            val password = passwordInput.text.toString().trim()
//
//
//            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//                Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//
//
//            if (password.length < 8) {
//                Toast.makeText(this, "Password must be at least 8 characters", Toast.LENGTH_SHORT)
//                    .show()
//                return@setOnClickListener
//            }
//
//
//            if (email == LoginConfig.DEMO_EMAIL && password == LoginConfig.DEMO_PASSWORD) {
//            // Login success, navigate to test center
//                startActivity(Intent(this, MainPage::class.java))
//                finish()
//            } else {
//                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
//            }

            startActivity(Intent(this, SdkMenuActivity::class.java))
        }


        register.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        forgotPwd.setOnClickListener {
            CommonTools.showMethodDialog(
                this,
                "😵Sorry",
                "Password recovery is not supported for now. Please contact Kozen support."
            )
        }
    }
}