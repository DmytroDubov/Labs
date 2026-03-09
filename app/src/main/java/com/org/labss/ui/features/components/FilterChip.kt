package com.org.labss.ui.features.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.org.labss.ui.theme.BlackPrimary
import com.org.labss.ui.theme.LightGraySurface
import com.org.labss.ui.theme.White

@Composable
fun FilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Text(
        text = text,
        color = if (selected) White else BlackPrimary,
        modifier = Modifier
            .background(
                color = if (selected) BlackPrimary else LightGraySurface,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp)
    )
}