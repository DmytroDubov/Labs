package com.org.labss.data.api

import com.google.gson.annotations.SerializedName

data class ProductDto(
    @SerializedName("id") val id: Int,
    @SerializedName("categoryId") val categoryId: Int,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("price") val price: Double,
    @SerializedName("imageUrl") val imageUrl: String,
    @SerializedName("isPopular") val isPopular: Boolean = false,
    @SerializedName("isFavorite") val isFavorite: Boolean = false,
    @SerializedName("quantity") val quantity: Int = 0
)