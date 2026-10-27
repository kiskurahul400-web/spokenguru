package com.spokenguru.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportLessonScreen(
    repository: LessonRepository,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isDark = isSystemInDarkTheme()

    var dbStats by remember { mutableStateOf("Loading stats...") }
    var lessonNumberText by remember { mutableStateOf("2") }
    var jsonInput by remember { mutableStateOf("") }
    var isImporting by remember { mutableStateOf(false) }

    fun refreshStats() {
        scope.launch {
            dbStats = repository.getDbStats()
        }
    }

    LaunchedEffect(Unit) {
        refreshStats()
    }

    val sampleLesson2Json = """[
  {
    "questionNumber": 1,
    "hindiText": "क्या समय हुआ है?",
    "romanText": "Kya samay hua hai?",
    "primaryAnswer": "What is the time?",
    "alternateAnswers": ["What's the time?"],
    "distractors": ["where", "how", "this", "that"],
    "hint": "Use 'What is the time?' or 'What's the time?'"
  },
  {
    "questionNumber": 2,
    "hindiText": "मेरी घड़ी में 5 बजे हैं।",
    "romanText": "Meri ghadi mein 5 baje hain.",
    "primaryAnswer": "It is 5 o'clock by my watch.",
    "alternateAnswers": ["It's 5 o'clock by my watch."],
    "distractors": ["in", "on", "are", "these"],
    "hint": "Use 'by my watch' for time in a watch"
  }
]"""

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Manage & Import Lessons", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDark) DuoDarkSurface else Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Stats Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDark) DuoDarkSurface else DuoGray
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isDark) DuoDarkCardBorder else DuoGrayBorder
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "DATABASE STATUS",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            letterSpacing = 1.sp,
                            color = DuoGrayText
                        )
                        IconButton(onClick = { refreshStats() }) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh", tint = DuoBlue)
                        }
                    }
                    Text(
                        text = dbStats,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isDark) DuoDarkText else DuoTextDark
                    )
                }
            }

            // Quick reset button for Lesson 1
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDark) DuoDarkSurface else Color.White
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isDark) DuoDarkCardBorder else DuoGrayBorder
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Reset Lesson 1 Progress",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = if (isDark) DuoDarkText else DuoTextDark
                        )
                        Text(
                            text = "Clear completed questions count to restart",
                            fontSize = 12.sp,
                            color = DuoGrayText
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    DuoButton(
                        text = "RESET",
                        color = DuoButtonColor.GRAY,
                        height = 38.dp,
                        modifier = Modifier.width(90.dp),
                        onClick = {
                            scope.launch {
                                repository.resetLesson(1)
                                refreshStats()
                                Toast.makeText(context, "Lesson 1 progress reset", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }

            // Import Form Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDark) DuoDarkSurface else Color.White
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isDark) DuoDarkCardBorder else DuoGrayBorder
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "ADD / UPDATE LESSON CONTENT",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        letterSpacing = 1.sp,
                        color = DuoBlue
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Target Lesson Number (1 – 40):",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isDark) DuoDarkText else DuoTextDark
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = lessonNumberText,
                        onValueChange = { lessonNumberText = it.filter { ch -> ch.isDigit() } },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Questions JSON:",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isDark) DuoDarkText else DuoTextDark
                        )
                        TextButton(onClick = { jsonInput = sampleLesson2Json }) {
                            Text(text = "Paste Sample JSON", color = DuoBlue, fontWeight = FontWeight.Bold)
                        }
                    }

                    OutlinedTextField(
                        value = jsonInput,
                        onValueChange = { jsonInput = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        shape = RoundedCornerShape(12.dp),
                        placeholder = { Text(text = "Paste JSON array here...") },
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    DuoButton(
                        text = if (isImporting) "IMPORTING..." else "SAVE TO DATABASE",
                        enabled = !isImporting && jsonInput.isNotBlank() && lessonNumberText.isNotBlank(),
                        color = DuoButtonColor.GREEN,
                        onClick = {
                            val targetLesson = lessonNumberText.toIntOrNull() ?: 1
                            isImporting = true
                            scope.launch {
                                val result = repository.importQuestionsJson(targetLesson, jsonInput)
                                isImporting = false
                                if (result.isSuccess) {
                                    val count = result.getOrNull() ?: 0
                                    Toast.makeText(
                                        context,
                                        "Successfully imported $count questions into Lesson $targetLesson!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    refreshStats()
                                    jsonInput = ""
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Import error: ${result.exceptionOrNull()?.localizedMessage}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}
