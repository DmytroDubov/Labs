package com.org.labss.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val description: String,
    val price: Double,
    val imageUrl: String,
    val isPopular: Boolean,
    val category: String,
    val isFavorite: Boolean = false,
    val quantity: Int = 0
)