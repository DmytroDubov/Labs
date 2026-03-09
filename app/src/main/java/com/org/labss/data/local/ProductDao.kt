package com.org.labss.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Query("SELECT * FROM products ORDER BY id ASC")
    fun observeAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE isPopular = 1 ORDER BY id ASC")
    fun observePopularProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE category = :category ORDER BY id ASC")
    fun observeByCategory(category: String): Flow<List<ProductEntity>>

    @Query(
        """
        SELECT * FROM products
        WHERE title LIKE '%' || :query || '%' 
           OR description LIKE '%' || :query || '%'
        ORDER BY id ASC
        """
    )
    fun observeByQuery(query: String): Flow<List<ProductEntity>>

    @Query(
        """
        SELECT * FROM products
        WHERE (:category IS NULL OR category = :category)
          AND (
              :query IS NULL OR :query = '' 
              OR title LIKE '%' || :query || '%'
              OR description LIKE '%' || :query || '%'
          )
        ORDER BY id ASC
        """
    )
    fun observeByQueryAndCategory(query: String?, category: String?): Flow<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<ProductEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: ProductEntity)

    @Query("UPDATE products SET quantity = :quantity WHERE id = :productId")
    suspend fun updateQuantity(productId: Int, quantity: Int)

    @Query("SELECT quantity FROM products WHERE id = :productId LIMIT 1")
    suspend fun getQuantityById(productId: Int): Int?

    @Query("UPDATE products SET isFavorite = :isFavorite WHERE id = :productId")
    suspend fun updateFavorite(productId: Int, isFavorite: Boolean)

    @Query("SELECT isFavorite FROM products WHERE id = :productId LIMIT 1")
    suspend fun getFavoriteById(productId: Int): Boolean?

    @Query("SELECT DISTINCT category FROM products ORDER BY category ASC")
    suspend fun getAllCategories(): List<String>

    @Query("DELETE FROM products")
    suspend fun clearAll()

    @Transaction
    suspend fun increaseQuantity(productId: Int) {
        val current = getQuantityById(productId) ?: 0
        updateQuantity(productId, current + 1)
    }

    @Transaction
    suspend fun decreaseQuantity(productId: Int) {
        val current = getQuantityById(productId) ?: 0
        val updated = (current - 1).coerceAtLeast(0)
        updateQuantity(productId, updated)
    }

    @Transaction
    suspend fun toggleFavorite(productId: Int) {
        val current = getFavoriteById(productId) ?: false
        updateFavorite(productId, !current)
    }
}