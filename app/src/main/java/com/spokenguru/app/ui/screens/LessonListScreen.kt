package com.spokenguru.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spokenguru.app.data.model.LessonEntity
import com.spokenguru.app.data.repository.LessonRepository
import com.spokenguru.app.ui.components.DuoButton
import com.spokenguru.app.ui.components.DuoButtonColor
import com.spokenguru.app.ui.theme.DuoBlue
import com.spokenguru.app.ui.theme.DuoDarkCardBorder
import com.spokenguru.app.ui.theme.DuoDarkSurface
import com.spokenguru.app.ui.theme.DuoDarkText
import com.spokenguru.app.ui.theme.DuoGray
import com.spokenguru.app.ui.theme.DuoGrayBorder
import com.spokenguru.app.ui.theme.DuoGrayText
import com.spokenguru.app.ui.theme.DuoGreen
import com.spokenguru.app.ui.theme.DuoRed
import com.spokenguru.app.ui.theme.DuoTextDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonListScreen(
    repository: LessonRepository,
    onSelectLesson: (Int) -> Unit,
    onOpenMistakes: () -> Unit,
    onOpenImport: () -> Unit
) {
    val lessons by repository.allLessons.collectAsState(initial = emptyList())
    val mistakesCount by repository.mistakesCount.collectAsState(initial = 0)
    val isDark = isSystemInDarkTheme()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(DuoGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Book,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Spoken Guru",
                                fontWeight = FontWeight.Bold,
                                fontSize = 19.sp
                            )
                            Text(
                                text = "Duolingo-style English Practice",
                                fontSize = 12.sp,
                                color = DuoGrayText
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onOpenImport) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Manage",
                            tint = if (isDark) DuoDarkText else DuoTextDark
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDark) DuoDarkSurface else Color.White
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Mistakes Review Banner if user has any mistakes
            if (mistakesCount > 0) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isDark) Color(0xFF2A1C1D) else Color(0xFFFFECEC)
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, DuoRed)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Revise Mistakes ($mistakesCount)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = DuoRed
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Jo sawal galat hue the unhe dobara practice karein",
                                    fontSize = 13.sp,
                                    color = if (isDark) DuoDarkText else DuoTextDark
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            DuoButton(
                                text = "PRACTICE",
                                onClick = onOpenMistakes,
                                color = DuoButtonColor.RED,
                                height = 40.dp,
                                modifier = Modifier.width(120.dp)
                            )
                        }
                    }
                }
            }

            // Section Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ALL LESSONS (1 – 40)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        letterSpacing = 1.sp,
                        color = DuoGrayText
                    )
                    Text(
                        text = "Index Practice",
                        fontSize = 12.sp,
                        color = DuoGrayText
                    )
                }
            }

            // Lessons List
            items(lessons, key = { it.lessonNumber }) { lesson ->
                LessonCard(
                    lesson = lesson,
                    onClick = {
                        if (lesson.isAvailable) {
                            onSelectLesson(lesson.lessonNumber)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun LessonCard(
    lesson: LessonEntity,
    onClick: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val isAvailable = lesson.isAvailable
    val isCompleted = lesson.completedQuestions > 0 && lesson.completedQuestions >= lesson.totalQuestions

    val cardBg = when {
        !isAvailable -> if (isDark) Color(0xFF162126) else DuoGray
        isCompleted -> if (isDark) Color(0xFF1A2E20) else Color(0xFFF1FBEA)
        else -> if (isDark) DuoDarkSurface else Color.White
    }

    val borderColor = when {
        !isAvailable -> if (isDark) DuoDarkCardBorder else DuoGrayBorder
        isCompleted -> DuoGreen
        else -> if (isDark) DuoDarkCardBorder else DuoGrayBorder
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isAvailable, onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circle Number Badge
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                !isAvailable -> if (isDark) Color(0xFF233238) else Color(0xFFE2E2E2)
                                isCompleted -> DuoGreen
                                else -> DuoBlue
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else if (!isAvailable) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = DuoGrayText,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text(
                            text = "${lesson.lessonNumber}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Lesson ${lesson.lessonNumber}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isAvailable) DuoBlue else DuoGrayText
                    )
                    Text(
                        text = lesson.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            !isAvailable -> DuoGrayText
                            isDark -> DuoDarkText
                            else -> DuoTextDark
                        }
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isAvailable) {
                            "${lesson.totalQuestions} Sentences • Book Page ${lesson.pageNumber}"
                        } else {
                            "Book Page ${lesson.pageNumber} • Coming Soon"
                        },
                        fontSize = 12.sp,
                        color = DuoGrayText
                    )
                }

                if (isAvailable) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(DuoGreen.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Start",
                            tint = DuoGreen,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            // Progress bar if available
            if (isAvailable && lesson.totalQuestions > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                val progress = (lesson.completedQuestions.toFloat() / lesson.totalQuestions).coerceIn(0f, 1f)
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = DuoGreen,
                    trackColor = if (isDark) Color(0xFF26373F) else DuoGrayBorder
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (lesson.completedQuestions == 0) "Tap to start" else "Completed: ${lesson.completedQuestions}/${lesson.totalQuestions}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isCompleted) DuoGreen else DuoGrayText
                    )
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCompleted) DuoGreen else DuoGrayText
                    )
                }
            }
        }
    }
}
