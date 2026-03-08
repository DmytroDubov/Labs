package com.org.labss.ui.vm

sealed interface ProductEvent {
    data class OnSearchQueryChanged(val query: String) : ProductEvent
    data class OnCategorySelected(val category: String?) : ProductEvent
    data class OnAddProductClicked(val productId: Int) : ProductEvent
    data class OnIncreaseQuantity(val productId: Int) : ProductEvent
    data class OnDecreaseQuantity(val productId: Int) : ProductEvent
    data class OnToggleFavorite(val productId: Int) : ProductEvent
}