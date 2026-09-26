package com.example.mobiledev

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mobiledev.StartActivity.Companion.USERNAME

class ContentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_content)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.content))
        { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        showAttention(intent)

        val btnBack : Button = findViewById(R.id.btn_back)
        btnBack.setOnClickListener {
            dropActivity()
        }
    }

    private fun dropActivity() {
        finish()
    }

    private fun showAttention(data : Intent?) {
        val name = data?.getStringExtra(USERNAME)
        var txt = ""
        if (name.isNullOrEmpty()) {
            txt = "Пони не готовы с вами знакомиться, так как они не знают вашего имени. Вы что, оборотень?"
        } else {
            txt = "Пони пока что не прибыли в Эквестрию. Присядьте на лавку..."
            showPonies()
        }
        val attention : TextView = findViewById(R.id.attention)
        attention.text = txt
    }

    private fun showPonies() {

    }
}