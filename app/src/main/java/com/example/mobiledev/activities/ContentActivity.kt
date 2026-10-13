package com.example.mobiledev.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mobiledev.R
import com.example.mobiledev.activities.StartActivity.Companion.USERNAME

class ContentActivity : AppCompatActivity() {
    data class Pony(
        val name: String,
        val kind: String,
        val sex: String,
        val photoSrc: Int,
        val color: Int
    )

    private val ponies = listOf(
        Pony("Сумеречная Искорка", "Аликорн", "Женский", R.drawable.twilight_sparkle, Color.rgb(230, 210, 255)),
        Pony("Спайк", "Дракон", "Мужской", R.drawable.spike, Color.rgb(190, 235, 200)),
        Pony("Радуга Дэш", "Пегас", "Женский", R.drawable.rainbow_dash, Color.rgb(190, 225, 255)),
        Pony("Эпплджек", "Земная пони", "Женский", R.drawable.applejack, Color.rgb(255, 225, 170)),
        Pony("Рарити", "Единорог", "Женский", R.drawable.rarity, Color.rgb(230, 215, 255)),
        Pony("Флаттершай", "Пегас", "Женский", R.drawable.fluttershy, Color.rgb(255, 235, 170)),
        Pony("Пинки Пай", "Земная пони", "Женский", R.drawable.pinkie_pie, Color.rgb(255, 190, 220)),
        Pony("Эппл Блум", "Земная пони", "Женский", R.drawable.apple_bloom, Color.rgb(255, 205, 130)),
        Pony("Свити Белль", "Единорог", "Женский", R.drawable.sweetie_belle, Color.rgb(245, 225, 240)),
        Pony("Скуталу", "Пегас", "Женский", R.drawable.scootaloo, Color.rgb(255, 175, 125))
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_content)

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.content)
        ) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        showAttention(intent)

        findViewById<Button>(R.id.btn_back).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.btn_settings).setOnClickListener {
            openSettings()
        }
    }

    private fun showAttention(data: Intent?) {
        val name = data?.getStringExtra(USERNAME)
        val attention = findViewById<TextView>(R.id.attention)

        if (name.isNullOrEmpty()) {
            attention.text = "Пони не готовы с вами знакомиться, так как они не знают вашего имени. Вы что, оборотень?"
            findViewById<LinearLayout>(R.id.ponies_container).removeAllViews()
        } else {
            attention.text = "Привет, $name! Знакомься с жителями Эквестрии."
            showPonies()
        }
    }

    private fun showPonies() {
        val container = findViewById<LinearLayout>(R.id.ponies_container)
        val attention = findViewById<TextView>(R.id.attention)
        container.removeAllViews()

        if (ponies.isEmpty()) {
            attention.text = "Данные отсутствуют. Попробуйте использовать резервную копию."
            attention.visibility = View.VISIBLE
            container.visibility = View.GONE
            return
        }
        container.visibility = View.VISIBLE

        ponies.forEach { pony ->
            val card = layoutInflater.inflate(
                R.layout.pony_card,
                container,
                false
            )

            val photo = card.findViewById<ImageView>(R.id.pony_photo)
            photo.setImageResource(pony.photoSrc)
            photo.contentDescription = pony.name

            val name = card.findViewById<TextView>(R.id.pony_name)
            name.text = pony.name

            val kind = card.findViewById<TextView>(R.id.pony_kind)
            kind.text = "Вид: ${pony.kind}"

            val gender = card.findViewById<TextView>(R.id.pony_sex)
            gender.text = "Пол: ${pony.sex}"

            val colorBar = card.findViewById<View>(
                R.id.pony_color_bar
            )
            colorBar.setBackgroundColor(pony.color)

            container.addView(card)
        }
    }

    private fun openSettings() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }
}