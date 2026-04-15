package com.example.sudribet

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object BetResolver {

    /**
     * Appelle l'API, récupère les scores réels, et résout les paris "En cours"
     * dont le match est terminé (isUpcoming = false).
     * À appeler depuis une coroutine.
     */
    suspend fun resolveAll(context: Context) {
        val prefs = context.getSharedPreferences("SudriPrefs", Context.MODE_PRIVATE)
        val betsJson = prefs.getString("my_bets", "[]")
        val type = object : TypeToken<MutableList<Bet>>() {}.type
        val bets: MutableList<Bet> = Gson().fromJson(betsJson, type)

        // On ne traite que les paris en attente liés à un vrai match API
        val pending = bets.filter { it.status == "En cours" && it.matchId.isNotEmpty() }
        if (pending.isEmpty()) return

        try {
            // Récupère TOUS les matchs (y compris passés) pour avoir les scores
            val response = MatchApiService.create().getMatches()
            val matchMap = response.matches.associateBy { it.id }

            var balance = prefs.getFloat("balance", 150f)
            var anyResolved = false

            bets.forEachIndexed { index, bet ->
                if (bet.status != "En cours" || bet.matchId.isEmpty()) return@forEachIndexed

                val apiMatch = matchMap[bet.matchId] ?: return@forEachIndexed
                if (apiMatch.isUpcoming) return@forEachIndexed // match pas encore joué

                // Le match est terminé → on détermine le vainqueur
                val winner = when {
                    apiMatch.scoreA > apiMatch.scoreB -> "A"
                    apiMatch.scoreB > apiMatch.scoreA -> "B"
                    else -> "Nul"
                }
                val won = bet.equipeChoisie == winner
                bets[index] = bet.copy(status = if (won) "Gagné" else "Perdu")

                if (won) {
                    balance += bet.gainsPotentiels.toFloat()
                    LocalNotificationHelper.showGoalNotification(
                        context,
                        "Pari gagné ! 🎉",
                        "+${bet.gainsPotentiels.toInt()} SC — ${bet.description.take(30)}"
                    )
                } else {
                    LocalNotificationHelper.showGoalNotification(
                        context,
                        "Pari perdu",
                        "${bet.description.take(30)} — Pas de chance"
                    )
                }
                anyResolved = true
            }

            if (anyResolved) {
                prefs.edit()
                    .putString("my_bets", Gson().toJson(bets))
                    .putFloat("balance", balance)
                    .apply()
            }
        } catch (e: Exception) {
            // API inaccessible — on réessaiera au prochain démarrage
        }
    }
}
