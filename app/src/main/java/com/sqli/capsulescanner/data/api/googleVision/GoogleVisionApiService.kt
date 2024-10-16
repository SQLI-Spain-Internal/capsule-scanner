package com.sqli.capsulescanner.data.api.googleVision

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface GoogleVisionApiService {
    @POST("/v1/images:annotate")
    suspend fun process(
        @Body googleVisionRequest: GoogleVisionRequest,
        @Query("key") visionApiKey: String
    ): Response<ResponseBody>

}