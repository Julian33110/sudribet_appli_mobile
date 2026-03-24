package com.example.sudribet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class ProfilActivity : AppCompatActivity() {

    private lateinit var tvUsername: TextView
    private lateinit var tvBalance: TextView
    private lateinit var tvWins: TextView
    private lateinit var tvLosses: TextView
    private lateinit var tvWinRate: TextView
    private lateinit var tvActiveBets: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil)

        tvUsername = findViewById(R.id.tvUsername)
        tvBalance = findViewById(R.id.tvBalance)
        tvWins = findViewById(R.id.tvWins)
        tvLosses = findViewById(R.id.tvLosses)
        tvWinRate = findViewById(R.id.tvWinRate)
        tvActiveBets = findViewById(R.id.tvActiveBets)

        val btnBack = findViewById<ImageView>(R.id.ivBack)
        val btnEdit = findViewById<Button>(R.id.btnEditProfil)
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        val switchDarkMode = findViewById<SwitchMaterial>(R.id.switchDarkMode)

        updateUI()

        // Dark Mode toggle
        val prefs = getSharedPreferences("SudriPrefs", Context.MODE_PRIVATE)
        val isDark = prefs.getBoolean("dark_mode", false)
        switchDarkMode.isChecked = isDark
        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("dark_mode", isChecked).apply()
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        btnBack.setOnClickListener { ActivityTransitions.navigateBack(this) }

        btnEdit.setOnClickListener {
            ActivityTransitions.navigate(this, Intent(this, EditProfilActivity::class.java))
        }

        btnLogout.setOnClickListener {
            ActivityTransitions.navigateAndClear(this, Intent(this, LoginActivity::class.java))
        }

        // Bottom Navigation
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_profil
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    ActivityTransitions.navigateTab(this, Intent(this, HomeActivity::class.java))
                    true
                }
                R.id.nav_bet -> {
                    ActivityTransitions.navigateTab(this, Intent(this, MainActivity::class.java))
                    true
                }
                R.id.nav_history -> {
                    ActivityTransitions.navigateTab(this, Intent(this, HistoryActivity::class.java))
                    true
                }
                R.id.nav_profil -> true // déjà ici
                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateUI()
        // Restaurer l'état du switch
        val prefs = getSharedPreferences("SudriPrefs", Context.MODE_PRIVATE)
        findViewById<SwitchMaterial>(R.id.switchDarkMode)?.isChecked = prefs.getBoolean("dark_mode", false)
        findViewById<BottomNavigationView>(R.id.bottomNav)?.selectedItemId = R.id.nav_profil
    }

    private fun updateUI() {
        val sharedPref = getSharedPreferences("SudriPrefs", Context.MODE_PRIVATE)

        val name = sharedPref.getString("username", "Utilisateur Sudri")
        val balance = sharedPref.getFloat("balance", 150.0f)
        tvUsername.text = name
        tvBalance.text = String.format(Locale.US, "%.2f €", balance)

        val betsJson = sharedPref.getString("my_bets", "[]")
        val type = object : TypeToken<List<Bet>>() {}.type
        val betsList: List<Bet> = Gson().fromJson(betsJson, type)

        val activeCount = betsList.count { it.status == "En cours" }
        val winsCount = betsList.count { it.status == "Gagné" }
        val lossesCount = betsList.count { it.status == "Perdu" }

        tvActiveBets.text = activeCount.toString()
        tvWins.text = winsCount.toString()
        tvLosses.text = lossesCount.toString()

        val totalFinished = winsCount + lossesCount
        if (totalFinished > 0) {
            val rate = (winsCount.toDouble() / totalFinished.toDouble()) * 100
            tvWinRate.text = String.format(Locale.US, "%.1f%%", rate)
        } else {
            tvWinRate.text = "0%"
        }
    }
}