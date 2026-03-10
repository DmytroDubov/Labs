package com.org.labss.domain.repository

import com.org.labss.domain.model.Category
import com.org.labss.domain.model.Product
import com.org.labss.domain.model.SearchHistoryItem
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun observeAllProducts(): Flow<List<Product>>
    fun observePopularProducts(): Flow<List<Product>>
    fun observeProductsByCategory(category: String): Flow<List<Product>>
    fun observeProductsByQuery(query: String): Flow<List<Product>>
    fun observeProductsByQueryAndCategory(query: String, category: String?): Flow<List<Product>>
    suspend fun refreshProducts()
    suspend fun getAllCategories(): List<String>
    suspend fun addProduct(productId: Int)
    suspend fun increaseQuantity(productId: Int)
    suspend fun decreaseQuantity(productId: Int)
    suspend fun updateQuantity(productId: Int, quantity: Int)
    suspend fun toggleFavorite(productId: Int)

    // Categories table
    fun observeCategories(): Flow<List<Category>>
    suspend fun syncCategories()

    // Search history table
    fun observeSearchHistory(): Flow<List<SearchHistoryItem>>
    suspend fun saveSearchQuery(query: String, resultCount: Int = 0)
    suspend fun deleteSearchQuery(query: String)
    suspend fun clearSearchHistory()
    suspend fun getRecentSearches(limit: Int = 10): List<SearchHistoryItem>
}