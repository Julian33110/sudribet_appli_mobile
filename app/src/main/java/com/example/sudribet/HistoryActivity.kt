package com.example.sudribet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class HistoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        findViewById<ImageView>(R.id.ivBack).setOnClickListener {
            ActivityTransitions.navigateBack(this)
        }

        findViewById<Button>(R.id.btnGoBet).setOnClickListener {
            ActivityTransitions.navigateTab(this, Intent(this, MainActivity::class.java))
        }

        val rv = findViewById<RecyclerView>(R.id.rvHistory)
        rv.layoutManager = LinearLayoutManager(this)

        val sharedPref = getSharedPreferences("SudriPrefs", Context.MODE_PRIVATE)
        val betsJson = sharedPref.getString("my_bets", "[]")
        val type = object : TypeToken<List<Bet>>() {}.type
        val betsList: List<Bet> = Gson().fromJson(betsJson, type)

        rv.adapter = BetAdapter(betsList)

        // Bottom Navigation
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_history
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
                R.id.nav_history -> true // déjà ici
                R.id.nav_profil -> {
                    ActivityTransitions.navigateTab(this, Intent(this, ProfilActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        findViewById<BottomNavigationView>(R.id.bottomNav)?.selectedItemId = R.id.nav_history
    }
}