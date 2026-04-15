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

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    private val _selectedSport = MutableStateFlow("Tous")

    private val apiService = MatchApiService.create()
    private var allMatches: List<Match> = emptyList()

    // Données de secours si l'API est inaccessible
    private val fallbackMatches = listOf(
        Match("f1", "ESME", "EPITA", 1.85, 3.40, "21:00", "Football", isLive = true, scoreA = 2, scoreB = 1, coteNul = 3.20),
        Match("f2", "IPSA", "SupBiotech", 1.50, 2.50, "15:00", "Rugby", coteNul = 4.10),
        Match("f3", "Epitech", "ESME", 1.80, 1.90, "21:00", "Basket", isLive = true, scoreA = 95, scoreB = 92),
        Match("f4", "Centrale", "Polytechnique", 1.40, 2.80, "18:00", "Handball", coteNul = 5.00),
        Match("f5", "INSA Lyon", "ENSAM", 1.60, 2.20, "20:00", "Volley"),
    )

    init {
        fetchMatches()
        startLiveUpdates()
    }

    fun fetchMatches() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                // upcoming=true → uniquement les matchs à venir pour parier
                val response = apiService.getMatches(upcoming = "true")
                allMatches = response.matches.map { it.toMatch() }
                _error.value = null
            } catch (e: Exception) {
                // API inaccessible → données locales
                allMatches = fallbackMatches
                _error.value = "API hors ligne — données locales"
            } finally {
                _isLoading.value = false
                filterMatches()
            }
        }
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
                if (changed) _matches.value = currentList
            }
        }
    }
}
