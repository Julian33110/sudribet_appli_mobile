package com.example.sudribet

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.imageview.ShapeableImageView

import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast

class HomeActivity : AppCompatActivity() {

    private lateinit var tvBalance: TextView
    private val handler = Handler(Looper.getMainLooper())
    private val goalSimulation = object : Runnable {
        override fun run() {
            LocalNotificationHelper.showGoalNotification(
                this@HomeActivity, 
                "ESME vs EPITA", 
                "1 - 0"
            )
            handler.postDelayed(this, 60000) // Simuler toutes les minutes
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        tvBalance = findViewById(R.id.tvHomeBalance)
        val btnProfil = findViewById<ShapeableImageView>(R.id.btnGoProfil)
        val btnClaimBonus = findViewById<ImageView>(R.id.btnClaimBonus)
        val cardBotWidget = findViewById<View>(R.id.cardBotWidget)

        updateBalance()

        btnProfil.setOnClickListener {
            ActivityTransitions.navigateTab(this, Intent(this, ProfilActivity::class.java))
        }

        btnClaimBonus.setOnClickListener {
            val sharedPref = getSharedPreferences("SudriPrefs", Context.MODE_PRIVATE)
            var balance = sharedPref.getFloat("balance", 150.0f)
            balance += 5.0f
            sharedPref.edit().putFloat("balance", balance).apply()
            updateBalance()
            Toast.makeText(this, "Bonus de 5€ crédité !", Toast.LENGTH_SHORT).show()
        }

        cardBotWidget.setOnClickListener {
            ActivityTransitions.navigate(this, Intent(this, ChatActivity::class.java))
        }

        // Bottom Navigation
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_home
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true // déjà ici
                R.id.nav_bet -> {
                    ActivityTransitions.navigateTab(this, Intent(this, MainActivity::class.java))
                    true
                }
                R.id.nav_history -> {
                    ActivityTransitions.navigateTab(this, Intent(this, HistoryActivity::class.java))
                    true
                }
                R.id.nav_profil -> {
                    ActivityTransitions.navigateTab(this, Intent(this, ProfilActivity::class.java))
                    true
                }
                else -> false
            }
        }

        // Demander la permission de notifications (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1001)
            }
        }

        // Lancer la simulation de buts
        handler.postDelayed(goalSimulation, 15000)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(goalSimulation)
    }

    private fun updateBalance() {
        val sharedPref = getSharedPreferences("SudriPrefs", Context.MODE_PRIVATE)
        val balance = sharedPref.getFloat("balance", 150.0f)
        tvBalance.text = String.format("%.2f €", balance)
    }

    override fun onResume() {
        super.onResume()
        updateBalance()
        // Re-sélectionner l'item home au retour
        findViewById<BottomNavigationView>(R.id.bottomNav)?.selectedItemId = R.id.nav_home
    }
}
