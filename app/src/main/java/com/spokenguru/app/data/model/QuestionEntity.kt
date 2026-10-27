package com.spokenguru.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val lessonNumber: Int,
    val questionNumber: Int,
    val hindiText: String,
    val romanText: String,
    val primaryAnswer: String,
    val alternateAnswersJson: String = "[]",
    val distractorsJson: String = "[]",
    val hint: String = "",
    val ruleTitle: String = "",
    val grammarRule: String = "",
    val tagKeyword: String = "",
    val categoryTag: String = "",
    val wordGlossesJson: String = "{}",
    val isCompleted: Boolean = false,
    val isMistake: Boolean = false
)
