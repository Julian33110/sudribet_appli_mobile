package com.example.sudribet

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.util.*

class ProfilActivity : AppCompatActivity() {

    private lateinit var tvUsername: TextView
    private lateinit var tvBalance: TextView
    private lateinit var tvWins: TextView
    private lateinit var tvLosses: TextView
    private lateinit var tvWinRate: TextView
    private lateinit var tvActiveBets: TextView
    private lateinit var tvLevel: TextView
    private lateinit var pbXP: ProgressBar
    private lateinit var tvStreakText: TextView
    private lateinit var badgeContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil)

        tvUsername = findViewById(R.id.tvUsername)
        tvBalance = findViewById(R.id.tvBalance)
        tvWins = findViewById(R.id.tvWins)
        tvLosses = findViewById(R.id.tvLosses)
        tvWinRate = findViewById(R.id.tvWinRate)
        tvActiveBets = findViewById(R.id.tvActiveBets)
        tvLevel = findViewById(R.id.tvLevel)
        pbXP = findViewById(R.id.pbXP)
        tvStreakText = findViewById(R.id.tvStreakText)
        badgeContainer = findViewById(R.id.badgeContainer) ?: return

        val btnBack = findViewById<ImageView>(R.id.ivBack)
        val btnEdit = findViewById<Button>(R.id.btnEditProfil)
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        val switchDarkMode = findViewById<SwitchMaterial>(R.id.switchDarkMode)

        updateUI()
        loadAvatar()

        val prefs = getSharedPreferences("SudriPrefs", Context.MODE_PRIVATE)
        val isDark = prefs.getBoolean("dark_mode", false)
        switchDarkMode.isChecked = isDark
        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("dark_mode", isChecked).apply()
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        btnBack.setOnClickListener { ActivityTransitions.navigateBack(this) }
        btnEdit.setOnClickListener { ActivityTransitions.navigate(this, Intent(this, EditProfilActivity::class.java)) }
        btnLogout.setOnClickListener {
            prefs.edit().remove("email").apply()
            ActivityTransitions.navigateAndClear(this, Intent(this, LoginActivity::class.java))
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_profil
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> { ActivityTransitions.navigateTab(this, Intent(this, HomeActivity::class.java)); true }
                R.id.nav_bet -> { ActivityTransitions.navigateTab(this, Intent(this, MainActivity::class.java)); true }
                R.id.nav_history -> { ActivityTransitions.navigateTab(this, Intent(this, HistoryActivity::class.java)); true }
                R.id.nav_profil -> true
                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateUI()
        loadAvatar()
        val prefs = getSharedPreferences("SudriPrefs", Context.MODE_PRIVATE)
        findViewById<SwitchMaterial>(R.id.switchDarkMode)?.isChecked = prefs.getBoolean("dark_mode", false)
        findViewById<BottomNavigationView>(R.id.bottomNav)?.selectedItemId = R.id.nav_profil
    }

    private fun loadAvatar() {
        val path = getSharedPreferences("SudriPrefs", Context.MODE_PRIVATE)
            .getString("avatar_path", null) ?: return
        val file = File(path)
        if (file.exists()) {
            findViewById<ShapeableImageView>(R.id.ivAvatar)?.setImageURI(Uri.fromFile(file))
        }
    }

    private fun updateUI() {
        val prefs = getSharedPreferences("SudriPrefs", Context.MODE_PRIVATE)
        val name = prefs.getString("username", "Parieur Sudri")
        val balance = prefs.getFloat("balance", 150f)

        tvUsername.text = name
        tvBalance.text = "${balance.toInt()} SC"

        val betsJson = prefs.getString("my_bets", "[]")
        val type = object : TypeToken<List<Bet>>() {}.type
        val betsList: List<Bet> = Gson().fromJson(betsJson, type)

        val activeCount = betsList.count { it.status == "En cours" }
        val winsCount = betsList.count { it.status == "Gagné" }
        val lossesCount = betsList.count { it.status == "Perdu" }

        tvActiveBets.text = activeCount.toString()
        tvWins.text = winsCount.toString()
        tvLosses.text = lossesCount.toString()

        val totalFinished = winsCount + lossesCount
        tvWinRate.text = if (totalFinished > 0)
            String.format(Locale.US, "%.1f%%", (winsCount.toDouble() / totalFinished) * 100)
        else "0%"

        val totalBets = betsList.size
        val totalXP = (totalBets * 50) + (winsCount * 100)
        val level = (totalXP / 500) + 1
        val progress = (totalXP % 500).toDouble() / 500.0 * 100

        val rank = when { level < 3 -> "DEBUTANT"; level < 7 -> "AMATEUR"; level < 12 -> "EXPERT"; else -> "LEGENDE" }
        tvLevel.text = "NIVEAU $level - $rank"
        pbXP.progress = progress.toInt()

        var streak = 0
        for (bet in betsList) { if (bet.status == "Gagné") streak++ else break }
        tvStreakText.text = "Serie x$streak"
        findViewById<android.view.View>(R.id.tvStreakIcon)?.alpha = if (streak > 0) 1.0f else 0.3f

        try { updateBadges(betsList, balance) } catch (e: Exception) { e.printStackTrace() }
    }

    private fun updateBadges(bets: List<Bet>, balance: Float) {
        badgeContainer.removeAllViews()
        val badges = BadgeSystem.getBadges(bets, balance)
        badges.forEach { badge ->
            val col = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(dpToPx(90), LinearLayout.LayoutParams.WRAP_CONTENT).also {
                    it.marginEnd = dpToPx(12)
                }
                alpha = if (badge.unlocked) 1f else 0.25f
            }
            val icon = TextView(this).apply {
                text = badge.emoji
                textSize = 28f
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(dpToPx(50), dpToPx(50))
                setBackgroundColor(0x11FFFFFF)
            }
            val label = TextView(this).apply {
                text = badge.name
                textSize = 9f
                gravity = Gravity.CENTER
                setTextColor(if (badge.unlocked) 0xFFFFFFFF.toInt() else 0x66FFFFFF.toInt())
                setTypeface(null, if (badge.unlocked) Typeface.BOLD else Typeface.NORMAL)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
                ).also { it.topMargin = dpToPx(4) }
            }
            col.addView(icon)
            col.addView(label)
            badgeContainer.addView(col)
        }
    }

    private fun dpToPx(dp: Int): Int = (dp * resources.displayMetrics.density).toInt()
}
