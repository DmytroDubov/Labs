package com.org.labss.ui.features.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.org.labss.domain.model.Category // 🌟 Ваш новий імпорт
import com.org.labss.ui.theme.BlackPrimary
import com.org.labss.ui.theme.LightGraySurface

@Composable
fun CategoryItem(
    category: Category, // 🌟 Приймаємо цілий об'єкт замість просто рядка
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier.clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(LightGraySurface, CircleShape)
                .clip(CircleShape), // Гарантуємо, що всередині все буде круглим
            contentAlignment = Alignment.Center
        ) {
            // 🌟 Використовуємо ваш ProductImage для завантаження картинки
            ProductImage(
                imageUrl = category.imageUrl, // Беремо посилання з об'єкта
                modifier = Modifier.fillMaxSize(),
                shape = CircleShape // Передаємо круглу форму
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = category.name, // Беремо назву з об'єкта
            color = BlackPrimary,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium
        )
    }
}