package com.sqli.capsulescanner.data.api.openAI

data class OpenAIResponse(
    val choices: List<Choice>,
    val model: String?,
    val usage: Usage
)

data class Choice(
    val finish_reason: String,
    val index: Int,
    val message: MessageInfo
)

data class Usage(
    val completion_tokens: Int,
    val completion_tokens_details: CompletionTokensDetails,
    val prompt_tokens: Int,
    val total_tokens: Int
)

data class MessageInfo(
    val content: String,
    val role: String
)

data class CompletionTokensDetails(
    val reasoning_tokens: Int
)