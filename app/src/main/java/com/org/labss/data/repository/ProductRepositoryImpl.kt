package com.org.labss.data.repository

import android.util.Log
import com.org.labss.data.api.ApiService
import com.org.labss.data.local.CategoryDao
import com.org.labss.data.local.ProductDao
import com.org.labss.data.local.ProductEntity
import com.org.labss.data.local.SearchHistoryDao
import com.org.labss.data.local.SearchHistoryEntity
import com.org.labss.data.api.SearchHistoryResponse
import com.org.labss.data.mapper.toDto
import com.org.labss.data.mapper.toDomain
import com.org.labss.data.mapper.toEntity
import com.org.labss.domain.model.Category
import com.org.labss.domain.model.Product
import com.org.labss.domain.model.SearchHistoryItem
import com.org.labss.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProductRepositoryImpl(
    private val apiService: ApiService,
    private val productDao: ProductDao,
    private val categoryDao: CategoryDao,
    private val searchHistoryDao: SearchHistoryDao
) : ProductRepository {

    override fun observeAllProducts(): Flow<List<Product>> =
        productDao.observeAllProducts().map { list -> list.map { it.toDomain() } }

    override fun observePopularProducts(): Flow<List<Product>> =
        productDao.observePopularProducts().map { list -> list.map { it.toDomain() } }

    override fun observeProductsByCategory(category: String): Flow<List<Product>> =
        productDao.observeByCategory(category).map { list -> list.map { it.toDomain() } }

    override fun observeProductsByQuery(query: String): Flow<List<Product>> =
        productDao.observeByQuery(query).map { list -> list.map { it.toDomain() } }

    override fun observeProductsByQueryAndCategory(query: String, category: String?): Flow<List<Product>> =
        productDao.observeByQueryAndCategory(query, category).map { list -> list.map { it.toDomain() } }

    override suspend fun refreshProducts() {
        try {
            val response = apiService.getProducts()

            val categoryEntities = response.categories.map { it.toEntity() }
            productDao.clearAll()
            categoryDao.clearAll()
            categoryDao.insertAll(categoryEntities)

            val categoryMap = response.categories.associate { it.id to it.name }

            val entities = response.products.mapIndexed { index, dto ->
                val currentQty = productDao.getQuantityById(dto.id) ?: 0
                val currentFavorite = productDao.getFavoriteById(dto.id) ?: dto.isFavorite
                val isPopular = dto.isPopular || index < 4
                val categoryName = categoryMap[dto.categoryId] ?: ""
                dto.toEntity(
                    categoryName = categoryName,
                    quantity = currentQty,
                    isPopularOverride = isPopular,
                    isFavorite = currentFavorite
                )
            }
            productDao.insertAll(entities)

            Log.d("API_TEST", "Завантажено: ${entities.size} товарів, ${categoryEntities.size} категорій")
        } catch (e: Exception) {
            Log.e("API_TEST", "Помилка завантаження даних: ${e.message}", e)
            val existing = productDao.getAllCategories()
            if (existing.isEmpty()) {
                loadFallbackData()
            }
        }
    }

    private suspend fun loadFallbackData() {
        val fallback = listOf(
            ProductEntity(
                id = 1,
                categoryId = 1,
                title = "Smartphone X",
                description = "Latest flagship model",
                price = 999.99,
                imageUrl = "https://via.placeholder.com/150",
                isPopular = true,
                category = "Electronics"
            ),
            ProductEntity(id = 2, categoryId = 1, title = "Laptop Pro", description = "High performance laptop", price = 1499.99, imageUrl = "https://via.placeholder.com/150", isPopular = true, category = "Electronics"),
            ProductEntity(id = 3, categoryId = 1, title = "Wireless Headphones", description = "Noise cancelling", price = 299.99, imageUrl = "https://via.placeholder.com/150", isPopular = true, category = "Electronics"),
            ProductEntity(id = 4, categoryId = 1, title = "Smart Watch", description = "Fitness tracker", price = 199.99, imageUrl = "https://via.placeholder.com/150", isPopular = true, category = "Electronics"),
            ProductEntity(id = 5, categoryId = 2, title = "T-Shirt Basic", description = "100% cotton t-shirt", price = 19.99, imageUrl = "https://via.placeholder.com/150", isPopular = false, category = "Clothing"),
            ProductEntity(id = 6, categoryId = 2, title = "Jeans Classic", description = "Slim fit jeans", price = 49.99, imageUrl = "https://via.placeholder.com/150", isPopular = false, category = "Clothing"),
            ProductEntity(id = 7, categoryId = 3, title = "Running Shoes", description = "Lightweight sport shoes", price = 89.99, imageUrl = "https://via.placeholder.com/150", isPopular = true, category = "Sports"),
            ProductEntity(id = 8, categoryId = 3, title = "Yoga Mat", description = "Non-slip exercise mat", price = 34.99, imageUrl = "https://via.placeholder.com/150", isPopular = false, category = "Sports")
        )
        productDao.insertAll(fallback)
        Log.w("API_TEST", "Завантажено fallback дані (${fallback.size} товарів)")
    }

    override suspend fun getAllCategories(): List<String> {
        val categoriesFromDb = productDao.getAllCategories()
        return categoriesFromDb.ifEmpty { emptyList() }
    }

    override suspend fun addProduct(productId: Int) {
        val current = productDao.getQuantityById(productId) ?: 0
        if (current == 0) productDao.updateQuantity(productId, 1)
    }

    override suspend fun increaseQuantity(productId: Int) {
        productDao.increaseQuantity(productId)
    }

    override suspend fun decreaseQuantity(productId: Int) {
        productDao.decreaseQuantity(productId)
    }

    override suspend fun updateQuantity(productId: Int, quantity: Int) {
        productDao.updateQuantity(productId, quantity.coerceAtLeast(0))
    }

    override suspend fun toggleFavorite(productId: Int) {
        productDao.toggleFavorite(productId)
    }

    override fun observeCategories(): Flow<List<Category>> =
        categoryDao.observeAllCategories().map { list -> list.map { it.toDomain() } }


    override fun observeSearchHistory(): Flow<List<SearchHistoryItem>> =
        searchHistoryDao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun saveSearchQuery(query: String, resultCount: Int) {
        if (query.isBlank()) return
        searchHistoryDao.deleteByQuery(query)
        searchHistoryDao.insert(
            SearchHistoryEntity(
                query = query.trim(),
                timestamp = System.currentTimeMillis(),
                resultCount = resultCount
            )
        )
        pushSearchHistoryToServer()
    }

    override suspend fun deleteSearchQuery(query: String) {
        searchHistoryDao.deleteByQuery(query)
        pushSearchHistoryToServer()
    }

    override suspend fun clearSearchHistory() {
        searchHistoryDao.clearAll()
        pushSearchHistoryToServer()
    }

    override suspend fun getRecentSearches(limit: Int): List<SearchHistoryItem> =
        searchHistoryDao.getRecent(limit).map { it.toDomain() }

    override suspend fun syncSearchHistory() {
        try {
            val remote = apiService.getSearchHistory()
            val localQueries = searchHistoryDao.getRecent(100).map { it.query }.toSet()
            val newEntries = remote.history
                .filter { it.query.isNotBlank() && it.query !in localQueries }
                .map { it.toEntity() }
            if (newEntries.isNotEmpty()) {
                newEntries.forEach { searchHistoryDao.insert(it) }
                Log.d("SEARCH_HISTORY", "Синхронізовано ${newEntries.size} записів з сервера")
            }
        } catch (e: Exception) {
            Log.e("SEARCH_HISTORY", "Помилка синхронізації history: ${e.message}")
        }
    }

    private suspend fun pushSearchHistoryToServer() {
        try {
            val localHistory = searchHistoryDao.getRecent(50).map { it.toDto() }
            apiService.updateSearchHistory(SearchHistoryResponse(history = localHistory))
            Log.d("SEARCH_HISTORY", "Збережено ${localHistory.size} записів на сервер")
        } catch (e: Exception) {
            Log.e("SEARCH_HISTORY", "Помилка збереження history на сервер: ${e.message}")
        }
    }
}