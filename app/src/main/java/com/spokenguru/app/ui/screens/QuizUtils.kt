package com.spokenguru.app.ui.screens

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

enum class AnswerState {
    IDLE,
    CORRECT,
    INCORRECT
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
