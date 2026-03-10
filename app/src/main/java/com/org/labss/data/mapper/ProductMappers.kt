package com.org.labss.data.mapper

import com.org.labss.data.api.ProductDto
import com.org.labss.data.local.CategoryEntity
import com.org.labss.data.local.ProductEntity
import com.org.labss.data.local.SearchHistoryEntity
import com.org.labss.domain.model.Category
import com.org.labss.domain.model.Product
import com.org.labss.domain.model.SearchHistoryItem

fun ProductDto.toEntity(
    quantity: Int = 0,
    isPopularOverride: Boolean? = null,
    isFavorite: Boolean = false
): ProductEntity = ProductEntity(
    id = id,
    title = title,
    description = description,
    price = price,
    imageUrl = imageUrl,
    isPopular = isPopularOverride ?: isPopular,
    category = category,
    isFavorite = isFavorite,
    quantity = quantity
)

fun ProductEntity.toDomain(): Product = Product(
    id = id,
    title = title,
    description = description,
    price = price,
    imageUrl = imageUrl,
    isPopular = isPopular,
    category = category,
    isFavorite = isFavorite,
    quantity = quantity
)

fun Product.toEntity(): ProductEntity = ProductEntity(
    id = id,
    title = title,
    description = description,
    price = price,
    imageUrl = imageUrl,
    isPopular = isPopular,
    category = category,
    isFavorite = isFavorite,
    quantity = quantity
)

fun CategoryEntity.toDomain(): Category = Category(
    id = id,
    name = name,
    imageUrl = imageUrl
)

fun Category.toEntity(): CategoryEntity = CategoryEntity(
    id = id,
    name = name,
    imageUrl = imageUrl
)

fun SearchHistoryEntity.toDomain(): SearchHistoryItem = SearchHistoryItem(
    id = id,
    query = query,
    timestamp = timestamp,
    resultCount = resultCount
)

fun SearchHistoryItem.toEntity(): SearchHistoryEntity = SearchHistoryEntity(
    id = id,
    query = query,
    timestamp = timestamp,
    resultCount = resultCount
)
