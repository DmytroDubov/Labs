package com.org.labss.ui.features.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.org.labss.domain.model.Product
import com.org.labss.ui.features.components.CategoryItem
import com.org.labss.ui.features.components.CustomSearchBar
import com.org.labss.ui.features.components.DynamicAddButton
import com.org.labss.ui.features.components.ProductImage
import com.org.labss.ui.features.components.FavoriteIcon
import com.org.labss.ui.vm.ProductEvent
import com.org.labss.ui.vm.ProductUiState
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.text.style.TextOverflow
import com.org.labss.ui.theme.BlackPrimary
import com.org.labss.ui.theme.LightGraySurface

@Composable
fun HomeScreen(
    state: ProductUiState,
    onEvent: (ProductEvent) -> Unit,
    onNavigateToSearch: (String, String?) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(
            top = 16.dp,
            bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()+6.dp
        )

    ) {
        item {
            CustomSearchBar(
                value = state.query,
                onValueChange = { onEvent(ProductEvent.OnSearchQueryChanged(it)) },
                readOnly = false,
                onClick = { onNavigateToSearch(state.query, state.selectedCategory) },
                onSearchAction = { query ->
                    onEvent(ProductEvent.OnSearchQueryChanged(query))
                    onNavigateToSearch(query, state.selectedCategory)
                }
            )
        }

        item {
            Text(text = "Our last giveaway", color = BlackPrimary)
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(LightGraySurface, RoundedCornerShape(16.dp))
            )
        }

        item {
            Text(text = "Popular categories", color = BlackPrimary)
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                items(state.categories.take(4)) { category ->
                    CategoryItem(title = category) {
                        onEvent(ProductEvent.OnCategorySelected(category))
                        onNavigateToSearch("", category)
                    }
                }
            }
        }

        item {
            Text(text = "Popular products", color = BlackPrimary)
            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                val rows = state.popularProducts.chunked(2)

                rows.forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        rowItems.forEach { product ->
                            Box(modifier = Modifier.weight(1f)) {
                                ProductGridCard(
                                    product = product,
                                    isFavorite = product.isFavorite,
                                    onToggleFavorite = { onEvent(ProductEvent.OnToggleFavorite(product.id)) },
                                    onAdd = { onEvent(ProductEvent.OnAddProductClicked(product.id)) },
                                    onIncrease = { onEvent(ProductEvent.OnIncreaseQuantity(product.id)) },
                                    onDecrease = { onEvent(ProductEvent.OnDecreaseQuantity(product.id)) }
                                )
                            }
                        }
                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductGridCard(
    product: Product,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onAdd: () -> Unit,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(245.dp)
            .background(LightGraySurface, RoundedCornerShape(16.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ProductImage(
            imageUrl = product.imageUrl,
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        )

        Text(
            text = product.title,
            color = BlackPrimary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.height(40.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "$${product.price}", color = BlackPrimary)

        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
        DynamicAddButton(
            quantity = product.quantity,
            onAdd = onAdd,
            onIncrease = onIncrease,
            onDecrease = onDecrease
        )
        FavoriteIcon(
            isFavorite = isFavorite,
            onClick = onToggleFavorite
        )
    }
}

}