package com.spokenguru.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spokenguru.app.ui.theme.GuruCardBorder
import com.spokenguru.app.ui.theme.GuruDarkCardBorder
import com.spokenguru.app.ui.theme.GuruDarkSurface
import com.spokenguru.app.ui.theme.GuruDarkText
import com.spokenguru.app.ui.theme.GuruPurple
import com.spokenguru.app.ui.theme.GuruPurpleBorder
import com.spokenguru.app.ui.theme.GuruTextDark

/**
 * Word Chip in Answer Slot: Clean pill WITHOUT the 'X' cross icon.
 * Tapping the word chip itself removes it back to the bank.
 */
@Composable
fun SlotWordChip(
    text: String,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val shape = RoundedCornerShape(20.dp)

    Box(
        modifier = modifier
            .shadow(1.dp, shape)
            .clip(shape)
            .background(if (isDark) GuruDarkSurface else Color.White)
            .border(1.dp, if (isDark) GuruDarkCardBorder else GuruPurpleBorder, shape)
            .clickable(onClick = onRemove)
            .padding(horizontal = 16.dp, vertical = 9.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = if (isDark) GuruDarkText else GuruTextDark
        )
    }
}

/**
 * Word Tile in Word Bank: Pure English word ONLY (No Hindi subtitle).
 */
@Composable
fun BankWordTile(
    word: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val shape = RoundedCornerShape(16.dp)

    if (isSelected) {
        // Placeholder / Disabled state inside bank
        Box(
            modifier = modifier
                .defaultMinSize(minWidth = 72.dp, minHeight = 48.dp)
                .clip(shape)
                .background(if (isDark) Color(0xFF141324) else Color(0xFFF9F8FD))
                .border(
                    width = 1.dp,
                    color = if (isDark) Color(0xFF242142) else Color(0xFFE5E2F8),
                    shape = shape
                )
                .padding(horizontal = 16.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = word,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = if (isDark) Color(0xFF38355B) else Color(0xFFC7C4E2)
            )
        }
    } else {
        // Active clickable English word tile
        Box(
            modifier = modifier
                .defaultMinSize(minWidth = 72.dp, minHeight = 48.dp)
                .shadow(2.dp, shape, ambientColor = GuruPurple.copy(alpha = 0.1f))
                .clip(shape)
                .background(if (isDark) GuruDarkSurface else Color.White)
                .border(1.dp, if (isDark) GuruDarkCardBorder else GuruCardBorder, shape)
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = word,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = if (isDark) GuruDarkText else GuruTextDark
            )
        }
    }
}
