package com.org.labss.ui.features.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.org.labss.domain.model.Product
import com.org.labss.ui.features.components.CustomSearchBar
import com.org.labss.ui.features.components.DynamicAddButton
import com.org.labss.ui.features.components.FilterChip
import com.org.labss.ui.features.components.ProductImage
import com.org.labss.ui.vm.ProductEvent
import com.org.labss.ui.vm.ProductUiState
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import com.org.labss.ui.theme.BlackPrimary
import com.org.labss.ui.theme.LightGraySurface
import com.org.labss.ui.theme.MediumGrayText

@Composable
fun SearchResultsScreen(
    state: ProductUiState,
    onEvent: (ProductEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Bottom)),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CustomSearchBar(
            value = state.query,
            onValueChange = { onEvent(ProductEvent.OnSearchQueryChanged(it)) },
            onSearchAction = { query ->
                onEvent(ProductEvent.OnSearchQueryChanged(query))
                onEvent(ProductEvent.OnSearchSubmitted(query, state.products.size))
            }
        )

        // Якщо запит порожній — показуємо пошукову історію
        if (state.query.isEmpty() && state.searchHistory.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent searches",
                    color = BlackPrimary,
                    style = MaterialTheme.typography.titleMedium
                )
                TextButton(onClick = { onEvent(ProductEvent.OnClearSearchHistory) }) {
                    Text(text = "Clear all", color = MediumGrayText)
                }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                items(state.searchHistory) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onEvent(ProductEvent.OnSearchQueryChanged(item.query))
                            }
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                tint = MediumGrayText
                            )
                            Column {
                                Text(text = item.query, color = BlackPrimary)
                                if (item.resultCount > 0) {
                                    Text(
                                        text = "${item.resultCount} results",
                                        color = MediumGrayText,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                        IconButton(onClick = { onEvent(ProductEvent.OnDeleteSearchHistory(item.query)) }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Delete",
                                tint = MediumGrayText
                            )
                        }
                    }
                }
            }
        } else {
            // Показуємо результати пошуку
            Text(
                text = "Results",
                color = BlackPrimary,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                items(state.categories) { category ->
                    FilterChip(
                        text = category,
                        selected = state.selectedCategory == category,
                        onClick = {
                            onEvent(
                                ProductEvent.OnCategorySelected(
                                    if (state.selectedCategory == category) null else category
                                )
                            )
                        }
                    )
                }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                items(state.products) { product ->
                    ProductListItem(
                        product = product,
                        onAdd = { onEvent(ProductEvent.OnAddProductClicked(product.id)) },
                        onIncrease = { onEvent(ProductEvent.OnIncreaseQuantity(product.id)) },
                        onDecrease = { onEvent(ProductEvent.OnDecreaseQuantity(product.id)) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductListItem(
    product: Product,
    onAdd: () -> Unit,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(LightGraySurface, RoundedCornerShape(16.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = product.title, color = BlackPrimary)
            Text(text = product.description, color = MediumGrayText)
            Text(text = "$${product.price}", color = BlackPrimary)
        }

        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ProductImage(
                imageUrl = product.imageUrl,
                modifier = Modifier.size(90.dp),
                shape = RoundedCornerShape(8.dp)
            )
            DynamicAddButton(
                quantity = product.quantity,
                onAdd = onAdd,
                onIncrease = onIncrease,
                onDecrease = onDecrease
            )
        }
    }
}