package com.spokenguru.app.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

object SoundEffectManager {
    private val scope = CoroutineScope(Dispatchers.Default)
    private const val SAMPLE_RATE = 44100

    private val successPcm: ByteArray by lazy { generateSuccessChime() }
    private val errorPcm: ByteArray by lazy { generateErrorBoop() }
    private val clickPcm: ByteArray by lazy { generateClickSound() }

    fun playSuccess() {
        scope.launch {
            playPcm(successPcm)
        }
    }

    fun playError() {
        scope.launch {
            playPcm(errorPcm)
        }
    }

    fun playClick() {
        scope.launch {
            playPcm(clickPcm)
        }
    }

    private fun playPcm(pcm: ByteArray) {
        try {
            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(SAMPLE_RATE)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(pcm.size)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(pcm, 0, pcm.size)
            audioTrack.play()

            // Release after playback finishes
            val durationMs = (pcm.size.toDouble() / (SAMPLE_RATE * 2) * 1000).toLong() + 50
            Thread.sleep(durationMs)
            audioTrack.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Joyful Duolingo-style rising major arpeggio / chime (G5 -> C6)
     */
    private fun generateSuccessChime(): ByteArray {
        val durationSeconds = 0.4
        val totalSamples = (SAMPLE_RATE * durationSeconds).toInt()
        val buffer = ByteArray(totalSamples * 2)

        for (i in 0 until totalSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            // Frequency transition: 784 Hz (G5) for first half, 1046 Hz (C6) for second half
            val freq = if (t < 0.12) 784.0 else 1046.0
            val decay = exp(-t * 7.0) // smooth exponential decay
            val sampleValue = (sin(2 * PI * freq * t) * decay * 0.7 * Short.MAX_VALUE).toInt()
            val clamped = sampleValue.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()

            buffer[i * 2] = (clamped.toInt() and 0xFF).toByte()
            buffer[i * 2 + 1] = ((clamped.toInt() shr 8) and 0xFF).toByte()
        }
        return buffer
    }

    /**
     * Low downward soft error boop (260 Hz -> 170 Hz)
     */
    private fun generateErrorBoop(): ByteArray {
        val durationSeconds = 0.35
        val totalSamples = (SAMPLE_RATE * durationSeconds).toInt()
        val buffer = ByteArray(totalSamples * 2)

        for (i in 0 until totalSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val freq = 260.0 - (t / durationSeconds) * 90.0
            val decay = exp(-t * 6.0)
            val sampleValue = (sin(2 * PI * freq * t) * decay * 0.6 * Short.MAX_VALUE).toInt()
            val clamped = sampleValue.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()

            buffer[i * 2] = (clamped.toInt() and 0xFF).toByte()
            buffer[i * 2 + 1] = ((clamped.toInt() shr 8) and 0xFF).toByte()
        }
        return buffer
    }

    /**
     * Subtle 15ms pop/click for tile tap
     */
    private fun generateClickSound(): ByteArray {
        val durationSeconds = 0.02
        val totalSamples = (SAMPLE_RATE * durationSeconds).toInt()
        val buffer = ByteArray(totalSamples * 2)

        for (i in 0 until totalSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val freq = 1200.0
            val decay = exp(-t * 150.0)
            val sampleValue = (sin(2 * PI * freq * t) * decay * 0.4 * Short.MAX_VALUE).toInt()
            val clamped = sampleValue.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()

            buffer[i * 2] = (clamped.toInt() and 0xFF).toByte()
            buffer[i * 2 + 1] = ((clamped.toInt() shr 8) and 0xFF).toByte()
        }
        return buffer
    }
}
