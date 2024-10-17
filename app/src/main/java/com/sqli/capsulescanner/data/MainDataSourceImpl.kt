package com.sqli.capsulescanner.data

import com.sqli.capsulescanner.BuildConfig
import com.sqli.capsulescanner.MainApplication
import com.sqli.capsulescanner.data.api.ApiService
import com.sqli.capsulescanner.data.api.AppConstants
import com.sqli.capsulescanner.data.api.AppConstants.VISION_API_LOCATION_ID
import com.sqli.capsulescanner.data.api.AppConstants.VISION_API_PRODUCT_SET_ID
import com.sqli.capsulescanner.data.api.AppConstants.VISION_API_PROJECT_ID
import com.sqli.capsulescanner.data.api.googleVision.Feature
import com.sqli.capsulescanner.data.api.googleVision.GoogleVisionApiService
import com.sqli.capsulescanner.data.api.googleVision.GoogleVisionRequest
import com.sqli.capsulescanner.data.api.googleVision.Image
import com.sqli.capsulescanner.data.api.googleVision.ImageContext
import com.sqli.capsulescanner.data.api.googleVision.ProductSearchParams
import com.sqli.capsulescanner.data.api.googleVision.Request
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
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

class MainDataSourceImpl @Inject constructor(
    private val apiService: ApiService,
    private val openAIApiService: OpenAIApiService,
    private val googleVisionApiService: GoogleVisionApiService,
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

            AppConstants.Processors.GOOGLE_VISION -> {
                val googleVisionRequest = GoogleVisionRequest(
                    requests = listOf(
                        Request(
                            features = listOf(
                                Feature(
                                    maxResults = 1,
                                    type = "PRODUCT_SEARCH"
                                )
                            ),
                            image = Image(
                                content = "${
                                    uriToBase64(
                                        MainApplication.instance.applicationContext,
                                        imageData.imageURI
                                    )
                                }"
                            ),
                            imageContext = ImageContext(
                                productSearchParams = ProductSearchParams(
                                    productCategories = listOf("general-v1"),
                                    productSet = "projects/${VISION_API_PROJECT_ID}/locations/${VISION_API_LOCATION_ID}/productSets/${VISION_API_PRODUCT_SET_ID}"
                                )
                            )
                        )
                    )
                )
                try {
                    val response = googleVisionApiService.process(
                        googleVisionRequest = googleVisionRequest,
                        visionApiKey = BuildConfig.VISION_API_KEY
                    )
                    return if (response.isSuccessful) {
                        val responseContent = response.body()?.string() ?: ""
                        val jsonObject = ((JSONObject(responseContent).getJSONArray("responses").get(0) as JSONObject).getJSONObject("productSearchResults").getJSONArray("results").get(0) as JSONObject).getJSONObject("product").apply { remove("name") }.toString()
                        val dataResponse = DataResponse(
                            response = jsonObject,
                            localUri = imageData.imageURI,
                            content = jsonObject,
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

            AppConstants.Processors.OPEN_AI -> {
                val openAIRequest = OpenAIRequest(
                    max_tokens = 1000,
                    messages = listOf(
                        Message(
                            content = listOf(
                                Content(
                                    image_url = ImageUrl(
                                        url = "data:image/jpeg;base64,${
                                            uriToBase64(
                                                MainApplication.instance.applicationContext,
                                                imageData.imageURI
                                            )
                                        }"
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
                            response = responseContent.choices[0].message.content.replace("```", "")
                                .replace("json", "").replace("\n", ""),
                            localUri = imageData.imageURI,
                            content = responseContent.choices[0].message.content.replace("```", "")
                                .replace("json", "").replace("\n", ""),
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