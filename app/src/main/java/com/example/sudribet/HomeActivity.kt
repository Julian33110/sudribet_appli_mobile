package com.example.sudribet

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.imageview.ShapeableImageView
import android.os.Handler
import android.os.Looper
import java.io.File

class HomeActivity : AppCompatActivity() {

    private lateinit var tvBalance: TextView
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        tvBalance = findViewById(R.id.tvHomeBalance)
        val btnProfil = findViewById<ShapeableImageView>(R.id.btnGoProfil)
        val btnClaimBonus = findViewById<ImageView>(R.id.btnClaimBonus)
        val cardBotWidget = findViewById<View>(R.id.cardBotWidget)
        val cardLeaderboard = findViewById<View>(R.id.cardLeaderboard)
        val tvMission1 = findViewById<TextView>(R.id.tvMission1)
        val tvMission2 = findViewById<TextView>(R.id.tvMission2)
        val tvMission3 = findViewById<TextView>(R.id.tvMission3)
        val tvGreeting = findViewById<TextView>(R.id.tvGreeting)
        val btnQuickBet = findViewById<TextView>(R.id.btnQuickBet)
        val btnQuickHistory = findViewById<TextView>(R.id.btnQuickHistory)

        // Résoudre les paris en cours au démarrage (vérifie les vrais scores API)
        lifecycleScope.launch {
            try { BetResolver.resolveAll(this@HomeActivity) } catch (e: Exception) { e.printStackTrace() }
        }

        // Recharge quotidienne
        val recharged = DailyManager.checkDailyRecharge(this)
        if (recharged > 0) {
            Toast.makeText(this, "Recharge quotidienne : +$recharged SC crédités !", Toast.LENGTH_LONG).show()
        }

        updateBalance()
        updateMissions(tvMission1, tvMission2, tvMission3)

        // Salutation personnalisée
        val prefs = getSharedPreferences("SudriPrefs", Context.MODE_PRIVATE)
        val username = prefs.getString("username", "Parieur")
        tvGreeting.text = "Bonjour, $username 👋"

        btnProfil.setOnClickListener {
            ActivityTransitions.navigateTab(this, Intent(this, ProfilActivity::class.java))
        }

        btnQuickBet.setOnClickListener {
            ActivityTransitions.navigateTab(this, Intent(this, MainActivity::class.java))
        }

        btnQuickHistory.setOnClickListener {
            ActivityTransitions.navigateTab(this, Intent(this, HistoryActivity::class.java))
        }

        btnClaimBonus.setOnClickListener {
            val bonus = DailyManager.checkDailyBonus(this)
            if (bonus > 0) {
                val prefs = getSharedPreferences("SudriPrefs", Context.MODE_PRIVATE)
                val balance = prefs.getFloat("balance", 150f)
                prefs.edit().putFloat("balance", balance + bonus).apply()
                updateBalance()
                Toast.makeText(this, "🎉 Jackpot ! +$bonus SC gagnés !", Toast.LENGTH_LONG).show()
            } else {
                val prefs = getSharedPreferences("SudriPrefs", Context.MODE_PRIVATE)
                val balance = prefs.getFloat("balance", 150f) + 5f
                prefs.edit().putFloat("balance", balance).apply()
                updateBalance()
                Toast.makeText(this, "Bonus de 5 SC crédité !", Toast.LENGTH_SHORT).show()
            }
        }

        cardBotWidget.setOnClickListener {
            ActivityTransitions.navigate(this, Intent(this, ChatActivity::class.java))
        }

        cardLeaderboard.setOnClickListener {
            ActivityTransitions.navigate(this, Intent(this, LeaderboardActivity::class.java))
        }

        // Demander permission notifications (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1001)
            }
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_home
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_bet -> { ActivityTransitions.navigateTab(this, Intent(this, MainActivity::class.java)); true }
                R.id.nav_history -> { ActivityTransitions.navigateTab(this, Intent(this, HistoryActivity::class.java)); true }
                R.id.nav_profil -> { ActivityTransitions.navigateTab(this, Intent(this, ProfilActivity::class.java)); true }
                else -> false
            }
        }
    }

    private fun updateMissions(tv1: TextView?, tv2: TextView?, tv3: TextView?) {
        try {
            val missions = DailyManager.getMissions(this)
            val views = listOf(tv1, tv2, tv3)
            missions.forEachIndexed { i, m ->
                val status = if (m.completed) "[OK]" else "${m.progress}/${m.goal}"
                views[i]?.text = "${m.title} +${m.reward} SC [$status]"
                views[i]?.alpha = if (m.completed) 0.5f else 1f
                if (m.completed && !DailyManager.isMissionClaimed(this, i)) {
                    DailyManager.claimMissionReward(this, i, m.reward)
                    updateBalance()
                }
            }
        } catch (e: Exception) { e.printStackTrace() }
    }



    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }

    private fun updateBalance() {
        val prefs = getSharedPreferences("SudriPrefs", Context.MODE_PRIVATE)
        val balance = prefs.getFloat("balance", 150f)
        tvBalance.text = "${balance.toInt()} SC"
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            try { BetResolver.resolveAll(this@HomeActivity) } catch (e: Exception) { e.printStackTrace() }
        }
        updateBalance()
        loadAvatar()
        findViewById<BottomNavigationView>(R.id.bottomNav)?.selectedItemId = R.id.nav_home
    }

    private fun loadAvatar() {
        val path = getSharedPreferences("SudriPrefs", Context.MODE_PRIVATE)
            .getString("avatar_path", null) ?: return
        val file = File(path)
        if (file.exists()) {
            findViewById<ShapeableImageView>(R.id.btnGoProfil)?.setImageURI(Uri.fromFile(file))
        }
    }
}
