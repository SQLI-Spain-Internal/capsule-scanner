package com.sqli.capsulescanner.entity

import android.net.Uri
import com.google.gson.annotations.SerializedName
import com.sqli.capsulescanner.data.api.AppConstants

data class ImageData (
    val processor: AppConstants.Processors,
    val imageURI : Uri,

    @SerializedName("info")
    val info: String,
    /**
     * Define struct here
     */
)