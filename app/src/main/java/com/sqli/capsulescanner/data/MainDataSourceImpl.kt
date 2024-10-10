package com.sqli.capsulescanner.data

import com.sqli.capsulescanner.MainApplication
import com.sqli.capsulescanner.data.api.ApiService
import com.sqli.capsulescanner.data.api.AppConstants
import com.sqli.capsulescanner.data.api.openAI.Content
import com.sqli.capsulescanner.data.api.openAI.ImageUrl
import com.sqli.capsulescanner.data.api.openAI.Message
import com.sqli.capsulescanner.data.api.openAI.OpenAIApiService
import com.sqli.capsulescanner.data.api.openAI.OpenAIRequest
import com.sqli.capsulescanner.entity.DataResponse
import com.sqli.capsulescanner.entity.ImageData
import com.sqli.capsulescanner.entity.ProcessorsResponse
import com.sqli.capsulescanner.utilities.uriToBase64
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import javax.inject.Inject

class MainDataSourceImpl @Inject constructor(
    private val apiService: ApiService,
    private val openAIApiService: OpenAIApiService
) : MainDataSource {
    override suspend fun process(imageData: ImageData): Response<DataResponse> {
        when (imageData.processor) {
            AppConstants.Processors.CUSTOM_PROCESSOR -> {
                return apiService.process(imageData)
            }

            AppConstants.Processors.LOCAL_PROCESSOR -> {
                /**
                 * Change to local processor, not implemented yet
                 */
                return apiService.process(imageData)
            }

            AppConstants.Processors.OPEN_AI -> {
                val openAIRequest = OpenAIRequest(

                    max_tokens = 1000,
                    messages = listOf(
                        Message(
                            content = listOf(
                                Content(
                                    image_url = ImageUrl(
                                        url = "data:image/jpeg;base64,${uriToBase64(
                                            MainApplication.instance.applicationContext,
                                            imageData.imageURI
                                        )}"
                                    ),
                                    text = null,
                                    type = "image_url"
                                ),
                                Content(
                                    image_url = null,
                                    text = "Can you identify this as a nespresso capsule?, provide if there is one the response in a JSON format including properties like capsule_name, flavor, intensity and others, please just respond with a one level JSON, if there is not identifiable capsule response should contain an error in the same one level JSON ?",
                                    type = "text"
                                )
                            ),
                            role = "user"
                        )
                    ),
                    model = "chatgpt-4o-latest"
                )
                try {
                    val response = openAIApiService.process(openAIRequest)
                    return if (response.isSuccessful) {
                        val responseContent = response.body()!!
                        val dataResponse = DataResponse(
                            response = responseContent.choices[0].message.content.replace("```","").replace("json","").replace("\n",""),
                            localUri = imageData.imageURI,
                            content = responseContent.choices[0].message.content.replace("```","").replace("json","").replace("\n",""),
                        )
                        Response.success(dataResponse)
                    } else {
                        val errorBody = response.errorBody()?.string() ?: "Unknown error"
                        Response.error(
                            response.code(),
                            response.errorBody() ?: errorBody.toResponseBody()
                        )
                    }
                } catch (e: Exception) {
                    val nada = 1
                    return Response.error(404, "Unknown error".toResponseBody())
                }
            }
        }
    }

    override suspend fun getProcessorsAvailable(): Response<ProcessorsResponse> {
        return apiService.getProcessorsAvailable()
    }
}