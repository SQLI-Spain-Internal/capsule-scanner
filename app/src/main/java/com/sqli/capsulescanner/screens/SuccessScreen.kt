package com.sqli.capsulescanner.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
            text = stringResource(id = R.string.successful),
            modifier = Modifier.padding(vertical = Dimens.dp_8)
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
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .padding(all = Dimens.dp_8)
                //.height(Dimens.dp_60)
                .weight(1f)
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
                        text = "$key:", style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = value, style = MaterialTheme.typography.bodyMedium)
                }
            }

        }
    }

}
