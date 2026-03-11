package com.org.labss.data.api

import com.google.gson.annotations.SerializedName

data class SearchHistoryResponse(
    @SerializedName("history") val history: List<SearchHistoryDto> = emptyList()
)

