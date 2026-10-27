package com.spokenguru.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.spokenguru.app.data.model.LessonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LessonDao {
    @Query("SELECT * FROM lessons ORDER BY lessonNumber ASC")
    fun getAllLessons(): Flow<List<LessonEntity>>

    @Query("SELECT * FROM lessons WHERE lessonNumber = :lessonNumber LIMIT 1")
    suspend fun getLesson(lessonNumber: Int): LessonEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLessons(lessons: List<LessonEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLesson(lesson: LessonEntity)

    @Update
    suspend fun updateLesson(lesson: LessonEntity)

    @Query("UPDATE lessons SET completedQuestions = :completed WHERE lessonNumber = :lessonNumber")
    suspend fun updateCompletedQuestions(lessonNumber: Int, completed: Int)

    @Query("SELECT COUNT(*) FROM lessons")
    suspend fun getLessonCount(): Int
}
