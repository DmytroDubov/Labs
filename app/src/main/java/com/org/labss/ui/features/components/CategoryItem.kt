// File: app/src/main/java/com/org/labs/ui/features/components/CategoryItem.kt
package com.org.labs.ui.features.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.org.labs.ui.theme.BlackPrimary
import com.org.labs.ui.theme.LightGraySurface
import com.org.labs.ui.theme.MediumGrayText

@Composable
fun CategoryItem(
    title: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier.clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(LightGraySurface, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Category,
                contentDescription = title,
                tint = BlackPrimary
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = title, color = MediumGrayText)
    }
}