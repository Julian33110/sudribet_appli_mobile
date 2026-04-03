package com.example.sudribet

data class Badge(
    val emoji: String,
    val name: String,
    val description: String,
    val unlocked: Boolean
)

object BadgeSystem {

    fun getBadges(bets: List<Bet>, balance: Float): List<Badge> {
        val wins = bets.count { it.status == "Gagné" }
        val losses = bets.count { it.status == "Perdu" }
        val total = bets.size

        val streak = computeStreak(bets)
        val hasCombo = bets.any { it.description.contains(",") }
        val hasBigCombo = bets.any { it.description.split(",").size >= 3 }

        return listOf(
            Badge("🎯", "Premier pari", "Place ton premier pari", total >= 1),
            Badge("🏆", "Première victoire", "Remporte ton premier pari", wins >= 1),
            Badge("🔥", "Série x3", "3 paris gagnés de suite", streak >= 3),
            Badge("💎", "Combiné", "Place un pari combiné", hasCombo),
            Badge("🎰", "Accro", "10 paris au total", total >= 10),
            Badge("⭐", "Lucky", "Gagne 5 paris", wins >= 5),
            Badge("🚀", "Expert", "Gagne 10 paris", wins >= 10),
            Badge("💰", "Riche", "Atteins 500 SC", balance >= 500f),
            Badge("🎳", "Combo Master", "Combiné de 3+ matchs", hasBigCombo),
            Badge("👑", "Légende", "Gagne 20 paris", wins >= 20)
        )
    }

    private fun computeStreak(bets: List<Bet>): Int {
        var streak = 0
        for (bet in bets) {
            if (bet.status == "Gagné") streak++ else break
        }
        return streak
    }
}
