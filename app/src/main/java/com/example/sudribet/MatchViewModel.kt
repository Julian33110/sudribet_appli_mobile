package com.example.sudribet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Random

class MatchViewModel : ViewModel() {

    private val _matches = MutableStateFlow<List<Match>>(emptyList())
    val matches: StateFlow<List<Match>> = _matches.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    private val _selectedSport = MutableStateFlow("Tous")

    private val allMatches = listOf(
        Match("1", "ESME", "EPITA", 1.85, 3.40, "21:00", "Football", isLive = true, scoreA = 2, scoreB = 1, coteNul = 3.20),
        Match("2", "IPSA", "SupBiotech", 1.50, 2.50, "15:00", "Rugby", coteNul = 4.10),
        Match("3", "Epitech", "ESME", 1.80, 1.90, "21:00", "Basket", isLive = true, scoreA = 95, scoreB = 92),
        Match("4", "Centrale", "Polytechnique", 1.40, 2.80, "18:00", "Handball", coteNul = 5.00),
        Match("5", "INSA", "ENSAM", 1.60, 2.20, "20:00", "Volley"),
        Match("6", "HEC", "ESSEC", 2.10, 2.00, "21:00", "Football", coteNul = 3.50),
        Match("7", "ESME Lyon", "ESME Lille", 2.30, 2.45, "17:00", "Football", coteNul = 3.30),
        Match("8", "Sorbonne", "Assas", 1.70, 2.10, "19:00", "Rugby", coteNul = 4.50),
        Match("9", "ESME", "INSA Lyon", 1.60, 2.60, "14:00", "Football", coteNul = 3.80),
        Match("10", "IPSA", "ISAE-Supaero", 1.75, 2.25, "16:00", "Basket"),
        Match("11", "Arts et Métiers", "ESME", 2.40, 1.70, "20:30", "Handball", coteNul = 4.20),
        Match("12", "CentraleSupélec", "ESME", 1.90, 2.10, "18:30", "Volley")
    )

    init {
        _matches.value = allMatches
        startLiveUpdates()
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        filterMatches()
    }

    fun updateSportFilter(sport: String) {
        _selectedSport.value = sport
        filterMatches()
    }

    private fun filterMatches() {
        val query = _searchQuery.value
        val sport = _selectedSport.value

        _matches.value = allMatches.filter {
            (it.equipeA.contains(query, ignoreCase = true) || it.equipeB.contains(query, ignoreCase = true)) &&
            (sport == "Tous" || it.categorie == sport)
        }
    }

    private fun startLiveUpdates() {
        viewModelScope.launch {
            while (true) {
                delay(3000)
                val currentList = _matches.value.toMutableList()
                var changed = false
                currentList.forEachIndexed { index, match ->
                    if (match.isLive) {
                        val newScoreA = match.scoreA + if (Random().nextInt(100) > 97) 1 else 0
                        val newScoreB = match.scoreB + if (Random().nextInt(100) > 97) 1 else 0
                        if (newScoreA != match.scoreA || newScoreB != match.scoreB) {
                            currentList[index] = match.copy(scoreA = newScoreA, scoreB = newScoreB)
                            changed = true
                        }
                    }
                }
                if (changed) {
                    _matches.value = currentList
                }
            }
        }
    }
}
