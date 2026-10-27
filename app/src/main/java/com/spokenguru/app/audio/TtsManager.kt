package com.spokenguru.app.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

class TtsManager(context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    private var isReady: Boolean = false

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US)
            isReady = result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED
            tts?.setPitch(1.0f)
        }
    }

    fun speak(text: String, isSlow: Boolean = false) {
        if (isReady && tts != null) {
            tts?.setSpeechRate(if (isSlow) 0.6f else 1.0f)
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "SpokenGuruTTS")
        }
    }

    fun stop() {
        tts?.stop()
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isReady = false
    }
}
