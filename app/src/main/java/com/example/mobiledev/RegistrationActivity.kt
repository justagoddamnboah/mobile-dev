package com.example.mobiledev

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar

class RegistrationActivity : AppCompatActivity() {
    lateinit var username : EditText
    lateinit var password : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_registration)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.registration))
        { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        username = findViewById(R.id.username)
        password = findViewById(R.id.password)

        findViewById<Button>(R.id.btn_to_start).setOnClickListener {
            registerProcess()
        }

        findViewById<Button>(R.id.btn_back).setOnClickListener {
            finish()
        }
    }

    private fun registerProcess() {
        val intent = Intent()

        if (!username.text.isNullOrBlank()) {
            intent.putExtra(StartActivity.USERNAME, username.text.toString())
        } else {
            Snackbar.make(findViewById(R.id.registration), "Введи имя", Snackbar.LENGTH_LONG).show()
            return
        }

        if (password.text.length >= 4) {
            intent.putExtra(StartActivity.PASSWORD, password.text.toString())
        } else {
            Snackbar.make(findViewById(R.id.registration), "Пароль не должен быть меньше 4 символов", Snackbar.LENGTH_LONG).show()
            return
        }

        setResult(RESULT_OK, intent)
        finish()
    }
}