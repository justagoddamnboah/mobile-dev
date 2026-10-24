package com.example.mobiledev.models

import com.example.mobiledev.network.Pony

object PonyRepository {
    fun getBackupPonies(): List<Pony> {
        return listOf(
            Pony(
                id = 1,
                name = "Сумеречная Искорка",
                sex = "Female",
                kind = "Alicorn",
                imageUrl = "https://vignette.wikia.nocookie.net/mlp/images/b/bc/Princess_Twilight_Sparkle_ID_S4E26.png"
            ),
            Pony(
                id = 6,
                name = "Радуга Дэш",
                sex = "Female",
                kind = "Pegasus",
                imageUrl = "https://vignette.wikia.nocookie.net/mlp/images/4/4b/Rainbow_Dash_Wonderbolt_fantasy_cropped_S1E3.png"
            )
        )
    }
}