package com.spokenguru.app.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spokenguru.app.ui.theme.DuoBlue
import com.spokenguru.app.ui.theme.DuoBlueDark
import com.spokenguru.app.ui.theme.DuoGray
import com.spokenguru.app.ui.theme.DuoGrayBorder
import com.spokenguru.app.ui.theme.DuoGrayText
import com.spokenguru.app.ui.theme.DuoGreen
import com.spokenguru.app.ui.theme.DuoGreenDark
import com.spokenguru.app.ui.theme.DuoRed
import com.spokenguru.app.ui.theme.DuoRedDark

enum class DuoButtonColor {
    GREEN,
    BLUE,
    RED,
    GRAY
}

@Composable
fun DuoButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: DuoButtonColor = DuoButtonColor.GREEN,
    enabled: Boolean = true,
    height: Dp = 50.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val (faceColor, shadowColor, textColor) = when {
        !enabled -> Triple(DuoGray, DuoGrayBorder, DuoGrayText)
        color == DuoButtonColor.GREEN -> Triple(DuoGreen, DuoGreenDark, Color.White)
        color == DuoButtonColor.BLUE -> Triple(DuoBlue, DuoBlueDark, Color.White)
        color == DuoButtonColor.RED -> Triple(DuoRed, DuoRedDark, Color.White)
        color == DuoButtonColor.GRAY -> Triple(Color(0xFFE5E5E5), Color(0xFFC7C7C7), Color(0xFF4B4B4B))
        else -> Triple(DuoGreen, DuoGreenDark, Color.White)
    }

    val shadowDepth = 4.dp
    val pressOffset by animateDpAsState(
        targetValue = if (isPressed && enabled) shadowDepth else 0.dp,
        label = "ButtonPress"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height + shadowDepth)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            ),
        contentAlignment = Alignment.TopCenter
    ) {
        // Bottom 3D shadow layer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .offset(y = shadowDepth)
                .clip(RoundedCornerShape(16.dp))
                .background(shadowColor)
        )

        // Top button face
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .offset(y = pressOffset)
                .clip(RoundedCornerShape(16.dp))
                .background(faceColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text.uppercase(),
                color = textColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )
        }
    }
}
