package com.org.labss.ui.vm

import com.org.labss.domain.model.Category
import com.org.labss.domain.model.Product
import com.org.labss.domain.model.SearchHistoryItem

data class ProductUiState(
    val query: String = "",
    val selectedCategory: String? = null,
    val categories: List<String> = emptyList(),
    val categoryObjects: List<Category> = emptyList(),
    val products: List<Product> = emptyList(),
    val popularProducts: List<Product> = emptyList(),
    val searchHistory: List<SearchHistoryItem> = emptyList(),
    val isLoading: Boolean = false
)