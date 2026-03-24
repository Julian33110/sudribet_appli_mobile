package com.example.sudribet

data class Match(
    val id: String,
    val equipeA: String,
    val equipeB: String,
    val coteA: Double,
    val coteB: Double,
    val heure: String,
    val categorie: String,
    val isLive: Boolean = false,
    val scoreA: Int = 0,
    val scoreB: Int = 0
)