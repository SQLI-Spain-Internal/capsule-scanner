package com.sqli.capsulescanner.navigation.ui

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import java.io.OutputStream

fun replaceImageInUri(context: Context, imageBitmap: ImageBitmap, targetUri: Uri) {
    try {
        val bitmap: Bitmap = imageBitmap.asAndroidBitmap()

        val outputStream: OutputStream? = context.contentResolver.openOutputStream(targetUri)

        outputStream?.use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}