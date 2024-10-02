package com.sqli.capsulescanner.entity

import android.net.Uri

data class DataResponse (
    val response: String,
    var localUri: Uri?,
    var content: String?
    /**
     * Define struct here
     */
){
    fun setImageUri(uri: Uri?){
        localUri = uri
    }
}