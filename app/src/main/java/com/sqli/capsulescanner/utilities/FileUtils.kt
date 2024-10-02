package com.sqli.capsulescanner.utilities

import android.content.Context
import android.content.res.AssetManager
import android.net.Uri
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.Random

fun getRandomAssetFile(context: Context): String? {
    val assetManager: AssetManager = context.assets

    return try {
        val files = assetManager.list("")?.filter { it.contains(".json") }

        if (!files.isNullOrEmpty()) {
            val randomIndex = Random().nextInt(files.size)
            files[randomIndex]
        } else {
            null
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun readJsonFromAssets(context: Context, fileName: String): String {
    return try {
        val inputStream = context.assets.open(fileName)
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()

        String(buffer, Charsets.UTF_8)
    } catch (ex: Exception) {
        ex.printStackTrace()
        "Error"
    }
}

fun uriToBase64(context: Context, imageUri: Uri): String? {
    return try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)

        val byteArrayOutputStream = ByteArrayOutputStream()
        val buffer = ByteArray(1024)
        var bytesRead: Int
        while (inputStream?.read(buffer).also { bytesRead = it ?: -1 } != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead)
        }

        val imageBytes: ByteArray = byteArrayOutputStream.toByteArray()

        Base64.encodeToString(imageBytes, Base64.NO_WRAP)

    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}