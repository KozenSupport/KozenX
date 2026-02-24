package com.kozen.support.x.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kozen.support.x.R
import com.kozen.support.x.config.LoginConfig
import com.kozen.support.x.utils.CommonTools

/**
 * LoginActivity
 * Simple login page using static credentials from config.
 */
class LoginActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        val progressOverlay = findViewById<View>(R.id.progressOverlay_login)
//        val emailInput = findViewById<EditText>(R.id.etEmail)
        val passwordInput = findViewById<EditText>(R.id.etPassword)
        val loginBtn = findViewById<Button>(R.id.btnLogin)
//        val forgotPwd = findViewById<TextView>(R.id.tvForgot)
//        var register = findViewById<TextView>(R.id.tvRegister)
        loginBtn.setOnClickListener {
//            val email = emailInput.text.toString().trim()
            progressOverlay.visibility = View.VISIBLE
            val password = passwordInput.text.toString().trim()
            if (password == LoginConfig.DEMO_PASSWORD){
                Handler(Looper.getMainLooper()).postDelayed({
                    progressOverlay.visibility = View.GONE
                    startActivity(Intent(this, SdkMenuActivity::class.java))
                    Toast.makeText(this,"Login Success!", Toast.LENGTH_SHORT).show()
                    finish()
                }, 800)
            }else{
                progressOverlay.visibility = View.GONE
                CommonTools.showMethodDialog(this,"Ops!","Wrong Passcode!")
            }
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

//            startActivity(Intent(this, SdkMenuActivity::class.java))
        }


//        register.setOnClickListener {
//            startActivity(Intent(this, RegisterActivity::class.java))
//        }

//        forgotPwd.setOnClickListener {
//            CommonTools.showMethodDialog(
//                this,
//                "😵Sorry",
//                "Password recovery is not supported for now. Please contact Kozen support."
//            )
//        }
    }
}