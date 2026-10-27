package com.spokenguru.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
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
import com.spokenguru.app.ui.theme.GuruCardBorderDashed
import com.spokenguru.app.ui.theme.GuruDarkCardBorder
import com.spokenguru.app.ui.theme.GuruDarkSurface
import com.spokenguru.app.ui.theme.GuruDarkText
import com.spokenguru.app.ui.theme.GuruDarkTextMuted
import com.spokenguru.app.ui.theme.GuruPurple
import com.spokenguru.app.ui.theme.GuruPurpleBorder
import com.spokenguru.app.ui.theme.GuruPurpleLight
import com.spokenguru.app.ui.theme.GuruTextDark
import com.spokenguru.app.ui.theme.GuruTextMuted
import com.spokenguru.app.ui.theme.GuruTextSubtle

/**
 * Word Chip in Answer Slot (Pill with 'X' icon)
 */
@Composable
fun SlotWordChip(
    text: String,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val shape = RoundedCornerShape(20.dp)

    Row(
        modifier = modifier
            .shadow(1.dp, shape)
            .clip(shape)
            .background(if (isDark) GuruDarkSurface else Color.White)
            .border(1.dp, if (isDark) GuruDarkCardBorder else GuruPurpleBorder, shape)
            .clickable(onClick = onRemove)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = text,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = if (isDark) GuruDarkText else GuruTextDark
        )
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .size(18.dp)
                .clip(CircleShape)
                .background(if (isDark) Color(0xFF28254A) else GuruPurpleLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove",
                tint = GuruPurple,
                modifier = Modifier.size(12.dp)
            )
        }
    }
}

/**
 * Word Tile in Word Bank (Card with English Word + Hindi Subtitle underneath)
 */
@Composable
fun BankWordTile(
    word: String,
    hindiGloss: String,
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
                .defaultMinSize(minWidth = 90.dp, minHeight = 64.dp)
                .clip(shape)
                .background(if (isDark) Color(0xFF141324) else Color(0xFFF9F8FD))
                .border(
                    width = 1.dp,
                    color = if (isDark) Color(0xFF242142) else Color(0xFFE5E2F8),
                    shape = shape
                )
                .padding(horizontal = 14.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = word,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = if (isDark) Color(0xFF38355B) else Color(0xFFC7C4E2)
                )
                if (hindiGloss.isNotBlank() && hindiGloss != "-") {
                    Text(
                        text = hindiGloss,
                        fontSize = 11.sp,
                        color = if (isDark) Color(0xFF38355B) else Color(0xFFC7C4E2)
                    )
                }
            }
        }
    } else {
        // Active clickable word tile
        Box(
            modifier = modifier
                .defaultMinSize(minWidth = 90.dp, minHeight = 64.dp)
                .shadow(2.dp, shape, ambientColor = GuruPurple.copy(alpha = 0.1f))
                .clip(shape)
                .background(if (isDark) GuruDarkSurface else Color.White)
                .border(1.dp, if (isDark) GuruDarkCardBorder else GuruCardBorder, shape)
                .clickable(onClick = onClick)
                .padding(horizontal = 14.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = word,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = if (isDark) GuruDarkText else GuruTextDark
                )
                if (hindiGloss.isNotBlank() && hindiGloss != "-") {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = hindiGloss,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = GuruPurple
                    )
                }
            }
        }
    }
}
