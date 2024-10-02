package com.sqli.capsulescanner.screens

import android.Manifest
import android.content.ContentValues
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.sqli.capsulescanner.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@ExperimentalMaterial3Api
@Composable
fun HomeScreen(
    mainViewModel: MainViewModel,
    onScanCapsule: (String) -> Unit,
) {
    CameraScreen(
        onImageCapture = { uri ->
            mainViewModel.setImageCapture(uri)
        },
        onScanCapsule = onScanCapsule
    )
}

@Composable
fun CameraScreen(
    onImageCapture: (Uri) -> Unit,
    onScanCapsule: (String) -> Unit,
) {
    val context = LocalContext.current
    var hasCameraPermission by remember { mutableStateOf(false) }

    // Check if the camera permission is granted
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        hasCameraPermission = isGranted
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    if (hasCameraPermission) {
        CameraPreview(
            onScanCapsule = onScanCapsule,
            onImageCapture = onImageCapture
        )
    } else {
        Text("Camera permission is required")
    }
}

@Composable
fun CameraPreview(
    onImageCapture: (Uri) -> Unit,
    onScanCapsule: (String) -> Unit,
) {
    val context = LocalContext.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var previewView: PreviewView? = null
    val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        AndroidView(
            factory = { ctx ->
                previewView = PreviewView(ctx)
                previewView!!.apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
                previewView!!
            },
            modifier = Modifier.fillMaxSize(1f),
            update = { view ->
                val cameraProvider = cameraProviderFuture.addListener(
                    {
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = androidx.camera.core.Preview.Builder().build().also {
                            it.setSurfaceProvider(view.surfaceProvider)
                        }
                        imageCapture = ImageCapture.Builder().build()
                        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                        try {
                            cameraProvider.unbindAll()

                            cameraProvider.bindToLifecycle(
                                context as ComponentActivity,
                                cameraSelector,
                                preview,
                                imageCapture
                            )
                        } catch (exc: Exception) {
                            exc.printStackTrace()
                        }

                    }, ContextCompat.getMainExecutor(context)
                )

            }
        )
        Button(
            onClick = {
                imageCapture?.let {
                    captureImage(
                        context = context, it, cameraExecutor,
                        onImageCapture = onImageCapture,
                        onScanCapsule = onScanCapsule
                    )
                }
            },
            modifier = Modifier
                .align(alignment = Alignment.BottomCenter)
                .absolutePadding(bottom = 16.dp)
                .size(80.dp)
                .clip(CircleShape)
                .background(Color.Gray)
                .padding(2.dp)
        ) {
        }
    }

}

fun captureImage(
    context: android.content.Context,
    imageCapture: ImageCapture,
    cameraExecutor: ExecutorService,
    onImageCapture: (Uri) -> Unit,
    onScanCapsule: (String) -> Unit,
) {
    val name = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
        .format(System.currentTimeMillis())

    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, name)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/CameraX-Images")
    }

    val outputOptions = ImageCapture.OutputFileOptions
        .Builder(
            context.contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
        .build()

    imageCapture.takePicture(
        outputOptions,
        cameraExecutor,
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val msg = "Photo saved successfully"
                Log.d("HomeScreen.captureImage", msg)
                onScanCapsule("${outputFileResults.savedUri}")
                outputFileResults.savedUri?.let { onImageCapture(it) }
            }

            override fun onError(exception: ImageCaptureException) {
                val msg = "Photo capture failed: ${exception.message}"
                Log.e("HomeScreen.captureImage", msg, exception)
                onScanCapsule(msg)
            }
        }
    )
}

