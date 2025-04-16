package com.cirin0.orderflowmobile.presentation.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun StyledButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    content: @Composable (() -> Unit),
    enabled: Boolean = true,
) {
    OutlinedButton(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Black
        ),
//        border = BorderStroke(
//            width = 1.dp,
//            color = Color.Black
//        ),
        enabled = enabled,
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp)
        ) {
            content()
        }
    }
}
