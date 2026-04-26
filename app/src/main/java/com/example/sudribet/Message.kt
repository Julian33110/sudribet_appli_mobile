package com.example.sudribet

data class Message(
    val text: String,
    val isUser: Boolean,
    val time: String,
    val betDescription: String? = null,
    val betCote: Double? = null
)