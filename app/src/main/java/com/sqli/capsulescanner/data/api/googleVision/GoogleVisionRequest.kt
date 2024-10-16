package com.sqli.capsulescanner.data.api.googleVision

data class GoogleVisionRequest(
    val requests: List<Request>
)

data class Request(
    val features: List<Feature>,
    val image: Image,
    val imageContext: ImageContext
)

data class Feature(
    val maxResults: Int,
    val type: String
)

data class Image(
    val content: String
)

data class ImageContext(
    val productSearchParams: ProductSearchParams
)

data class ProductSearchParams(
    val productCategories: List<String>,
    val productSet: String
)