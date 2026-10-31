package com.example.mobiledev

import android.app.Application
import android.content.Context

class MobileApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }

    companion object {
        private lateinit var appContext: Context

        fun mobileApplicationContext(): Context {
            return appContext
        }
    }
}