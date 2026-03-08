package com.org.labss.ui.vm

import com.org.labs.domain.model.Product

data class ProductUiState(
    val query: String = "",
    val selectedCategory: String? = null,
    val categories: List<String> = emptyList(),
    val products: List<Product> = emptyList(),
    val popularProducts: List<Product> = emptyList(),
    val isLoading: Boolean = false
)