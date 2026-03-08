package com.org.labss

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.org.labss.data.local.ECDatabase
import com.org.labss.data.local.ProductDao
import com.org.labss.data.local.ProductEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.collections.all
import kotlin.collections.count
import kotlin.collections.distinct
import kotlin.collections.first
import kotlin.collections.isNotEmpty
import kotlin.collections.map
import kotlin.collections.shuffled
import kotlin.collections.sorted
import kotlin.jvm.java

@RunWith(AndroidJUnit4::class)
class ProductDaoTest {

    private lateinit var db: ECDatabase
    private lateinit var dao: ProductDao

    private val testEntities = listOf(
        ProductEntity(1, "Навушники", "Гарні навушники", 2999.0, "", true, "Електроніка", false, 0),
        ProductEntity(2, "Клавіатура", "RGB клавіатура", 3200.0, "", true, "Аксесуари", false, 0),
        ProductEntity(3, "Мишка", "Ігрова мишка", 1200.0, "", false, "Аксесуари", false, 0),
        ProductEntity(4, "Монітор", "4K монітор", 12500.0, "", true, "Електроніка", false, 0),
        ProductEntity(5, "SSD", "Зовнішній SSD", 3800.0, "", false, "Накопичувачі", false, 0),
    )

    @Before
    fun createDb() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ECDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = db.productDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    // --- Insert + Read ---

    @Test
    fun insertAll_and_observeAllProducts_returnsAllItems() = runTest {
        dao.insertAll(testEntities)
        val result = dao.observeAllProducts().first()
        Assert.assertEquals(5, result.size)
    }

    @Test
    fun insertAll_and_observeAllProducts_orderedById() = runTest {
        dao.insertAll(testEntities.shuffled())
        val result = dao.observeAllProducts().first()
        Assert.assertEquals(listOf(1, 2, 3, 4, 5), result.map { it.id })
    }

    @Test
    fun insertAll_replacesOnConflict() = runTest {
        dao.insertAll(testEntities)
        val updated = testEntities[0].copy(title = "Оновлені навушники")
        dao.insert(updated)
        val result = dao.observeAllProducts().first()
        Assert.assertEquals(5, result.size)
        Assert.assertEquals("Оновлені навушники", result.first { it.id == 1 }.title)
    }

    // --- Popular ---

    @Test
    fun observePopularProducts_returnsOnlyPopular() = runTest {
        dao.insertAll(testEntities)
        val popular = dao.observePopularProducts().first()
        Assert.assertTrue(popular.isNotEmpty())
        Assert.assertTrue(popular.all { it.isPopular })
    }

    @Test
    fun observePopularProducts_countMatchesInserted() = runTest {
        dao.insertAll(testEntities)
        val popular = dao.observePopularProducts().first()
        Assert.assertEquals(testEntities.count { it.isPopular }, popular.size)
    }

    // --- Search ---

    @Test
    fun observeByQueryAndCategory_withQuery_filtersCorrectly() = runTest {
        dao.insertAll(testEntities)
        val result = dao.observeByQueryAndCategory("навуш", null).first()
        Assert.assertEquals(1, result.size)
        Assert.assertEquals("Навушники", result[0].title)
    }

    @Test
    fun observeByQueryAndCategory_withCategory_filtersCorrectly() = runTest {
        dao.insertAll(testEntities)
        val result = dao.observeByQueryAndCategory("", "Аксесуари").first()
        Assert.assertEquals(2, result.size)
        Assert.assertTrue(result.all { it.category == "Аксесуари" })
    }

    @Test
    fun observeByQueryAndCategory_withQueryAndCategory_filtersCorrectly() = runTest {
        dao.insertAll(testEntities)
        val result = dao.observeByQueryAndCategory("клавіатура", "Аксесуари").first()
        Assert.assertEquals(1, result.size)
        Assert.assertEquals("Клавіатура", result[0].title)
    }

    @Test
    fun observeByQueryAndCategory_emptyQuery_returnsAll() = runTest {
        dao.insertAll(testEntities)
        val result = dao.observeByQueryAndCategory("", null).first()
        Assert.assertEquals(5, result.size)
    }

    @Test
    fun observeByQueryAndCategory_noMatch_returnsEmpty() = runTest {
        dao.insertAll(testEntities)
        val result = dao.observeByQueryAndCategory("xyz_not_exist", null).first()
        Assert.assertTrue(result.isEmpty())
    }

    // --- Quantity ---

    @Test
    fun updateQuantity_changesQuantityCorrectly() = runTest {
        dao.insertAll(testEntities)
        dao.updateQuantity(1, 5)
        val qty = dao.getQuantityById(1)
        Assert.assertEquals(5, qty)
    }

    @Test
    fun increaseQuantity_incrementsByOne() = runTest {
        dao.insertAll(testEntities)
        dao.updateQuantity(1, 3)
        dao.increaseQuantity(1)
        Assert.assertEquals(4, dao.getQuantityById(1))
    }

    @Test
    fun decreaseQuantity_decrementsByOne() = runTest {
        dao.insertAll(testEntities)
        dao.updateQuantity(1, 3)
        dao.decreaseQuantity(1)
        Assert.assertEquals(2, dao.getQuantityById(1))
    }

    @Test
    fun decreaseQuantity_doesNotGoBelowZero() = runTest {
        dao.insertAll(testEntities)
        dao.updateQuantity(1, 0)
        dao.decreaseQuantity(1)
        Assert.assertEquals(0, dao.getQuantityById(1))
    }

    // --- Favorite ---

    @Test
    fun updateFavorite_setsToTrue() = runTest {
        dao.insertAll(testEntities)
        dao.updateFavorite(1, true)
        val fav = dao.getFavoriteById(1)
        Assert.assertTrue(fav == true)
    }

    @Test
    fun toggleFavorite_switchesToTrue_whenFalse() = runTest {
        dao.insertAll(testEntities) // всі isFavorite = false
        dao.toggleFavorite(1)
        val fav = dao.getFavoriteById(1)
        Assert.assertTrue(fav == true)
    }

    @Test
    fun toggleFavorite_switchesToFalse_whenTrue() = runTest {
        dao.insertAll(testEntities.map { it.copy(isFavorite = true) })
        dao.toggleFavorite(1)
        val fav = dao.getFavoriteById(1)
        Assert.assertFalse(fav == true)
    }

    @Test
    fun toggleFavorite_doesNotAffectOtherProducts() = runTest {
        dao.insertAll(testEntities)
        dao.toggleFavorite(1)
        val fav2 = dao.getFavoriteById(2)
        Assert.assertFalse(fav2 == true)
    }

    // --- Categories ---

    @Test
    fun getAllCategories_returnsDistinctSortedCategories() = runTest {
        dao.insertAll(testEntities)
        val categories = dao.getAllCategories()
        Assert.assertEquals(categories.distinct(), categories)
        Assert.assertEquals(categories.sorted(), categories)
    }

    @Test
    fun getAllCategories_containsExpected() = runTest {
        dao.insertAll(testEntities)
        val categories = dao.getAllCategories()
        Assert.assertTrue("Електроніка" in categories)
        Assert.assertTrue("Аксесуари" in categories)
        Assert.assertTrue("Накопичувачі" in categories)
    }

    // --- Clear ---

    @Test
    fun clearAll_removesAllProducts() = runTest {
        dao.insertAll(testEntities)
        dao.clearAll()
        val result = dao.observeAllProducts().first()
        Assert.assertTrue(result.isEmpty())
    }
}