package com.spokenguru.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lessons")
data class LessonEntity(
    @PrimaryKey
    val lessonNumber: Int,
    val title: String,
    val pageNumber: Int,
    val totalQuestions: Int = 0,
    val completedQuestions: Int = 0,
    val isAvailable: Boolean = false
)
