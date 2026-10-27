package com.spokenguru.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.spokenguru.app.data.model.QuestionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {
    @Query("SELECT * FROM questions WHERE lessonNumber = :lessonNumber ORDER BY questionNumber ASC")
    fun getQuestionsForLesson(lessonNumber: Int): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE lessonNumber = :lessonNumber ORDER BY questionNumber ASC")
    suspend fun getQuestionsForLessonSync(lessonNumber: Int): List<QuestionEntity>

    @Query("SELECT * FROM questions WHERE isMistake = 1 ORDER BY lessonNumber ASC, questionNumber ASC")
    fun getAllMistakes(): Flow<List<QuestionEntity>>

    @Query("SELECT COUNT(*) FROM questions WHERE lessonNumber = :lessonNumber AND isCompleted = 1")
    suspend fun getCompletedCount(lessonNumber: Int): Int

    @Query("SELECT COUNT(*) FROM questions WHERE isMistake = 1")
    fun getMistakesCount(): Flow<Int>

    @Query("UPDATE questions SET isCompleted = :completed WHERE id = :id")
    suspend fun updateCompletedStatus(id: Long, completed: Boolean)

    @Query("UPDATE questions SET isMistake = :isMistake WHERE id = :id")
    suspend fun updateMistakeStatus(id: Long, isMistake: Boolean)

    @Query("UPDATE questions SET isCompleted = 0 WHERE lessonNumber = :lessonNumber")
    suspend fun resetLessonProgress(lessonNumber: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuestionEntity>)

    @Query("DELETE FROM questions WHERE lessonNumber = :lessonNumber")
    suspend fun deleteQuestionsForLesson(lessonNumber: Int)

    @Query("SELECT COUNT(*) FROM questions")
    suspend fun getTotalQuestionsCount(): Int
}
