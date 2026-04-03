package com.example.sudribet

import android.content.Context
import java.text.SimpleDateFormat
import java.util.*

object DailyManager {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    fun checkDailyRecharge(context: Context): Int {
        val prefs = context.getSharedPreferences("SudriPrefs", Context.MODE_PRIVATE)
        val today = dateFormat.format(Date())
        val lastRecharge = prefs.getString("last_recharge_date", "")

        if (lastRecharge == today) return 0

        var balance = prefs.getFloat("balance", 150f)
        var credited = 0

        if (balance < 50f) {
            balance += 150f
            credited = 150
        }

        prefs.edit()
            .putString("last_recharge_date", today)
            .putFloat("balance", balance)
            .apply()

        return credited
    }

    fun checkDailyBonus(context: Context): Int {
        val prefs = context.getSharedPreferences("SudriPrefs", Context.MODE_PRIVATE)
        val today = dateFormat.format(Date())
        val lastBonus = prefs.getString("last_bonus_date", "")

        if (lastBonus == today) return 0

        prefs.edit().putString("last_bonus_date", today).apply()

        // 20% de chance de jackpot quotidien (25 SC)
        return if (Math.random() < 0.20) 25 else 0
    }

    fun getMissions(context: Context): List<DailyMission> {
        val prefs = context.getSharedPreferences("SudriPrefs", Context.MODE_PRIVATE)
        val betsJson = prefs.getString("my_bets", "[]")
        val today = dateFormat.format(Date())

        val betsToday = try {
            val type = com.google.gson.reflect.TypeToken.getParameterized(List::class.java, Bet::class.java).type
            (com.google.gson.Gson().fromJson<List<Bet>>(betsJson, type) ?: emptyList())
                .filter { it.date.isNotEmpty() }
        } catch (e: Exception) { emptyList() }

        val betsPlacedToday = prefs.getInt("bets_today_$today", 0)
        val winsToday = betsToday.filter { it.status == "Gagné" }.size
        val hasLiveBet = betsToday.any { it.description.contains("(", ignoreCase = true) }

        return listOf(
            DailyMission("Place 3 paris aujourd'hui", betsPlacedToday, 3, 20, betsPlacedToday >= 3),
            DailyMission("Gagne un pari", winsToday, 1, 30, winsToday >= 1),
            DailyMission("Parie sur un match ESME", if (hasLiveBet) 1 else 0, 1, 15, hasLiveBet)
        )
    }

    fun incrementBetsToday(context: Context) {
        val prefs = context.getSharedPreferences("SudriPrefs", Context.MODE_PRIVATE)
        val today = dateFormat.format(Date())
        val current = prefs.getInt("bets_today_$today", 0)
        prefs.edit().putInt("bets_today_$today", current + 1).apply()
    }

    fun claimMissionReward(context: Context, missionIndex: Int, reward: Int) {
        val prefs = context.getSharedPreferences("SudriPrefs", Context.MODE_PRIVATE)
        val today = dateFormat.format(Date())
        val key = "mission_claimed_${today}_$missionIndex"
        if (prefs.getBoolean(key, false)) return

        val balance = prefs.getFloat("balance", 150f)
        prefs.edit()
            .putFloat("balance", balance + reward)
            .putBoolean(key, true)
            .apply()
    }

    fun isMissionClaimed(context: Context, missionIndex: Int): Boolean {
        val prefs = context.getSharedPreferences("SudriPrefs", Context.MODE_PRIVATE)
        val today = dateFormat.format(Date())
        return prefs.getBoolean("mission_claimed_${today}_$missionIndex", false)
    }
}

data class DailyMission(
    val title: String,
    val progress: Int,
    val goal: Int,
    val reward: Int,
    val completed: Boolean
)
