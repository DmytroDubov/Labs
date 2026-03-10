package com.org.labss.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "products",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_DEFAULT
        )
    ],
    indices = [Index(value = ["categoryId"])]
)
data class ProductEntity(
    @PrimaryKey val id: Int,
    val categoryId: Int = 0,
    val title: String,
    val description: String,
    val price: Double,
    val imageUrl: String,
    val isPopular: Boolean,
    val category: String,
    val isFavorite: Boolean = false,
    val quantity: Int = 0
)