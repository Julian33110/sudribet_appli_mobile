package com.example.sudribet

data class Bet(
    val id: String,
    val description: String, // ex: "Suds + Toulouse"
    val totalCote: Double,
    val mise: Double,
    val gainsPotentiels: Double,
    val date: String,
    val status: String // "En cours", "Gagné", "Perdu"
)