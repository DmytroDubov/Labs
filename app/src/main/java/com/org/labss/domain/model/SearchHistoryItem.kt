package com.org.labss.domain.model

data class SearchHistoryItem(
    val id: Int = 0,
    val query: String,
    val timestamp: Long = System.currentTimeMillis(),
    val resultCount: Int = 0
)

