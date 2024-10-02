package com.sqli.capsulescanner.repository

import com.sqli.capsulescanner.data.MainDataSource
import com.sqli.capsulescanner.entity.DataResponse
import com.sqli.capsulescanner.entity.ImageData
import com.sqli.capsulescanner.entity.ProcessorsResponse
import com.sqli.capsulescanner.utilities.ResourceState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val mainDataSource: MainDataSource
) {

    suspend fun process(imageData: ImageData): Flow<ResourceState<DataResponse>> {
        return flow {
            emit(ResourceState.Loading)
            val response = mainDataSource.process(imageData)
            if (response.isSuccessful && response.body() != null) {
                /**
                 * Process response here
                 */
                emit(ResourceState.Success(response.body()!!))
            } else {
                emit(ResourceState.Error("Error processing data"))
            }
        }.catch { e ->
            emit(ResourceState.Error(e.localizedMessage ?: "Error processing data"))
        }
    }

    suspend fun getProcessorsAvailable(): Flow<ResourceState<ProcessorsResponse>> {
        return flow {
            emit(ResourceState.Loading)
            val response = mainDataSource.getProcessorsAvailable()
            if (response.isSuccessful && response.body() != null) {
                /**
                 * Process response here
                 */
                emit(ResourceState.Success(response.body()!!))
            } else {
                emit(ResourceState.Error("Error processing data"))
            }
        }.catch { e ->
            emit(ResourceState.Error(e.localizedMessage ?: "Error processing data"))
        }
    }
}