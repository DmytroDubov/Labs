package com.org.labss.data.api

import com.google.gson.annotations.SerializedName

data class SearchHistoryDto(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("query") val query: String,
    @SerializedName("timestamp") val timestamp: Long,
    @SerializedName("resultCount") val resultCount: Int = 0
)

