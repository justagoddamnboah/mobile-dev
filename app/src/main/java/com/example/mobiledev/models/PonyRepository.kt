package com.example.mobiledev.models

import com.example.mobiledev.network.Pony

object PonyRepository {
    private val ponies = mutableListOf<Pony>()

    fun addPonies(newPonies: List<Pony>) {
        ponies.addAll(newPonies)
    }

    fun setPonies(newPonies: List<Pony>) {
        ponies.clear()
        ponies.addAll(newPonies)
    }

    fun getPonies(): List<Pony> {
        return ponies.toList()
    }

    fun getBackupPonies(): List<Pony> {
        return ponies.toList()
    }

    fun clearPonies() {
        ponies.clear()
    }
}