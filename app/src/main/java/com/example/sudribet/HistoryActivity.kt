package com.example.sudribet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class HistoryActivity : AppCompatActivity() {

    private lateinit var rv: RecyclerView
    private lateinit var emptyState: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        rv = findViewById(R.id.rvHistory)
        rv.layoutManager = LinearLayoutManager(this)
        emptyState = findViewById(R.id.emptyState)

        findViewById<ImageView>(R.id.ivBack).setOnClickListener {
            ActivityTransitions.navigateBack(this)
        }

        findViewById<Button>(R.id.btnGoBet).setOnClickListener {
            ActivityTransitions.navigateTab(this, Intent(this, MainActivity::class.java))
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_history
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> { ActivityTransitions.navigateTab(this, Intent(this, HomeActivity::class.java)); true }
                R.id.nav_bet -> { ActivityTransitions.navigateTab(this, Intent(this, MainActivity::class.java)); true }
                R.id.nav_history -> true
                R.id.nav_profil -> { ActivityTransitions.navigateTab(this, Intent(this, ProfilActivity::class.java)); true }
                else -> false
            }
        }

        refreshBets()
    }

    override fun onResume() {
        super.onResume()
        refreshBets()
        findViewById<BottomNavigationView>(R.id.bottomNav)?.selectedItemId = R.id.nav_history
    }

    private fun refreshBets() {
        val sharedPref = getSharedPreferences("SudriPrefs", Context.MODE_PRIVATE)
        val betsJson = sharedPref.getString("my_bets", "[]")
        val type = object : TypeToken<List<Bet>>() {}.type
        val betsList: List<Bet> = Gson().fromJson(betsJson, type)

        rv.adapter = BetAdapter(betsList)

        if (betsList.isEmpty()) {
            rv.visibility = View.GONE
            emptyState.visibility = View.VISIBLE
        } else {
            rv.visibility = View.VISIBLE
            emptyState.visibility = View.GONE
        }
    }
}
