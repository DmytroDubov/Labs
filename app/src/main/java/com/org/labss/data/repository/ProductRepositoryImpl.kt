package com.org.labss.data.repository

import android.util.Log
import com.org.labss.data.api.ApiService
import com.org.labss.data.local.ProductDao
import com.org.labss.data.mapper.toDomain
import com.org.labss.data.mapper.toEntity
import com.org.labss.domain.model.Product
import com.org.labss.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProductRepositoryImpl(
    private val apiService: ApiService,
    private val productDao: ProductDao
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
            val productsFromSource = apiService.getProducts().record
            val entities = productsFromSource.mapIndexed { index, dto ->
                val currentQty = productDao.getQuantityById(dto.id) ?: 0
                val currentFavorite = productDao.getFavoriteById(dto.id) ?: false
                val isPopular = dto.isPopular || index < 4
                dto.toEntity(
                    quantity = currentQty,
                    isPopularOverride = isPopular,
                    isFavorite = currentFavorite
                )
            }
            productDao.insertAll(entities)
        } catch (e: Exception) {
            Log.e("API_TEST", "Помилка завантаження даних: ${e.message}", e)
        }
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
}