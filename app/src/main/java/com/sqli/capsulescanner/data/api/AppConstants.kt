package com.sqli.capsulescanner.data.api

object AppConstants {
    const val API_BASE_URL = "https://some_url/"
    const val OPEN_AI_API_BASE_URL = "https://api.openai.com/"
    enum class Processors {
        CUSTOM_PROCESSOR, LOCAL_PROCESSOR, OPEN_AI
    }
}