package com.sqli.capsulescanner.data.api

import com.sqli.capsulescanner.entity.DataResponse
import com.sqli.capsulescanner.entity.ImageData
import com.sqli.capsulescanner.entity.ProcessorsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    /**
     * Define rest services here
     */

    @POST("/api/process")
    suspend fun process(
        @Body imageData: ImageData
    ): Response<DataResponse>

    /**
     * Define rest services here
     */
    @POST("/api/availableProcessors")
    suspend fun getProcessorsAvailable(): Response<ProcessorsResponse>
}