package com.org.labss.domain.model

data class Product(
    val id: Int,
    val title: String,
    val description: String,
    val price: Double,
    val imageUrl: String,
    val isPopular: Boolean,
    val category: String,
    val isFavorite: Boolean = false,
    val quantity: Int = 0
)