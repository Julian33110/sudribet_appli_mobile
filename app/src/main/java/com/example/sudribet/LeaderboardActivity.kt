package com.example.sudribet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class LeaderboardEntry(val rank: Int, val name: String, val score: Int, val wins: Int, val isCurrentUser: Boolean)

class LeaderboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        findViewById<ImageView>(R.id.ivBack).setOnClickListener { finish() }

        val prefs = getSharedPreferences("SudriPrefs", Context.MODE_PRIVATE)
        val userName = prefs.getString("username", "Moi") ?: "Moi"
        val balance = prefs.getFloat("balance", 150f).toInt()

        val betsJson = prefs.getString("my_bets", "[]")
        val type = object : TypeToken<List<Bet>>() {}.type
        val bets: List<Bet> = Gson().fromJson(betsJson, type)
        val userWins = bets.count { it.status == "Gagné" }

        val fakeEntries = mutableListOf(
            LeaderboardEntry(0, "Alex M.", 1240, 18, false),
            LeaderboardEntry(0, "Sarah K.", 980, 14, false),
            LeaderboardEntry(0, "Thomas B.", 870, 12, false),
            LeaderboardEntry(0, userName, balance, userWins, true),
            LeaderboardEntry(0, "Lucas D.", 720, 9, false),
            LeaderboardEntry(0, "Emma R.", 690, 8, false),
            LeaderboardEntry(0, "Noa P.", 580, 7, false),
            LeaderboardEntry(0, "Camille F.", 510, 6, false),
            LeaderboardEntry(0, "Hugo L.", 430, 5, false),
            LeaderboardEntry(0, "Jade T.", 390, 4, false)
        )

        val sorted = fakeEntries.sortedByDescending { it.score }
            .mapIndexed { i, e -> e.copy(rank = i + 1) }

        val rv = findViewById<RecyclerView>(R.id.rvLeaderboard)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = LeaderboardAdapter(sorted)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> { ActivityTransitions.navigateTab(this, Intent(this, HomeActivity::class.java)); true }
                R.id.nav_bet -> { ActivityTransitions.navigateTab(this, Intent(this, MainActivity::class.java)); true }
                R.id.nav_history -> { ActivityTransitions.navigateTab(this, Intent(this, HistoryActivity::class.java)); true }
                R.id.nav_profil -> { ActivityTransitions.navigateTab(this, Intent(this, ProfilActivity::class.java)); true }
                else -> false
            }
        }
    }
}

class LeaderboardAdapter(private val entries: List<LeaderboardEntry>) :
    RecyclerView.Adapter<LeaderboardAdapter.VH>() {

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvRank: TextView = view.findViewById(R.id.tvRank)
        val tvName: TextView = view.findViewById(R.id.tvPlayerName)
        val tvScore: TextView = view.findViewById(R.id.tvScore)
        val tvWins: TextView = view.findViewById(R.id.tvWins)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_leaderboard, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val entry = entries[position]
        holder.tvRank.text = when (entry.rank) { 1 -> "🥇"; 2 -> "🥈"; 3 -> "🥉"; else -> "#${entry.rank}" }
        holder.tvName.text = entry.name
        holder.tvScore.text = "${entry.score} SC"
        holder.tvWins.text = "${entry.wins} victoires"

        val bg = if (entry.isCurrentUser) 0xFF1A2A4A.toInt() else 0xFF0D1B2A.toInt()
        holder.itemView.setBackgroundColor(bg)
        holder.tvName.setTypeface(null, if (entry.isCurrentUser) android.graphics.Typeface.BOLD else android.graphics.Typeface.NORMAL)
    }

    override fun getItemCount() = entries.size
}
