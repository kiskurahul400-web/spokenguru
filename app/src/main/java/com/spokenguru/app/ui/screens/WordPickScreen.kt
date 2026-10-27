package com.spokenguru.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
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
import com.spokenguru.app.ui.theme.GuruAmber
import com.spokenguru.app.ui.theme.GuruAmberLight
import com.spokenguru.app.ui.theme.GuruBg
import com.spokenguru.app.ui.theme.GuruBlue
import com.spokenguru.app.ui.theme.GuruBlueLight
import com.spokenguru.app.ui.theme.GuruCardBorder
import com.spokenguru.app.ui.theme.GuruCardBorderDashed
import com.spokenguru.app.ui.theme.GuruDarkBg
import com.spokenguru.app.ui.theme.GuruDarkCardBorder
import com.spokenguru.app.ui.theme.GuruDarkSurface
import com.spokenguru.app.ui.theme.GuruDarkText
import com.spokenguru.app.ui.theme.GuruDarkTextMuted
import com.spokenguru.app.ui.theme.GuruMint
import com.spokenguru.app.ui.theme.GuruMintDark
import com.spokenguru.app.ui.theme.GuruMintLight
import com.spokenguru.app.ui.theme.GuruPurple
import com.spokenguru.app.ui.theme.GuruPurpleBorder
import com.spokenguru.app.ui.theme.GuruPurpleDeep
import com.spokenguru.app.ui.theme.GuruPurpleLight
import com.spokenguru.app.ui.theme.GuruRed
import com.spokenguru.app.ui.theme.GuruRedLight
import com.spokenguru.app.ui.theme.GuruTextDark
import com.spokenguru.app.ui.theme.GuruTextMuted
import com.spokenguru.app.ui.theme.GuruTextSubtle
import kotlinx.coroutines.launch

data class BankWordItem(
    val id: Int,
    val word: String,
    val hindiGloss: String
)

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

    // Toggle options
    var tapSoundEnabled by remember { mutableStateOf(true) }
    var hinglishVisible by remember { mutableStateOf(true) }
    var extraDistractorsEnabled by remember { mutableStateOf(true) }

    val bankWords = remember { mutableStateListOf<BankWordItem>() }
    val selectedWords = remember { mutableStateListOf<BankWordItem>() }

    // Load questions on launch
    LaunchedEffect(lessonNumber) {
        val list = repository.getQuestionsForLessonSync(lessonNumber)
        if (list.isNotEmpty()) {
            questions = list
            val firstUncompleted = list.indexOfFirst { !it.isCompleted }
            currentIndex = if (firstUncompleted != -1) firstUncompleted else 0
        }
    }

    // Set up word bank when question changes
    LaunchedEffect(currentIndex, questions) {
        if (questions.isNotEmpty() && currentIndex < questions.size) {
            val q = questions[currentIndex]
            selectedWords.clear()
            answerState = AnswerState.IDLE

            val correctWords = q.primaryAnswer.split("\\s+".toRegex()).map { cleanWordPunctuation(it) }.filter { it.isNotBlank() }

            val distractorType = object : TypeToken<List<String>>() {}.type
            val distractors: List<String> = try {
                gson.fromJson(q.distractorsJson, distractorType) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }

            val glossMapType = object : TypeToken<Map<String, String>>() {}.type
            val wordGlosses: Map<String, String> = try {
                gson.fromJson(q.wordGlossesJson, glossMapType) ?: emptyMap()
            } catch (e: Exception) {
                emptyMap()
            }

            val rawBank = (correctWords + if (extraDistractorsEnabled) distractors else emptyList()).shuffled()
            bankWords.clear()
            rawBank.forEachIndexed { idx, word ->
                val clean = cleanWordPunctuation(word)
                val gloss = wordGlosses[clean] ?: wordGlosses[clean.lowercase()] ?: ""
                bankWords.add(BankWordItem(id = idx, word = word, hindiGloss = gloss))
            }
        }
    }

    if (questions.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(if (isDark) GuruDarkBg else GuruBg),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Loading Lesson Questions...", fontSize = 16.sp, color = GuruTextSubtle)
        }
        return
    }

    val currentQuestion = questions[currentIndex]

    Scaffold(
        containerColor = if (isDark) GuruDarkBg else GuruBg
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
                    .padding(bottom = 120.dp)
            ) {
                // Top App Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .shadow(1.dp, CircleShape)
                            .clip(CircleShape)
                            .background(if (isDark) GuruDarkSurface else Color.White)
                            .size(40.dp)
                            .clickable { showExitDialog = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = if (isDark) GuruDarkText else GuruTextDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "EBOOK 1 BILINGUAL",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = GuruPurple,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "English Practice Guru",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = if (isDark) GuruDarkText else GuruTextDark
                        )
                    }

                    Box(
                        modifier = Modifier
                            .shadow(1.dp, CircleShape)
                            .clip(CircleShape)
                            .background(if (isDark) GuruDarkSurface else Color.White)
                            .size(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (isDark) GuruDarkText else GuruTextDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Sub-header Badges row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // #14 of 101
                    PillBadge(
                        text = "#${currentIndex + 1} of ${questions.size}",
                        bg = if (isDark) Color(0xFF262347) else GuruPurpleLight,
                        textColor = GuruPurple,
                        showDot = true
                    )
                    // Tag Keyword
                    if (currentQuestion.tagKeyword.isNotBlank()) {
                        PillBadge(
                            text = "💡 ${currentQuestion.tagKeyword}",
                            bg = if (isDark) Color(0xFF322818) else GuruAmberLight,
                            textColor = GuruAmber
                        )
                    }
                    // Category
                    PillBadge(
                        text = "Demonstratives (A2)",
                        bg = if (isDark) Color(0xFF1E1D38) else Color(0xFFF3EFFF),
                        textColor = GuruPurple
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // TARGET SENTENCE CARD (TRANSLATE FROM HINDI)
                val cardShape = RoundedCornerShape(24.dp)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .shadow(2.dp, cardShape, ambientColor = GuruPurple.copy(alpha = 0.08f))
                        .clip(cardShape)
                        .background(if (isDark) GuruDarkSurface else Color.White)
                        .border(1.dp, if (isDark) GuruDarkCardBorder else GuruCardBorder, cardShape)
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "TRANSLATE FROM HINDI",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GuruPurple,
                                letterSpacing = 1.sp
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isDark) Color(0xFF242244) else Color(0xFFF6F4FF))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = currentQuestion.categoryTag.ifBlank { "Plural Distance" },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = GuruPurple
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = currentQuestion.hindiText,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) GuruDarkText else GuruTextDark,
                            lineHeight = 30.sp
                        )

                        if (hinglishVisible && currentQuestion.romanText.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "(${currentQuestion.romanText})",
                                fontSize = 14.sp,
                                color = if (isDark) GuruDarkTextMuted else GuruTextMuted
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Audio Action Buttons Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Listen Pill
                            ActionPillButton(
                                text = "Listen",
                                icon = Icons.AutoMirrored.Filled.VolumeUp,
                                onClick = { ttsManager.speak(currentQuestion.primaryAnswer, isSlow = false) },
                                modifier = Modifier.weight(1f)
                            )

                            // Slow Turtle Pill
                            ActionPillButton(
                                text = "Slow",
                                icon = Icons.Default.Pets,
                                onClick = { ttsManager.speak(currentQuestion.primaryAnswer, isSlow = true) },
                                modifier = Modifier.weight(1f)
                            )

                            // Speak / Pronounce Pill
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(GuruPurpleDeep)
                                    .clickable { ttsManager.speak(currentQuestion.primaryAnswer) }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Mic,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Speak",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // YOUR ENGLISH SENTENCE (ANSWER SLOT CARD)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .shadow(2.dp, cardShape, ambientColor = GuruPurple.copy(alpha = 0.08f))
                        .clip(cardShape)
                        .background(if (isDark) GuruDarkSurface else Color.White)
                        .border(1.dp, if (isDark) GuruDarkCardBorder else GuruCardBorder, cardShape)
                        .padding(18.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "YOUR ENGLISH SENTENCE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GuruPurple,
                                letterSpacing = 0.8.sp
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Hint pill
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isDark) Color(0xFF2B2213) else GuruAmberLight)
                                        .clickable { showHintDialog = true }
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = "💡", fontSize = 11.sp)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Hint",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GuruAmber
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(6.dp))

                                // Clear button
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isDark) Color(0xFF252345) else GuruPurpleLight)
                                        .clickable { selectedWords.clear() }
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "Clear",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GuruPurple
                                    )
                                }

                                Spacer(modifier = Modifier.width(6.dp))

                                // Undo button
                                Box(
                                    modifier = Modifier
                                        .size(26.dp)
                                        .clip(CircleShape)
                                        .background(if (isDark) Color(0xFF252345) else GuruPurpleLight)
                                        .clickable {
                                            if (selectedWords.isNotEmpty()) {
                                                selectedWords.removeAt(selectedWords.size - 1)
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Undo,
                                        contentDescription = "Undo",
                                        tint = GuruPurple,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Dashed slot container
                        val slotShape = RoundedCornerShape(18.dp)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = 110.dp)
                                .clip(slotShape)
                                .background(if (isDark) Color(0xFF131222) else Color(0xFFFAF9FF))
                                .border(
                                    width = 1.5.dp,
                                    color = if (isDark) Color(0xFF332F5C) else GuruCardBorderDashed,
                                    shape = slotShape
                                )
                                .padding(12.dp),
                            contentAlignment = Alignment.TopStart
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
                                                if (tapSoundEnabled) SoundEffectManager.playClick()
                                                selectedWords.remove(item)
                                            }
                                        }
                                    )
                                }

                                // "+ Tap next word" badge
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .border(1.dp, if (isDark) Color(0xFF332F5C) else Color(0xFFDDD8FD), RoundedCornerShape(20.dp))
                                        .background(Color.Transparent)
                                        .padding(horizontal = 14.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = "+ Tap next word",
                                        fontSize = 13.sp,
                                        color = GuruPurple,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tap words in correct order or tap microphone to speak",
                            fontSize = 11.sp,
                            color = GuruTextSubtle,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // WORD BANK SECTION
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "WORD BANK",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = GuruPurple,
                            letterSpacing = 1.sp
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isDark) Color(0xFF262347) else GuruPurpleLight)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${bankWords.size} tokens",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GuruPurple
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Word Bank Grid
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        bankWords.forEach { item ->
                            val isSelected = selectedWords.any { it.id == item.id }
                            BankWordTile(
                                word = item.word,
                                hindiGloss = item.hindiGloss,
                                isSelected = isSelected,
                                onClick = {
                                    if (answerState == AnswerState.IDLE && !isSelected) {
                                        if (tapSoundEnabled) SoundEffectManager.playClick()
                                        selectedWords.add(item)
                                    }
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Action Buttons Row: SHOW ANSWER + CHECK ANSWER
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        GuruButton(
                            text = "SHOW ANSWER",
                            variant = GuruButtonVariant.SECONDARY,
                            onClick = {
                                // reveal hint / answers
                                showHintDialog = true
                            },
                            modifier = Modifier.weight(1f),
                            height = 50.dp
                        )

                        GuruButton(
                            text = "Check Answer",
                            icon = Icons.Default.Check,
                            variant = GuruButtonVariant.PRIMARY,
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
                                    if (tapSoundEnabled) SoundEffectManager.playSuccess()
                                    ttsManager.speak(currentQuestion.primaryAnswer)
                                    scope.launch {
                                        repository.markQuestionCompleted(currentQuestion.id, currentQuestion.lessonNumber)
                                        repository.markMistake(currentQuestion.id, false)
                                    }
                                } else {
                                    answerState = AnswerState.INCORRECT
                                    if (tapSoundEnabled) SoundEffectManager.playError()
                                    scope.launch {
                                        repository.markMistake(currentQuestion.id, true)
                                    }
                                }
                            },
                            modifier = Modifier.weight(1.3f),
                            height = 50.dp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Auxiliary Controls Row (Tap Sound, Hinglish, +Extra, Type)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SmallTogglePill(
                            label = "Tap Sound",
                            active = tapSoundEnabled,
                            icon = "🔊",
                            onClick = { tapSoundEnabled = !tapSoundEnabled }
                        )
                        SmallTogglePill(
                            label = "Hinglish",
                            active = hinglishVisible,
                            icon = "अ/A",
                            onClick = { hinglishVisible = !hinglishVisible }
                        )
                        SmallTogglePill(
                            label = "+Extra",
                            active = extraDistractorsEnabled,
                            icon = "🔥",
                            onClick = { extraDistractorsEnabled = !extraDistractorsEnabled }
                        )
                        SmallTogglePill(
                            label = "Type",
                            active = false,
                            icon = "⌨",
                            onClick = {}
                        )
                    }
                }
            }

            // FEEDBACK BOTTOM OVERLAYS (CORRECT / WRONG)
            AnimatedVisibility(
                visible = answerState != AnswerState.IDLE,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                if (answerState == AnswerState.CORRECT) {
                    CorrectFeedbackCard(
                        currentQuestion = currentQuestion,
                        currentIndex = currentIndex,
                        totalQuestions = questions.size,
                        ttsManager = ttsManager,
                        onNext = {
                            if (currentIndex < questions.size - 1) {
                                currentIndex++
                            } else {
                                isLessonCompleted = true
                            }
                        }
                    )
                } else if (answerState == AnswerState.INCORRECT) {
                    IncorrectFeedbackCard(
                        currentQuestion = currentQuestion,
                        selectedWords = selectedWords.map { it.word },
                        ttsManager = ttsManager,
                        onTryAgain = {
                            answerState = AnswerState.IDLE
                        },
                        onNext = {
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

    // Hint Dialog
    if (showHintDialog) {
        AlertDialog(
            onDismissRequest = { showHintDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = GuruAmber
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Guru Grammar Hint", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Text(
                        text = currentQuestion.hint.ifBlank { "Notice singular vs plural and near vs far." },
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Rule: ${currentQuestion.grammarRule.ifBlank { "Use This/That for singular and These/Those for plural." }}",
                        fontSize = 13.sp,
                        color = GuruPurple
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showHintDialog = false }) {
                    Text(text = "GOT IT", fontWeight = FontWeight.Bold, color = GuruPurple)
                }
            }
        )
    }

    // Exit Dialog
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text(text = "Exit Lesson?", fontWeight = FontWeight.Bold) },
            text = { Text(text = "Aapka progress save ho chuka hai. Baad me aap wahi se resume kar sakte hain.") },
            confirmButton = {
                TextButton(onClick = { showExitDialog = false; onNavigateBack() }) {
                    Text(text = "EXIT", color = GuruRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text(text = "KEEP PRACTICING", color = GuruPurple, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // Completion Dialog
    if (isLessonCompleted) {
        AlertDialog(
            onDismissRequest = { onNavigateBack() },
            title = { Text(text = "🎉 Lesson 1 Completed!", fontWeight = FontWeight.Bold) },
            text = { Text(text = "Shabaash! Aapne Lesson 1 ke sabhi 101 questions successfully complete kar liye hain!") },
            confirmButton = {
                GuruButton(
                    text = "BACK TO HOME",
                    onClick = onNavigateBack,
                    height = 46.dp
                )
            }
        )
    }
}

/**
 * Correct Feedback Card matching unnamed (2).png
 */
@Composable
fun CorrectFeedbackCard(
    currentQuestion: QuestionEntity,
    currentIndex: Int,
    totalQuestions: Int,
    ttsManager: TtsManager,
    onNext: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(16.dp, shape)
            .clip(shape)
            .background(if (isDark) GuruDarkSurface else Color.White)
            .border(1.dp, if (isDark) GuruDarkCardBorder else GuruCardBorder, shape)
            .padding(20.dp)
    ) {
        Column {
            // Header: Answer Validated + Level 1 • Card 14
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(GuruMintLight)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = GuruMintDark,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Answer Validated",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GuruMintDark
                        )
                    }
                }

                Text(
                    text = "Level 1 • Card ${currentIndex + 1}",
                    fontSize = 12.sp,
                    color = GuruTextSubtle
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Celebration heading
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(GuruMintLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Celebration,
                        contentDescription = null,
                        tint = GuruMintDark,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "शानदार! बिल्कुल सही",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = GuruMintDark
                    )
                    Text(
                        text = "Excellent! Perfectly Correct Response",
                        fontSize = 12.sp,
                        color = GuruTextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ENGLISH TRANSLATION (Mint Card)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isDark) Color(0xFF142B20) else GuruMintLight)
                    .border(1.dp, GuruMint.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = GuruMint,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "\"${currentQuestion.primaryAnswer}\"",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = if (isDark) GuruDarkText else GuruTextDark
                        )
                        Text(
                            text = "Correct English translation verified",
                            fontSize = 11.sp,
                            color = GuruMintDark
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Grammar Rule Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isDark) Color(0xFF1F1D38) else Color(0xFFF6F4FF))
                    .border(1.dp, if (isDark) Color(0xFF332F5C) else GuruPurpleBorder, RoundedCornerShape(16.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "📖", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "GURU GRAMMAR RULE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GuruPurple,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currentQuestion.grammarRule.ifBlank {
                            "Demonstrative pronouns are used to point out objects. Use This/These for near and That/Those for far."
                        },
                        fontSize = 12.sp,
                        color = if (isDark) GuruDarkText else GuruTextDark,
                        lineHeight = 17.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Next Question Pill Button
            GuruButton(
                text = "Next Question ${currentIndex + 2} / $totalQuestions",
                subText = "AGLA SAWAL",
                icon = Icons.AutoMirrored.Filled.ArrowForward,
                onClick = onNext,
                modifier = Modifier.fillMaxWidth(),
                height = 52.dp
            )
        }
    }
}

/**
 * Incorrect Feedback Card matching unnamed (3).png
 */
@Composable
fun IncorrectFeedbackCard(
    currentQuestion: QuestionEntity,
    selectedWords: List<String>,
    ttsManager: TtsManager,
    onTryAgain: () -> Unit,
    onNext: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(16.dp, shape)
            .clip(shape)
            .background(if (isDark) GuruDarkSurface else Color.White)
            .border(1.dp, if (isDark) GuruDarkCardBorder else GuruCardBorder, shape)
            .padding(20.dp)
    ) {
        Column {
            // Header banner: Needs Correction
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isDark) Color(0xFF321A1D) else GuruRedLight)
                    .border(1.dp, GuruRed.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = null,
                        tint = GuruRed,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Needs Correction / उत्तर जाँचना आवश्यक",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = GuruRed
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Correct Answer Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isDark) Color(0xFF142B20) else GuruMintLight)
                    .border(1.dp, GuruMint.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        text = "सही उत्तर (CORRECT ANSWER)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = GuruMintDark
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "\"${currentQuestion.primaryAnswer}\"",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = if (isDark) GuruDarkText else GuruTextDark
                        )
                        IconButton(
                            onClick = { ttsManager.speak(currentQuestion.primaryAnswer) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "Listen",
                                tint = GuruMintDark,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Grammar comparison box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isDark) Color(0xFF1F1D38) else Color(0xFFF6F4FF))
                    .border(1.dp, if (isDark) Color(0xFF332F5C) else GuruPurpleBorder, RoundedCornerShape(16.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "📖", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "GURU GRAMMAR RULE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GuruPurple,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currentQuestion.grammarRule.ifBlank {
                            "Singular = This/That • Plural = These/Those"
                        },
                        fontSize = 12.sp,
                        color = if (isDark) GuruDarkText else GuruTextDark,
                        lineHeight = 17.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons: Try Again & Continue
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                GuruButton(
                    text = "दोबारा प्रयास करें (Try Again)",
                    icon = Icons.Default.Refresh,
                    variant = GuruButtonVariant.PRIMARY,
                    onClick = onTryAgain,
                    modifier = Modifier.fillMaxWidth(),
                    height = 50.dp
                )

                GuruButton(
                    text = "समझ गया, अगला प्रश्न →",
                    variant = GuruButtonVariant.SECONDARY,
                    onClick = onNext,
                    modifier = Modifier.fillMaxWidth(),
                    height = 50.dp
                )
            }
        }
    }
}

@Composable
fun PillBadge(
    text: String,
    bg: Color,
    textColor: Color,
    showDot: Boolean = false
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(bg)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (showDot) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(textColor)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = text,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}

@Composable
fun ActionPillButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isDark) Color(0xFF242244) else Color(0xFFF6F4FF))
            .border(1.dp, if (isDark) Color(0xFF353260) else Color(0xFFDDD8FD), RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = GuruPurple,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                color = GuruPurple,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
fun SmallTogglePill(
    label: String,
    active: Boolean,
    icon: String,
    onClick: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val shape = RoundedCornerShape(14.dp)
    Box(
        modifier = Modifier
            .clip(shape)
            .background(
                if (active) (if (isDark) Color(0xFF272449) else GuruPurpleLight)
                else (if (isDark) Color(0xFF1B1A2C) else Color(0xFFF3F3F7))
            )
            .border(
                1.dp,
                if (active) GuruPurpleBorder else Color.Transparent,
                shape
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = icon, fontSize = 11.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                color = if (active) GuruPurple else GuruTextSubtle
            )
        }
    }
}
