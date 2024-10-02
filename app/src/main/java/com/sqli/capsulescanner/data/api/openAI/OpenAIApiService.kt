package com.sqli.capsulescanner.data.api.openAI

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface OpenAIApiService {
    /**
     * Define rest services here
     */

    @POST("/v1/chat/completions")
    suspend fun process(
        @Body openAIRequest: OpenAIRequest
    ): Response<OpenAIResponse>

}