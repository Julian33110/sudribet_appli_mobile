package com.example.sudribet

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.random.Random

object BetResolver {

    fun resolveAll(context: Context) {
        val prefs = context.getSharedPreferences("SudriPrefs", Context.MODE_PRIVATE)
        val betsJson = prefs.getString("my_bets", "[]")
        val type = object : TypeToken<MutableList<Bet>>() {}.type
        val bets: MutableList<Bet> = Gson().fromJson(betsJson, type)

        var balance = prefs.getFloat("balance", 150f)
        var anyResolved = false

        bets.forEachIndexed { index, bet ->
            if (bet.status == "En cours") {
                // Probabilité de gagner inversement proportionnelle à la cote
                val winProbability = 1.0 / bet.totalCote
                val won = Random.nextDouble() < winProbability

                val newStatus = if (won) "Gagné" else "Perdu"
                bets[index] = bet.copy(status = newStatus)

                if (won) {
                    balance += bet.gainsPotentiels.toFloat()
                    LocalNotificationHelper.showGoalNotification(
                        context,
                        "Pari gagné ! 🎉",
                        "+${bet.gainsPotentiels.toInt()} SC sur ${bet.description.take(30)}"
                    )
                } else {
                    LocalNotificationHelper.showGoalNotification(
                        context,
                        "Pari perdu",
                        "${bet.description.take(30)} — Pas de chance cette fois"
                    )
                }
                anyResolved = true
            }
        }

        if (anyResolved) {
            prefs.edit()
                .putString("my_bets", Gson().toJson(bets))
                .putFloat("balance", balance)
                .apply()
        }
    }
}
