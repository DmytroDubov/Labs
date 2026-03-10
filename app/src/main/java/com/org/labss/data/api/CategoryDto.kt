package com.org.labss.data.api

import com.google.gson.annotations.SerializedName

data class CategoryDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("imageUrl") val imageUrl: String = ""
)

