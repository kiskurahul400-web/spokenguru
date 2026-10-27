package com.spokenguru.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spokenguru.app.ui.theme.DuoDarkCardBorder
import com.spokenguru.app.ui.theme.DuoDarkSurface
import com.spokenguru.app.ui.theme.DuoDarkText
import com.spokenguru.app.ui.theme.DuoGray
import com.spokenguru.app.ui.theme.DuoGrayBorder
import com.spokenguru.app.ui.theme.DuoGrayBorderDark
import com.spokenguru.app.ui.theme.DuoTextDark

@Composable
fun WordChip(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPlaceholder: Boolean = false,
    isInSlot: Boolean = false
) {
    val isDark = isSystemInDarkTheme()
    val cornerShape = RoundedCornerShape(12.dp)
    val shadowHeight = 3.dp

    if (isPlaceholder) {
        // Disabled ghost placeholder inside the bank
        Box(
            modifier = modifier
                .height(44.dp)
                .clip(cornerShape)
                .background(if (isDark) Color(0xFF162228) else DuoGray)
                .border(
                    width = 1.5.dp,
                    color = if (isDark) DuoDarkCardBorder else DuoGrayBorder,
                    shape = cornerShape
                )
                .padding(horizontal = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = Color.Transparent,
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium
            )
        }
    } else {
        val faceBg = if (isDark) DuoDarkSurface else Color.White
        val shadowBg = if (isDark) Color(0xFF131D22) else DuoGrayBorderDark
        val borderColor = if (isDark) DuoDarkCardBorder else DuoGrayBorder
        val textColor = if (isDark) DuoDarkText else DuoTextDark

        Box(
            modifier = modifier
                .height(44.dp + shadowHeight)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.TopCenter
        ) {
            // Shadow
            Box(
                modifier = Modifier
                    .wrapContentWidth()
                    .height(44.dp)
                    .offset(y = shadowHeight)
                    .clip(cornerShape)
                    .background(shadowBg)
            ) {
                // Invisible spacer for matching width
                Text(
                    text = text,
                    color = Color.Transparent,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 14.dp)
                )
            }

            // Top Chip Face
            Box(
                modifier = Modifier
                    .wrapContentWidth()
                    .height(44.dp)
                    .clip(cornerShape)
                    .background(faceBg)
                    .border(1.5.dp, borderColor, cornerShape)
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text,
                    color = textColor,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
