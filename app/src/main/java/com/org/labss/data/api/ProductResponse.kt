package com.org.labss.data.api

import com.google.gson.annotations.SerializedName

data class ProductResponse(
    @SerializedName("categories") val categories: List<CategoryDto>,
    @SerializedName("products") val products: List<ProductDto>
)