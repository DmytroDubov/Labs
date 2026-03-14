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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.org.labss.ui.theme.BlackPrimary
import com.org.labss.ui.theme.LightGraySurface
import com.org.labss.ui.theme.MediumGrayText

@Composable
fun HomeScreen(
    state: ProductUiState,
    onEvent: (ProductEvent) -> Unit,
    onNavigateToSearch: (String, String?) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        CustomSearchBar(
            value = state.query,
            onValueChange = { onEvent(ProductEvent.OnSearchQueryChanged(it)) },
            readOnly = false,
            onClick = { onNavigateToSearch(state.query, state.selectedCategory) },
            onSearchAction = { query ->
                val effectiveQuery = query.ifBlank {
                    state.popularProducts.firstOrNull()?.title
                        ?: state.products.firstOrNull()?.title
                        ?: "Капучино"
                }
                onEvent(ProductEvent.OnSearchQueryChanged(effectiveQuery))
                onEvent(ProductEvent.OnSearchSubmitted(effectiveQuery, state.products.size))
                onNavigateToSearch(effectiveQuery, state.selectedCategory)
            }
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(
                top = 16.dp,
                bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()+6.dp
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp)


        ) {

            item{
                Text(
                    text = "Last gateway",
                    color = BlackPrimary,
                    style = MaterialTheme.typography.titleLarge
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.9f)
                        .background(LightGraySurface, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {

                    Spacer(modifier = Modifier.height(12.dp))
                    val bannerImageUrl = state.popularProducts.firstOrNull()?.imageUrl ?: ""

                    ProductImage(
                        imageUrl = bannerImageUrl,
                        modifier = Modifier
                            .height(120.dp)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    )

                }
            }


            item {
                Text(
                    text = "Popular categories",
                    color = BlackPrimary,
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(state.categoryObjects.take(4)) { category ->
                        CategoryItem(
                            category = category
                        ) {
                            onEvent(ProductEvent.OnCategorySelected(category.name))
                            onNavigateToSearch("", category.name)
                        }
                    }
                }
            }
            item {
            if (state.isBuyAll) {
                TextButton(onClick = { onEvent(ProductEvent.OnClearSearchHistory) }) {
                    Text(text = "Buy all", color = MediumGrayText)
                }
            } }
            item {
                Text(text = "Popular products", color = BlackPrimary,style = MaterialTheme.typography.titleLarge)
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
                                        onToggleFavorite = { onEvent(ProductEvent.OnToggleFavorite(product.id)) }
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
}
}

@Composable
private fun ProductGridCard(
    product: Product,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(LightGraySurface, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            ProductImage(
                imageUrl = product.imageUrl,
                modifier = Modifier.fillMaxSize()
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = product.title,
                    color = BlackPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "$${product.price}",
                    color = BlackPrimary,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            FavoriteIcon(
                isFavorite = isFavorite,
                onClick = onToggleFavorite
            )
        }
    }
}