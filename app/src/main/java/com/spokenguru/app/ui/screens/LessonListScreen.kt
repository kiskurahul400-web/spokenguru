package com.spokenguru.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import com.spokenguru.app.ui.theme.isAppDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.platform.LocalContext
import com.spokenguru.app.ui.theme.AppThemePreset
import com.spokenguru.app.ui.theme.DarkModePreference
import com.spokenguru.app.ui.theme.ThemeState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spokenguru.app.data.model.LessonEntity
import com.spokenguru.app.data.repository.LessonRepository
import com.spokenguru.app.ui.components.AppBottomNavigation
import com.spokenguru.app.ui.components.NavTab
import com.spokenguru.app.ui.theme.GuruAmber
import com.spokenguru.app.ui.theme.GuruAmberLight
import com.spokenguru.app.ui.theme.GuruBg
import com.spokenguru.app.ui.theme.GuruBlue
import com.spokenguru.app.ui.theme.GuruBlueLight
import com.spokenguru.app.ui.theme.GuruCardBorder
import com.spokenguru.app.ui.theme.GuruDarkBg
import com.spokenguru.app.ui.theme.GuruDarkCardBorder
import com.spokenguru.app.ui.theme.GuruDarkSurface
import com.spokenguru.app.ui.theme.GuruDarkText
import com.spokenguru.app.ui.theme.GuruDarkTextMuted
import com.spokenguru.app.ui.theme.GuruMint
import com.spokenguru.app.ui.theme.GuruMintLight
import com.spokenguru.app.ui.theme.GuruPurple
import com.spokenguru.app.ui.theme.GuruPurpleLight
import com.spokenguru.app.ui.theme.GuruRed
import com.spokenguru.app.ui.theme.GuruRedLight
import com.spokenguru.app.ui.theme.GuruTextDark
import com.spokenguru.app.ui.theme.GuruTextMuted
import com.spokenguru.app.ui.theme.GuruTextSubtle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonListScreen(
    repository: LessonRepository,
    currentTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    onSelectLesson: (Int) -> Unit
) {
    val lessons by repository.allLessons.collectAsState(initial = emptyList())
    val mistakesCount by repository.mistakesCount.collectAsState(initial = 0)
    val isDark = isAppDarkTheme()
    var showAllChapters by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            AppBottomNavigation(
                currentTab = currentTab,
                onTabSelected = onTabSelected
            )
        },
        containerColor = if (isDark) GuruDarkBg else GuruBg
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Top App Bar matching reference
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .shadow(1.dp, CircleShape)
                            .clip(CircleShape)
                            .background(if (isDark) GuruDarkSurface else Color.White)
                            .size(42.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = if (isDark) GuruDarkText else GuruTextDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = GuruPurple,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "English Practice Guru",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = if (isDark) GuruDarkText else GuruTextDark
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Quick Theme Palette Switcher
                        Box(
                            modifier = Modifier
                                .shadow(1.dp, CircleShape)
                                .clip(CircleShape)
                                .background(if (isDark) GuruDarkSurface else Color.White)
                                .clickable { showThemeDialog = true }
                                .size(42.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = "Change Theme",
                                tint = GuruPurple,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Search Button
                        Box(
                            modifier = Modifier
                                .shadow(1.dp, CircleShape)
                                .clip(CircleShape)
                                .background(if (isDark) GuruDarkSurface else Color.White)
                                .size(42.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = if (isDark) GuruDarkText else GuruTextDark,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // Header: Lessons & Chapters
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Lessons & Chapters",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 26.sp,
                            color = if (isDark) GuruDarkText else GuruTextDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Bilingual English & Hindi practice lessons from eBook 1",
                            fontSize = 14.sp,
                            color = if (isDark) GuruDarkTextMuted else GuruTextMuted,
                            lineHeight = 20.sp
                        )
                    }
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filter",
                            tint = if (isDark) GuruDarkText else GuruTextDark
                        )
                    }
                }
            }

            // Mistakes banner if any
            if (mistakesCount > 0) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isDark) Color(0xFF2E1922) else GuruRedLight
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GuruRed.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Pending Revisions: $mistakesCount Mistakes",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = GuruRed
                                )
                                Text(
                                    text = "Inhe dubara practice karke clear karein",
                                    fontSize = 12.sp,
                                    color = if (isDark) GuruDarkTextMuted else GuruTextMuted
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(GuruRed)
                                    .clickable { onTabSelected(NavTab.PROFILE) }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = "Review >",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }

            // Display lessons
            val visibleLessons = if (showAllChapters) lessons else lessons.take(6)
            itemsIndexed(visibleLessons) { index, lesson ->
                LessonGuruCard(
                    lesson = lesson,
                    index = index,
                    onClick = {
                        if (lesson.isAvailable) {
                            onSelectLesson(lesson.lessonNumber)
                        }
                    }
                )
            }

            // "Browse All 40 Chapters" button
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .shadow(1.dp, RoundedCornerShape(20.dp))
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isDark) GuruDarkSurface else Color.White)
                            .border(1.dp, if (isDark) GuruDarkCardBorder else GuruCardBorder, RoundedCornerShape(20.dp))
                            .clickable { showAllChapters = !showAllChapters }
                            .padding(horizontal = 24.dp, vertical = 12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (showAllChapters) "Show Top Chapters ⌃" else "Browse All 40 Chapters ⌵",
                                color = GuruPurple,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }

    if (showThemeDialog) {
        ThemePickerDialog(
            onDismiss = { showThemeDialog = false }
        )
    }
}

@Composable
fun LessonGuruCard(
    lesson: LessonEntity,
    index: Int,
    onClick: () -> Unit
) {
    val isDark = isAppDarkTheme()
    val shape = RoundedCornerShape(22.dp)

    // Vibrant icons and color palette matching reference UI
    val (iconBg, iconTint, iconVector, hindiSub) = when (lesson.lessonNumber) {
        1 -> Quadruple(GuruPurpleLight, GuruPurple, Icons.Default.NearMe, "This, That, These & Those का प्रयोग")
        2 -> Quadruple(GuruBlueLight, GuruBlue, Icons.Default.Schedule, "समय बताना (Time telling in English)")
        3 -> Quadruple(Color(0xFFF3E8FF), Color(0xFF9333EA), Icons.Default.QuestionMark, "WH Family के शब्द (Interrogative)")
        4 -> Quadruple(GuruRedLight, GuruRed, Icons.Default.Edit, "क्रिया के तीन रूप (V1, V2, V3)")
        5 -> Quadruple(Color(0xFFE0F2FE), Color(0xFF0284C7), Icons.Default.Layers, "विशेषण की तीन अवस्थाएं (Degrees)")
        6 -> Quadruple(Color(0xFFF3E8FF), Color(0xFF9333EA), Icons.Default.Stars, "क्रिया विशेषण की तुलना (Adverbs Degrees)")
        else -> Quadruple(GuruPurpleLight, GuruPurple, Icons.Default.MenuBook, "English Practice Exercise")
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .shadow(2.dp, shape, ambientColor = GuruPurple.copy(alpha = 0.08f))
            .clip(shape)
            .background(if (isDark) GuruDarkSurface else Color.White)
            .border(1.dp, if (isDark) GuruDarkCardBorder else GuruCardBorder, shape)
            .clickable(enabled = lesson.isAvailable, onClick = onClick)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left rounded icon
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconVector,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Center details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Lesson ${String.format("%02d", lesson.lessonNumber)} • Page ${lesson.pageNumber}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isDark) GuruDarkTextMuted else GuruTextMuted
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = lesson.title,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = if (isDark) GuruDarkText else GuruTextDark
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = hindiSub,
                    fontSize = 12.sp,
                    color = if (isDark) GuruDarkTextMuted else GuruTextMuted
                )

                if (lesson.isAvailable && lesson.completedQuestions > 0) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        LinearProgressIndicator(
                            progress = { (lesson.completedQuestions.toFloat() / lesson.totalQuestions).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .weight(1f)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = GuruPurple,
                            trackColor = if (isDark) Color(0xFF28254A) else Color(0xFFEEECFD)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${lesson.completedQuestions}/${lesson.totalQuestions}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GuruPurple
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Right "Practice →" Pill Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (lesson.isAvailable) (if (isDark) Color(0xFF272449) else GuruPurpleLight) else (if (isDark) Color(0xFF1B1A2C) else Color(0xFFF3F3F7)))
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (lesson.isAvailable) "Practice" else "Soon",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (lesson.isAvailable) GuruPurple else GuruTextSubtle
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = if (lesson.isAvailable) Icons.Default.ArrowForward else Icons.Default.Lock,
                        contentDescription = null,
                        tint = if (lesson.isAvailable) GuruPurple else GuruTextSubtle,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Composable
fun ThemePickerDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val isDark = isAppDarkTheme()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = null,
                    tint = GuruPurple,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Theme & Mode (थीम व मोड)",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = if (isDark) GuruDarkText else GuruTextDark
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Display Mode
                Column {
                    Text(
                        text = "Display Mode (प्रदर्शन मोड):",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = if (isDark) GuruDarkText else GuruTextDark
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        DarkModePreference.values().forEach { mode ->
                            val isSelected = ThemeState.darkModePref == mode
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) GuruPurple else (if (isDark) Color(0xFF262447) else GuruPurpleLight)
                                    )
                                    .clickable {
                                        ThemeState.updateDarkMode(context, mode)
                                    }
                                    .padding(vertical = 10.dp, horizontal = 2.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${mode.icon} ${mode.label}",
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 12.sp,
                                    color = if (isSelected) Color.White else (if (isDark) GuruDarkText else GuruTextDark)
                                )
                            }
                        }
                    }
                }

                // Color Preset
                Column {
                    Text(
                        text = "Color Palette (4 रंग थीम):",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = if (isDark) GuruDarkText else GuruTextDark
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    val presets = AppThemePreset.values()
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        for (i in presets.indices step 2) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                for (j in 0..1) {
                                    if (i + j < presets.size) {
                                        val preset = presets[i + j]
                                        val isSelected = ThemeState.currentPreset == preset
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(
                                                    if (isSelected) (if (isDark) Color(0xFF292452) else Color(0xFFF3F0FE))
                                                    else (if (isDark) Color(0xFF1B1933) else Color(0xFFFBFBFE))
                                                )
                                                .border(
                                                    width = if (isSelected) 2.dp else 1.dp,
                                                    color = if (isSelected) preset.primaryColor else (if (isDark) Color(0xFF2C284C) else Color(0xFFE4E1F4)),
                                                    shape = RoundedCornerShape(12.dp)
                                                )
                                                .clickable {
                                                    ThemeState.updatePreset(context, preset)
                                                }
                                                .padding(10.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(20.dp)
                                                        .clip(CircleShape)
                                                        .background(preset.primaryColor)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Column {
                                                    Text(
                                                        text = preset.title,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 11.sp,
                                                        color = if (isDark) GuruDarkText else GuruTextDark
                                                    )
                                                    if (isSelected) {
                                                        Text(
                                                            text = "Active ✓",
                                                            fontSize = 10.sp,
                                                            color = preset.primaryColor,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("DONE", fontWeight = FontWeight.Bold, color = GuruPurple)
            }
        },
        containerColor = if (isDark) GuruDarkSurface else Color.White,
        shape = RoundedCornerShape(20.dp)
    )
}
