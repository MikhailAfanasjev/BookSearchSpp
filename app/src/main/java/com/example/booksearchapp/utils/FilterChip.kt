package com.example.booksearchapp.utils

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.booksearchapp.presentation.ui.theme.DarkGray
import com.example.booksearchapp.presentation.ui.theme.UltraLightGray
import com.example.linguareader.R


/**
 * Пример простого «чипа» (ярлыка) для выбранных фильтров.
 * Можно заменить на Material3 FilterChip/AssistChip,
 * если хотите использовать готовые компоненты.
 */
@Composable
fun FilterChip(
    text: String,
    onRemove: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color.White, // фон белый
        border = BorderStroke(2.dp, colorResource(id = R.color.blue)),
        contentColor = Color.Black,
        modifier = Modifier.padding(end = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(text = text, style = TextStyle(fontSize = 14.sp))
            Spacer(modifier = Modifier.width(4.dp))
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(18.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = "Remove filter",
                    tint = DarkGray
                )
            }
        }
    }
}