package com.sqli.capsulescanner.data

import com.sqli.capsulescanner.entity.DataResponse
import com.sqli.capsulescanner.entity.ImageData
import com.sqli.capsulescanner.entity.ProcessorsResponse
import retrofit2.Response

interface MainDataSource {
    suspend fun process(imageData: ImageData): Response<DataResponse>
    suspend fun getProcessorsAvailable(): Response<ProcessorsResponse>
}