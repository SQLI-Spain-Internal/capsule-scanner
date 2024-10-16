package com.sqli.capsulescanner.data.api

object AppConstants {
    const val API_BASE_URL = "https://some_url/"
    const val GOOGLE_VISION_API_BASE_URL = "https://vision.googleapis.com/"
    const val OPEN_AI_API_BASE_URL = "https://api.openai.com/"
    enum class Processors {
        CUSTOM_PROCESSOR, LOCAL_PROCESSOR, OPEN_AI, GOOGLE_VISION
    }

    const val VISION_API_PROJECT_ID = "capsule-scanner"
    const val VISION_API_LOCATION_ID = "europe-west1"
    const val VISION_API_PRODUCT_SET_ID = "nespresso-originals"
}