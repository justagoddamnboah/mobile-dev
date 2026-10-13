package com.example.mobiledev.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mobiledev.R

class StartActivity : BaseActivity() {
    companion object {
        const val USERNAME: String = "USERNAME"
        const val PASSWORD: String = "PASSWORD"
    }

    private var currentUsername: String? = null
    lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_start)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.start)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (savedInstanceState != null) {
            currentUsername = savedInstanceState.getString(USERNAME)
            updateGreeting()
        }

        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                useResult(data)
            }
        }

        findViewById<Button>(R.id.btn_to_register).setOnClickListener {
            openRegistration()
        }
        findViewById<Button>(R.id.btn_to_content).setOnClickListener {
            openContent()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(USERNAME, currentUsername)
    }

    private fun openRegistration() {
        val intent = Intent(this, RegistrationActivity::class.java)
        resultLauncher.launch(intent)
    }

    private fun openContent() {
        val intent = Intent(this, ContentActivity::class.java)
        intent.putExtra(USERNAME, currentUsername)
        startActivity(intent)
    }

    private fun useResult(data: Intent?) {
        if (data != null && data.hasExtra(USERNAME)) {
            currentUsername = data.getStringExtra(USERNAME)
            updateGreeting()
        }
    }

    private fun updateGreeting() {
        val greeting: TextView = findViewById(R.id.pony_quote)
        if (currentUsername != null) {
            greeting.text = "Привет, $currentUsername!"
        } else {
            greeting.text = "Добро пожаловать в Эквестрию! Здесь ты можешь познакомиться со всеми жителями!"
        }
    }
}