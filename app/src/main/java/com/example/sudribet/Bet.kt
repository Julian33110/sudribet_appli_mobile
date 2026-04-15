package com.example.sudribet

data class Bet(
    val id: String,
    val description: String,
    val totalCote: Double,
    val mise: Double,
    val gainsPotentiels: Double,
    val date: String,
    val status: String,              // "En cours", "Gagné", "Perdu"
    val matchId: String = "",        // ID du match API (pour résolution automatique)
    val equipeChoisie: String = ""   // "A", "B", ou "Nul"
)