package com.org.labss.data.api

import com.google.gson.annotations.SerializedName

data class ProductDto(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String, // Змінили "name" на "title"
    @SerializedName("description") val description: String,
    @SerializedName("price") val price: Double,
    @SerializedName("imageUrl") val imageUrl: String,
    val isPopular: Boolean = false,
    @SerializedName("category") val category: String
)