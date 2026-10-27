package com.spokenguru.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.spokenguru.app.audio.SoundEffectManager
import com.spokenguru.app.audio.TtsManager
import com.spokenguru.app.data.model.QuestionEntity
import com.spokenguru.app.data.repository.LessonRepository
import com.spokenguru.app.ui.components.DuoButton
import com.spokenguru.app.ui.components.DuoButtonColor
import com.spokenguru.app.ui.components.WordChip
import com.spokenguru.app.ui.theme.DuoBlue
import com.spokenguru.app.ui.theme.DuoDarkCardBorder
import com.spokenguru.app.ui.theme.DuoDarkSlotBg
import com.spokenguru.app.ui.theme.DuoDarkSurface
import com.spokenguru.app.ui.theme.DuoDarkText
import com.spokenguru.app.ui.theme.DuoGray
import com.spokenguru.app.ui.theme.DuoGrayBorder
import com.spokenguru.app.ui.theme.DuoGrayText
import com.spokenguru.app.ui.theme.DuoGreen
import com.spokenguru.app.ui.theme.DuoGreenLight
import com.spokenguru.app.ui.theme.DuoRed
import com.spokenguru.app.ui.theme.DuoRedLight
import com.spokenguru.app.ui.theme.DuoTextDark
import com.spokenguru.app.ui.theme.DuoYellow
import kotlinx.coroutines.launch

data class BankWord(
    val id: Int,
    val word: String
)

enum class AnswerState {
    IDLE,
    CORRECT,
    INCORRECT
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WordPickScreen(
    lessonNumber: Int,
    repository: LessonRepository,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()
    val scope = rememberCoroutineScope()
    val gson = remember { Gson() }

    val ttsManager = remember { TtsManager(context) }
    DisposableEffect(Unit) {
        onDispose {
            ttsManager.shutdown()
        }
    }

    var questions by remember { mutableStateOf<List<QuestionEntity>>(emptyList()) }
    var currentIndex by remember { mutableIntStateOf(0) }
    var answerState by remember { mutableStateOf(AnswerState.IDLE) }
    var showHintDialog by remember { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }
    var isLessonCompleted by remember { mutableStateOf(false) }

    val bankWords = remember { mutableStateListOf<BankWord>() }
    val selectedWords = remember { mutableStateListOf<BankWord>() }

    // Load questions on first composition
    LaunchedEffect(lessonNumber) {
        val list = repository.getQuestionsForLessonSync(lessonNumber)
        if (list.isNotEmpty()) {
            questions = list
            // Find first uncompleted question to resume progress
            val firstUncompleted = list.indexOfFirst { !it.isCompleted }
            currentIndex = if (firstUncompleted != -1) firstUncompleted else 0
        }
    }

    // Set up word bank whenever current question changes
    LaunchedEffect(currentIndex, questions) {
        if (questions.isNotEmpty() && currentIndex < questions.size) {
            val q = questions[currentIndex]
            selectedWords.clear()
            answerState = AnswerState.IDLE

            // Split primary answer words (clean punctuation off each word for the chip)
            val correctWords = q.primaryAnswer.split("\\s+".toRegex()).map { cleanWordPunctuation(it) }.filter { it.isNotBlank() }

            // Parse distractors
            val distractorType = object : TypeToken<List<String>>() {}.type
            val distractors: List<String> = try {
                gson.fromJson(q.distractorsJson, distractorType) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }

            val combined = (correctWords + distractors).shuffled()
            bankWords.clear()
            combined.forEachIndexed { idx, word ->
                bankWords.add(BankWord(id = idx, word = word))
            }
        }
    }

    if (questions.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Loading questions...", fontSize = 16.sp, color = DuoGrayText)
        }
        return
    }

    val currentQuestion = questions[currentIndex]

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isDark) DuoDarkSurface else Color.White)
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { showExitDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Exit",
                            tint = DuoGrayText
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    val progress = ((currentIndex + 1).toFloat() / questions.size).coerceIn(0f, 1f)
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .weight(1f)
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        color = DuoGreen,
                        trackColor = if (isDark) Color(0xFF26373F) else DuoGrayBorder
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "${currentIndex + 1}/${questions.size}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = DuoGrayText
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .padding(bottom = 150.dp) // space for bottom sheet
            ) {
                // Section Title
                Text(
                    text = "TRANSLATE THIS SENTENCE",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = DuoGrayText
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Hindi Sentence + Roman
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = androidx.compose.material3.CardDefaults.cardColors(
                        containerColor = if (isDark) DuoDarkSurface else Color.White
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.5.dp,
                        if (isDark) DuoDarkCardBorder else DuoGrayBorder
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = currentQuestion.hindiText,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) DuoDarkText else DuoTextDark,
                            lineHeight = 28.sp
                        )

                        if (currentQuestion.romanText.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "(${currentQuestion.romanText})",
                                fontSize = 14.sp,
                                color = DuoGrayText
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Audio & Hint Buttons
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // TTS 1.0x Normal Speed
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(DuoBlue.copy(alpha = 0.15f))
                                    .border(1.dp, DuoBlue, RoundedCornerShape(12.dp))
                            ) {
                                IconButton(
                                    onClick = { ttsManager.speak(currentQuestion.primaryAnswer, isSlow = false) },
                                    modifier = Modifier.size(38.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VolumeUp,
                                        contentDescription = "Listen",
                                        tint = DuoBlue,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // TTS 0.6x Turtle Slow Speed
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(DuoYellow.copy(alpha = 0.15f))
                                    .border(1.dp, DuoYellow, RoundedCornerShape(12.dp))
                            ) {
                                IconButton(
                                    onClick = { ttsManager.speak(currentQuestion.primaryAnswer, isSlow = true) },
                                    modifier = Modifier.size(38.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Pets,
                                        contentDescription = "Slow Turtle Audio",
                                        tint = DuoYellow,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Hint Button
                            if (currentQuestion.hint.isNotBlank()) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(DuoGreen.copy(alpha = 0.15f))
                                        .border(1.dp, DuoGreen, RoundedCornerShape(12.dp))
                                ) {
                                    IconButton(
                                        onClick = { showHintDialog = true },
                                        modifier = Modifier.size(38.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Lightbulb,
                                            contentDescription = "Hint",
                                            tint = DuoGreen,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ANSWER SLOT AREA
                Text(
                    text = "YOUR ANSWER",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp,
                    color = DuoGrayText
                )
                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 110.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isDark) DuoDarkSlotBg else DuoGray)
                        .border(
                            width = 1.5.dp,
                            color = if (isDark) DuoDarkCardBorder else DuoGrayBorder,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(12.dp),
                    contentAlignment = if (selectedWords.isEmpty()) Alignment.Center else Alignment.TopStart
                ) {
                    if (selectedWords.isEmpty()) {
                        Text(
                            text = "Tap words below to assemble translation",
                            color = DuoGrayText,
                            fontSize = 14.sp
                        )
                    } else {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            selectedWords.forEach { item ->
                                WordChip(
                                    text = item.word,
                                    isInSlot = true,
                                    onClick = {
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

                Spacer(modifier = Modifier.height(24.dp))

                // WORD BANK AREA
                Text(
                    text = "WORD BANK",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp,
                    color = DuoGrayText
                )
                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    bankWords.forEach { item ->
                        val isSelected = selectedWords.any { it.id == item.id }
                        WordChip(
                            text = item.word,
                            isPlaceholder = isSelected,
                            onClick = {
                                if (answerState == AnswerState.IDLE && !isSelected) {
                                    SoundEffectManager.playClick()
                                    selectedWords.add(item)
                                }
                            }
                        )
                    }
                }
            }

            // BOTTOM SHEET / ACTION BAR
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            ) {
                if (answerState == AnswerState.IDLE) {
                    // Normal Check Button Container
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (isDark) DuoDarkSurface else Color.White)
                            .border(
                                width = 1.dp,
                                color = if (isDark) DuoDarkCardBorder else DuoGrayBorder,
                                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                            )
                            .padding(16.dp)
                    ) {
                        DuoButton(
                            text = "CHECK",
                            enabled = selectedWords.isNotEmpty(),
                            color = DuoButtonColor.GREEN,
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
                                        repository.markQuestionCompleted(currentQuestion.id, currentQuestion.lessonNumber)
                                        repository.markMistake(currentQuestion.id, false)
                                    }
                                } else {
                                    answerState = AnswerState.INCORRECT
                                    SoundEffectManager.playError()
                                    scope.launch {
                                        repository.markMistake(currentQuestion.id, true)
                                    }
                                }
                            }
                        )
                    }
                } else {
                    // Result Bottom Banner (Green / Red)
                    val isCorrect = answerState == AnswerState.CORRECT
                    val bannerBg = if (isCorrect) {
                        if (isDark) Color(0xFF1B3820) else DuoGreenLight
                    } else {
                        if (isDark) Color(0xFF381B1C) else DuoRedLight
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                            .background(bannerBg)
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (isCorrect) DuoGreen else DuoRed),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isCorrect) Icons.Default.Check else Icons.Default.Close,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (isCorrect) "बहुत बढ़िया! / Excellent!" else "गलत उत्तर / Correct Solution:",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCorrect) DuoGreen else DuoRed
                                )
                                if (!isCorrect) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = currentQuestion.primaryAnswer,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isDark) DuoDarkText else DuoTextDark
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        DuoButton(
                            text = "CONTINUE",
                            color = if (isCorrect) DuoButtonColor.GREEN else DuoButtonColor.RED,
                            onClick = {
                                if (currentIndex < questions.size - 1) {
                                    currentIndex++
                                } else {
                                    isLessonCompleted = true
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // Hint Dialog
    if (showHintDialog) {
        AlertDialog(
            onDismissRequest = { showHintDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = DuoGreen
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Vocabulary Hint", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Text(
                    text = currentQuestion.hint,
                    fontSize = 16.sp,
                    lineHeight = 22.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { showHintDialog = false }) {
                    Text(text = "GOT IT", fontWeight = FontWeight.Bold, color = DuoGreen)
                }
            }
        )
    }

    // Exit Confirmation Dialog
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text(text = "Exit Lesson?", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = "Aapka progress save ho chuka hai. Aap baad me wahi se resume kar sakte hain.",
                    fontSize = 15.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showExitDialog = false
                        onNavigateBack()
                    }
                ) {
                    Text(text = "EXIT", fontWeight = FontWeight.Bold, color = DuoRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text(text = "KEEP PRACTICING", fontWeight = FontWeight.Bold, color = DuoGreen)
                }
            }
        )
    }

    // Lesson Completed Celebration Dialog
    if (isLessonCompleted) {
        AlertDialog(
            onDismissRequest = { onNavigateBack() },
            title = { Text(text = "🎉 Lesson 1 Completed!", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = "Shabaash! Aapne Lesson 1 ke sabhi 101 questions successfully complete kar liye hain!",
                    fontSize = 15.sp
                )
            },
            confirmButton = {
                DuoButton(
                    text = "BACK TO LESSONS",
                    color = DuoButtonColor.GREEN,
                    height = 44.dp,
                    onClick = { onNavigateBack() }
                )
            }
        )
    }
}

/**
 * Flexible Answer Checker:
 * Strips punctuation, normalizes whitespace, compares against primary and alternate answers.
 */
fun checkAnswer(
    userSentence: String,
    primary: String,
    alternatesJson: String,
    gson: Gson
): Boolean {
    val cleanUser = cleanSentence(userSentence)
    val cleanPrimary = cleanSentence(primary)

    if (cleanUser == cleanPrimary) return true

    val listType = object : TypeToken<List<String>>() {}.type
    val alts: List<String> = try {
        gson.fromJson(alternatesJson, listType) ?: emptyList()
    } catch (e: Exception) {
        emptyList()
    }

    return alts.any { cleanSentence(it) == cleanUser }
}

fun cleanSentence(str: String): String {
    return str.lowercase()
        .replace(Regex("[^a-z0-9' ]"), "")
        .replace(Regex("\\s+"), " ")
        .trim()
}

fun cleanWordPunctuation(word: String): String {
    return word.replace(Regex("^[.,!?;:\"()]+|[.,!?;:\"()]+$"), "")
}
