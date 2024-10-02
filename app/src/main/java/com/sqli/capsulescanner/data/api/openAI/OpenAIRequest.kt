package com.sqli.capsulescanner.data.api.openAI

data class OpenAIRequest(
    val max_tokens: Int,
    val messages: List<Message>,
    val model: String
)

data class Message(
    val content: List<Content>,
    val role: String
)

data class Content(
    val image_url: ImageUrl?,
    val text: String?,
    val type: String
)

data class ImageUrl(
    val url: String
)