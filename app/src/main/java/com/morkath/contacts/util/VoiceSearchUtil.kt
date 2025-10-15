package com.morkath.contacts.util

import android.content.Intent
import android.speech.RecognizerIntent

object VoiceSearchUtil {
    /**
     * Tạo một Intent để khởi động nhận diện giọng nói.
     *
     * @param prompt Thông điệp hiển thị cho người dùng khi bắt đầu nhận diện giọng nói.
     * @return Trả về Intent đã cấu hình.
     */
    fun createVoiceSearchIntent(prompt: String = "Speak now"): Intent {
        return Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PROMPT, prompt)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 2000)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 1500)
        }
    }

    /**
     * Trích xuất kết quả nhận diện giọng nói từ Intent trả về.
     *
     * @param data Intent chứa kết quả nhận diện giọng nói.
     * @return Trả về chuỗi kết quả đầu tiên hoặc null nếu không có kết quả.
     */
    fun extractResult(data: Intent?): String? {
        val matches = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
        return matches?.firstOrNull()
    }
}