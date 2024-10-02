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
                                    text = "Identify any Nespresso capsules present in the given context. If any are found, provide their details in JSON format.\n" +
                                            " \n" +
                                            "- Capsule Name\n" +
                                            "\n" +
                                            "- Flavor Profile\n" +
                                            "\n" +
                                            "- Intensity Level\n" +
                                            " \n" +
                                            "If no Nespresso capsules are identified, or if the image is not clear enough or of insufficient quality to make an identification, please return only a JSON object indicating the issue.\n" +
                                            "\n" +
                                            " ",
                                    type = "text"
                                )
                            ),
                            role = "user"
                        )
                    ),
                    model = "gpt-4o"
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