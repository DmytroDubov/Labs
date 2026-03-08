package com.org.labss.ui.features.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.org.labss.ui.theme.BlackPrimary
import com.org.labss.ui.theme.White


@Composable
fun DynamicAddButton(
    quantity: Int,
    onAdd: () -> Unit,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (quantity == 0) {
        Box(
            modifier = modifier
                .height(36.dp)
                .background(BlackPrimary, RoundedCornerShape(8.dp))
                .clickable { onAdd() }
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Add", color = White)
        }
    } else {
        Row(
            modifier = modifier
                .height(36.dp)
                .background(White, RoundedCornerShape(8.dp))
                .border(1.dp, BlackPrimary, RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "-", modifier = Modifier.clickable { onDecrease() })
            Text(text = quantity.toString())
            Text(text = "+", modifier = Modifier.clickable { onIncrease() })
        }
    }
}