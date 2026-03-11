package com.org.labss

import com.org.labss.data.api.ProductDto
import com.org.labss.data.local.ProductEntity
import com.org.labss.data.mapper.toDomain
import com.org.labss.data.mapper.toEntity
import com.org.labss.domain.model.Product
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductMappersTest {

    private val sampleDto = ProductDto(
        id = 1,
        categoryId = 1,
        title = "Навушники",
        description = "Гарні навушники",
        price = 2999.0,
        imageUrl = "https://example.com/img.jpg",
        isPopular = false,
        isFavorite = false,
        quantity = 0
    )

    private val sampleEntity = ProductEntity(
        id = 1,
        categoryId = 1,
        title = "Навушники",
        description = "Гарні навушники",
        price = 2999.0,
        imageUrl = "https://example.com/img.jpg",
        isPopular = true,
        category = "Електроніка",
        isFavorite = false,
        quantity = 3
    )


    @Test
    fun `dto toEntity maps all fields correctly`() {
        val entity = sampleDto.toEntity(categoryName = "Електроніка")
        assertEquals(sampleDto.id, entity.id)
        assertEquals(sampleDto.categoryId, entity.categoryId)
        assertEquals(sampleDto.title, entity.title)
        assertEquals(sampleDto.description, entity.description)
        assertEquals(sampleDto.price, entity.price, 0.01)
        assertEquals(sampleDto.imageUrl, entity.imageUrl)
        assertEquals("Електроніка", entity.category)
    }

    @Test
    fun `dto toEntity uses isPopularOverride when provided`() {
        val entity = sampleDto.toEntity(isPopularOverride = true)
        assertTrue(entity.isPopular)
    }

    @Test
    fun `dto toEntity falls back to dto isPopular when override is null`() {
        val entity = sampleDto.toEntity(isPopularOverride = null)
        assertEquals(sampleDto.isPopular, entity.isPopular)
    }

    @Test
    fun `dto toEntity preserves quantity`() {
        val entity = sampleDto.toEntity(quantity = 5)
        assertEquals(5, entity.quantity)
    }

    @Test
    fun `dto toEntity preserves isFavorite`() {
        val entity = sampleDto.toEntity(isFavorite = true)
        assertTrue(entity.isFavorite)
    }


    @Test
    fun `entity toDomain maps all fields correctly`() {
        val product = sampleEntity.toDomain()
        assertEquals(sampleEntity.id, product.id)
        assertEquals(sampleEntity.title, product.title)
        assertEquals(sampleEntity.description, product.description)
        assertEquals(sampleEntity.price, product.price, 0.01)
        assertEquals(sampleEntity.imageUrl, product.imageUrl)
        assertEquals(sampleEntity.isPopular, product.isPopular)
        assertEquals(sampleEntity.category, product.category)
        assertEquals(sampleEntity.isFavorite, product.isFavorite)
        assertEquals(sampleEntity.quantity, product.quantity)
    }

    @Test
    fun `entity toDomain with isFavorite true`() {
        val entity = sampleEntity.copy(isFavorite = true)
        val product = entity.toDomain()
        assertTrue(product.isFavorite)
    }


    @Test
    fun `product toEntity maps all fields correctly`() {
        val product = Product(
            id = 2,
            title = "Клавіатура",
            description = "RGB",
            price = 3200.0,
            imageUrl = "",
            isPopular = true,
            category = "Аксесуари",
            isFavorite = true,
            quantity = 0
        )
        val entity = product.toEntity()
        assertEquals(product.id, entity.id)
        assertEquals(product.title, entity.title)
        assertEquals(product.isFavorite, entity.isFavorite)
        assertEquals(product.quantity, entity.quantity)
    }


    @Test
    fun `dto toEntity default quantity is 0`() {
        val entity = sampleDto.toEntity(categoryName = "Електроніка")
        assertEquals(0, entity.quantity)
    }

    @Test
    fun `dto toEntity default isFavorite is false`() {
        val entity = sampleDto.toEntity(categoryName = "Електроніка")
        assertFalse(entity.isFavorite)
    }
}

