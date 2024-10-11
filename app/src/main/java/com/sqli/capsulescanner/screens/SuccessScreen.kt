package com.sqli.capsulescanner.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.sqli.capsulescanner.R
import com.sqli.capsulescanner.components.Loader
import com.sqli.capsulescanner.entity.DataResponse
import com.sqli.capsulescanner.ui.theme.Dimens
import org.json.JSONObject

@ExperimentalMaterial3Api
@Composable
fun SuccessScreen(
    dataResponse: DataResponse
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = stringResource(id = R.string.processing_completed),
            modifier =
            Modifier
                .padding(top = 100.dp)
                .align(alignment = Alignment.CenterHorizontally),
            textAlign = TextAlign.Center,
            fontSize = 24.sp,
            fontFamily = FontFamily.Monospace,
        )

        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(dataResponse.localUri)
                .error(R.drawable.no_image_loaded)
                .crossfade(true)
                .build(),
            loading = {
                Loader()
            },
            onSuccess = {

            },
            contentDescription = stringResource(R.string.description),
            modifier = Modifier
                .padding(all = Dimens.dp_8)
                .weight(0.5f)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )

        ImageDataResultForm(dataResponse)

    }

}

@Composable
fun ImageDataResultForm(dataResponse: DataResponse) {
    val jsonObject = JSONObject(dataResponse.content)

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimens.dp_16)
    ) {

        jsonObject.keys().forEach { key ->
            val value = jsonObject.getString(key)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "$key:",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

        }
    }

}
