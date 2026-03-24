package com.example.sudribet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.imageview.ShapeableImageView

class HomeActivity : AppCompatActivity() {

    private lateinit var tvBalance: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        tvBalance = findViewById(R.id.tvHomeBalance)
        val btnProfil = findViewById<ShapeableImageView>(R.id.btnGoProfil)
        val btnClaimBonus = findViewById<ImageView>(R.id.btnClaimBonus)
        val lottieConfetti = findViewById<LottieAnimationView>(R.id.lottieConfetti)
        val cardBotWidget = findViewById<CardView>(R.id.cardBotWidget)

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
            android.widget.Toast.makeText(this, "Bonus de 5€ crédité !", android.widget.Toast.LENGTH_SHORT).show()
            
            lottieConfetti.playAnimation()
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