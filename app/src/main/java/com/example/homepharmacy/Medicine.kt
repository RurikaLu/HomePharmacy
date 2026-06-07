package com.example.homepharmacy

data class Medicine(
    val id: Int = 0,
    val name: String,
    val expiryDate: String,       // "2025-12-31"
    val status: Boolean,          // true = есть, false = закончилось
    val instructions: String,      // рекомендации как пить
    val purchaseLocation: String = "",
    val purchaseDate: String = "", // дата покупки

    val price: String // цена
)