package com.org.labss.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.org.labss.analytic.AnalyticsManager
import com.org.labss.domain.repository.ProductRepository
import com.posthog.PostHog
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SharedViewModel(
    private val repository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductUiState(isLoading = true))
    val uiState: StateFlow<ProductUiState> = _uiState.asStateFlow()

    private val queryFlow = MutableStateFlow("")
    private val categoryFlow = MutableStateFlow<String?>(null)
    var isSearchInitialized: Boolean = false

    init {
        observeFilteredProducts()
        observePopular()
        observeCategories()
        observeSearchHistory()
        loadInitial()
        checkFeatureFlags()
    }


    private fun checkFeatureFlags() {
        PostHog.reloadFeatureFlags {
            val isBuyAllEnabled = AnalyticsManager.isFeatureEnabled("show-buy-all")

            _uiState.update { it.copy(isBuyAll = isBuyAllEnabled) }

            android.util.Log.d("PostHogDebug", "Флаг Buy All отримано: $isBuyAllEnabled")
        }
    }
    private fun loadInitial() {
        viewModelScope.launch {
            repository.refreshProducts()
            repository.syncSearchHistory()
            val categories = repository.getAllCategories()
            _uiState.update { it.copy(categories = categories, isLoading = false) }
        }
    }

    private fun observeFilteredProducts() {
        viewModelScope.launch {
            combine(queryFlow, categoryFlow) { q, c -> q to c }
                .flatMapLatest { (q, c) ->
                    repository.observeProductsByQueryAndCategory(q, c)
                }
                .collect { list ->
                    _uiState.update { it.copy(products = list) }
                }
        }
    }

    private fun observePopular() {
        viewModelScope.launch {
            repository.observePopularProducts().collect { list ->
                _uiState.update { it.copy(popularProducts = list) }
            }
        }
    }

    private fun observeCategories() {
        viewModelScope.launch {
            repository.observeCategories().collect { list ->
                _uiState.update { it.copy(categoryObjects = list) }
            }
        }
    }

    private fun observeSearchHistory() {
        viewModelScope.launch {
            repository.observeSearchHistory().collect { list ->
                _uiState.update { it.copy(searchHistory = list) }
            }
        }
    }

    fun onEvent(event: ProductEvent) {
        when (event) {
            is ProductEvent.OnSearchQueryChanged -> {
                queryFlow.value = event.query
                _uiState.update { it.copy(query = event.query) }
            }

            is ProductEvent.OnCategorySelected -> {
                categoryFlow.value = event.category
                _uiState.update { it.copy(selectedCategory = event.category) }

                event.category?.let { categoryName ->
                    AnalyticsManager.trackCategorySelected(categoryName)
                }
            }

            is ProductEvent.OnAddProductClicked -> {
                viewModelScope.launch { repository.addProduct(event.productId) }

                AnalyticsManager.trackProductAdded(event.productId)
            }

            is ProductEvent.OnIncreaseQuantity -> {
                viewModelScope.launch { repository.increaseQuantity(event.productId) }

                AnalyticsManager.trackQuantityChanged(event.productId, "increase")
            }

            is ProductEvent.OnDecreaseQuantity -> {
                viewModelScope.launch { repository.decreaseQuantity(event.productId) }

                AnalyticsManager.trackQuantityChanged(event.productId, "decrease")
            }

            is ProductEvent.OnToggleFavorite -> {
                viewModelScope.launch { repository.toggleFavorite(event.productId) }

                AnalyticsManager.trackFavoriteToggled(event.productId)
            }

            is ProductEvent.OnSearchSubmitted -> {
                viewModelScope.launch {
                    repository.saveSearchQuery(event.query, event.resultCount)
                }

                AnalyticsManager.trackSearchSubmitted(event.query, event.resultCount)
            }

            is ProductEvent.OnDeleteSearchHistory -> {
                viewModelScope.launch { repository.deleteSearchQuery(event.query) }

                AnalyticsManager.trackSearchHistoryItemDeleted(event.query)
            }

            is ProductEvent.OnClearSearchHistory -> {
                viewModelScope.launch { repository.clearSearchHistory() }

                AnalyticsManager.trackSearchHistoryCleared()
            }
        }
    }

    fun resetSearch() {
        isSearchInitialized = false
        queryFlow.value = ""
        categoryFlow.value = null
        _uiState.update { it.copy(query = "", selectedCategory = null) }
    }

    class Factory(
        private val repository: ProductRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SharedViewModel(repository) as T
        }
    }
}