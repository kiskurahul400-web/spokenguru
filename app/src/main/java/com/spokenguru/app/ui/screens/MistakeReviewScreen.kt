package com.spokenguru.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import com.spokenguru.app.ui.theme.isAppDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.spokenguru.app.audio.SoundEffectManager
import com.spokenguru.app.audio.TtsManager
import com.spokenguru.app.data.model.QuestionEntity
import com.spokenguru.app.data.repository.LessonRepository
import com.spokenguru.app.ui.components.BankWordTile
import com.spokenguru.app.ui.components.GuruButton
import com.spokenguru.app.ui.components.GuruButtonVariant
import com.spokenguru.app.ui.components.SlotWordChip
import com.spokenguru.app.ui.theme.GuruBg
import com.spokenguru.app.ui.theme.GuruCardBorder
import com.spokenguru.app.ui.theme.GuruCardBorderDashed
import com.spokenguru.app.ui.theme.GuruDarkBg
import com.spokenguru.app.ui.theme.GuruDarkCardBorder
import com.spokenguru.app.ui.theme.GuruDarkSurface
import com.spokenguru.app.ui.theme.GuruDarkText
import com.spokenguru.app.ui.theme.GuruMint
import com.spokenguru.app.ui.theme.GuruMintLight
import com.spokenguru.app.ui.theme.GuruPurple
import com.spokenguru.app.ui.theme.GuruPurpleLight
import com.spokenguru.app.ui.theme.GuruRed
import com.spokenguru.app.ui.theme.GuruRedLight
import com.spokenguru.app.ui.theme.GuruTextDark
import com.spokenguru.app.ui.theme.GuruTextMuted
import com.spokenguru.app.ui.theme.GuruTextSubtle
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MistakeReviewScreen(
    repository: LessonRepository,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val isDark = isAppDarkTheme()
    val scope = rememberCoroutineScope()
    val gson = remember { Gson() }

    val ttsManager = remember { TtsManager(context) }
    DisposableEffect(Unit) {
        onDispose { ttsManager.shutdown() }
    }

    val mistakes by repository.allMistakes.collectAsState(initial = emptyList())
    var currentIndex by remember { mutableIntStateOf(0) }
    var answerState by remember { mutableStateOf(AnswerState.IDLE) }

    val bankWords = remember { mutableStateListOf<BankWordItem>() }
    val selectedWords = remember { mutableStateListOf<BankWordItem>() }

    LaunchedEffect(currentIndex, mistakes) {
        if (mistakes.isNotEmpty() && currentIndex < mistakes.size) {
            val q = mistakes[currentIndex]
            selectedWords.clear()
            answerState = AnswerState.IDLE

            val correctWords = q.primaryAnswer.split("\\s+".toRegex()).map { cleanWordPunctuation(it) }.filter { it.isNotBlank() }
            val distractorType = object : TypeToken<List<String>>() {}.type
            val distractors: List<String> = try {
                gson.fromJson(q.distractorsJson, distractorType) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }

            val list = mutableListOf<BankWordItem>()
            correctWords.forEachIndexed { idx, w ->
                list.add(BankWordItem(id = idx, word = w, isDistractor = false))
            }
            distractors.take(3).forEachIndexed { idx, w ->
                list.add(BankWordItem(id = 100 + idx, word = w, isDistractor = true))
            }

            bankWords.clear()
            bankWords.addAll(list.shuffled())
        }
    }

    if (mistakes.isEmpty()) {
        Scaffold(containerColor = if (isDark) GuruDarkBg else GuruBg) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(GuruMintLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = GuruMint,
                            modifier = Modifier.size(42.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "All Mistakes Cleared!",
                        fontFamily = FontFamily.Serif,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) GuruDarkText else GuruTextDark
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Sabhi galat sawal theek ho gaye hain. Shabaash!",
                        fontSize = 14.sp,
                        color = GuruTextMuted
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    GuruButton(
                        text = "Back to Lessons",
                        onClick = onNavigateBack,
                        height = 48.dp,
                        modifier = Modifier.width(200.dp)
                    )
                }
            }
        }
        return
    }

    val currentQuestion = if (currentIndex < mistakes.size) mistakes[currentIndex] else mistakes.first()

    Scaffold(containerColor = if (isDark) GuruDarkBg else GuruBg) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Header with Back and Prev/Next
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = if (isDark) GuruDarkText else GuruTextDark
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Column {
                        Text(
                            text = "REVISE MISTAKES",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GuruRed,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Mistake #${currentIndex + 1} of ${mistakes.size}",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = if (isDark) GuruDarkText else GuruTextDark
                        )
                    }
                }

                // Prev / Next icons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { if (currentIndex > 0) currentIndex-- },
                        enabled = currentIndex > 0
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = "Prev",
                            tint = if (currentIndex > 0) GuruPurple else GuruTextSubtle
                        )
                    }
                    IconButton(
                        onClick = { if (currentIndex < mistakes.size - 1) currentIndex++ },
                        enabled = currentIndex < mistakes.size - 1
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Next",
                            tint = if (currentIndex < mistakes.size - 1) GuruPurple else GuruTextSubtle
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Hindi Question Card
            val cardShape = RoundedCornerShape(22.dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp, cardShape)
                    .clip(cardShape)
                    .background(if (isDark) GuruDarkSurface else Color.White)
                    .border(1.dp, if (isDark) GuruDarkCardBorder else GuruCardBorder, cardShape)
                    .padding(18.dp)
            ) {
                Column {
                    Text(
                        text = currentQuestion.hindiText,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) GuruDarkText else GuruTextDark,
                        lineHeight = 28.sp
                    )
                    if (currentQuestion.romanText.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "(${currentQuestion.romanText})",
                            fontSize = 14.sp,
                            color = GuruTextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Answer Slot Card (No 'X' cross icon)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp, cardShape)
                    .clip(cardShape)
                    .background(if (isDark) GuruDarkSurface else Color.White)
                    .border(1.dp, if (isDark) GuruDarkCardBorder else GuruCardBorder, cardShape)
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "YOUR CORRECTION",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = GuruPurple,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val slotShape = RoundedCornerShape(16.dp)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 100.dp)
                            .clip(slotShape)
                            .background(if (isDark) Color(0xFF131222) else Color(0xFFFAF9FF))
                            .border(1.5.dp, GuruCardBorderDashed, slotShape)
                            .padding(12.dp)
                    ) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            selectedWords.forEach { item ->
                                SlotWordChip(
                                    text = item.word,
                                    onRemove = {
                                        if (answerState == AnswerState.IDLE) {
                                            SoundEffectManager.playClick()
                                            selectedWords.remove(item)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Word Bank (Clean English Words Only)
            Text(
                text = "WORD BANK",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = GuruPurple,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                bankWords.forEach { item ->
                    val isSelected = selectedWords.any { it.id == item.id }
                    BankWordTile(
                        word = item.word,
                        isSelected = isSelected,
                        onClick = {
                            if (answerState == AnswerState.IDLE && !isSelected) {
                                SoundEffectManager.playClick()
                                selectedWords.add(item)
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Bottom Actions
            if (answerState == AnswerState.IDLE) {
                GuruButton(
                    text = "Verify Correction",
                    icon = Icons.Default.Check,
                    enabled = selectedWords.isNotEmpty(),
                    onClick = {
                        val userSentence = selectedWords.joinToString(" ") { it.word }
                        val isCorrect = checkAnswer(
                            userSentence = userSentence,
                            primary = currentQuestion.primaryAnswer,
                            alternatesJson = currentQuestion.alternateAnswersJson,
                            gson = gson
                        )

                        if (isCorrect) {
                            answerState = AnswerState.CORRECT
                            SoundEffectManager.playSuccess()
                            ttsManager.speak(currentQuestion.primaryAnswer)
                            scope.launch {
                                repository.markMistake(currentQuestion.id, false)
                            }
                        } else {
                            answerState = AnswerState.INCORRECT
                            SoundEffectManager.playError()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            } else if (answerState == AnswerState.CORRECT) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(GuruMintLight)
                        .padding(14.dp)
                ) {
                    Column {
                        Text(
                            text = "Correct! Mistake Cleared 🎉",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = GuruMint
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        GuruButton(
                            text = "Next Mistake →",
                            onClick = {
                                if (currentIndex < mistakes.size - 1) {
                                    currentIndex++
                                } else {
                                    currentIndex = 0
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            height = 46.dp
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(GuruRedLight)
                        .padding(14.dp)
                ) {
                    Column {
                        Text(
                            text = "Still incorrect: ${currentQuestion.primaryAnswer}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = GuruRed
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        GuruButton(
                            text = "Try Again",
                            icon = Icons.Default.Refresh,
                            onClick = { answerState = AnswerState.IDLE },
                            modifier = Modifier.fillMaxWidth(),
                            height = 46.dp
                        )
                    }
                }
            }
        }
    }
}
