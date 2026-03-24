package com.example.sudribet

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

class SudribetApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Restaurer le mode sombre au démarrage de l'app
        val prefs = getSharedPreferences("SudriPrefs", Context.MODE_PRIVATE)
        val isDark = prefs.getBoolean("dark_mode", false)
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}
