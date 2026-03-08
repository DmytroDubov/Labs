package com.org.labss.data.mapper

import com.org.labss.data.api.ProductDto
import com.org.labss.data.local.ProductEntity
import com.org.labss.domain.model.Product

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