package com.sqli.capsulescanner.screens

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.annotation.AnyRes
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.sqli.capsulescanner.R
import com.sqli.capsulescanner.entity.DataResponse
import com.sqli.capsulescanner.ui.theme.AppTheme
import com.sqli.capsulescanner.ui.theme.Dimens
import com.sqli.capsulescanner.ui.theme.OrangeRed
import com.sqli.capsulescanner.utilities.ResourceState
import com.sqli.capsulescanner.viewmodel.MainViewModel
import org.json.JSONObject
import java.io.File

@ExperimentalMaterial3Api
@Composable
fun HomeScreen(
    mainViewModel: MainViewModel,
    onItemSelected: () -> Unit,
    onScanCapsule: () -> Unit,
) {
    val history = mainViewModel.history.collectAsStateWithLifecycle()
    HomeScreen(
        history = history.value,
        onScanCapsule = onScanCapsule,
        onItemSelected = {
            mainViewModel.setData(ResourceState.Success(it))
            onItemSelected()
        }
    )
}

@Composable
fun HomeScreen(
    history: MutableList<DataResponse>,
    onItemSelected: (DataResponse) -> Unit,
    onScanCapsule: () -> Unit,
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (history.isEmpty()) {
            Text(
                text = stringResource(id = R.string.no_history),
                modifier =
                Modifier
                    .align(alignment = Alignment.TopCenter)
                    .padding(top = 100.dp),
                textAlign = TextAlign.Center,
                fontSize = 24.sp,
                fontFamily = FontFamily.Monospace
            )
            Image(
                modifier = Modifier
                    .padding(all = 20.dp)
                    .fillMaxWidth(fraction = 0.8f)
                    .clip(shape = CircleShape)
                    .alpha(alpha = 0.5f)
                    .align(alignment = Alignment.Center),
                contentScale = ContentScale.FillWidth,
                painter = painterResource(id = R.drawable.capsules),
                contentDescription = "card"
            )

            val infiniteTransition = rememberInfiniteTransition(label = "Arrow")
            val animatedOffset by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 20f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ), label = ""
            )

            Image(
                painter = painterResource(id = R.drawable.balck_arrow),
                contentDescription = "Arrow",
                modifier = Modifier
                    .align(Alignment.Center)
                    .scale(1.5f)
                    .offset(y = animatedOffset.dp)
                    .rotate(-150.0f)
            )
        } else {
            LazyColumn {
                itemsIndexed(history) { index, item ->
                    if (index == 0) {
                        Text(
                            text = stringResource(id = R.string.your_history),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(all = 20.dp)
                                .align(alignment = Alignment.Center),
                            textAlign = TextAlign.Center,
                            fontSize = 24.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    ListItem(
                        item = item,
                        onItemSelected = {
                            onItemSelected(item)
                        })
                }
            }
        }

        FloatingActionButton(
            onClick = {
                onScanCapsule()
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp),
            shape = CircleShape,
            containerColor = colorResource(id = R.color.orange)
        ) {
            Icon(Icons.Default.PhotoCamera, contentDescription = "Scan")
        }
    }
}

@Composable
fun ListItem(
    item: DataResponse,
    onItemSelected: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.White)
            .clip(RoundedCornerShape(8.dp))
            .clickable {
                onItemSelected()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = item.localUri),
            contentDescription = "Item Image",
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(16.dp))

        val jsonObject = JSONObject(item.content)

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(Dimens.dp_16)
        ) {

            jsonObject.keys().asSequence().take(1).forEach { key ->
                val value = jsonObject.getString(key)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = FontFamily.Monospace,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "Info",
            modifier = Modifier
                .padding(10.dp)
                .clickable {
                    onItemSelected()
                },
            tint = OrangeRed
        )
    }
}

@ExperimentalMaterial3Api
@Preview
@Composable
fun HomeScreenPreview() {
    AppTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
            HomeScreen(
                history = mutableListOf(),
                onItemSelected = {},
                onScanCapsule = {},
            )
        }
    }
}

@ExperimentalMaterial3Api
@Preview
@Composable
fun HomeScreenListPreview() {
    AppTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
            HomeScreen(
                history = mutableListOf(
                    DataResponse(
                        response = "",
                        localUri = Uri.EMPTY,
                        content = "{\n" +
                                "              \"name\": \"projects/capsule-scanner/locations/europe-west1/products/cap_012\",\n" +
                                "              \"displayName\": \"Stockholm Lungo\",\n" +
                                "              \"productCategory\": \"general-v1\",\n" +
                                "              \"productLabels\": [\n" +
                                "                {\n" +
                                "                  \"key\": \"intensity\",\n" +
                                "                  \"value\": \"8\"\n" +
                                "                },\n" +
                                "                {\n" +
                                "                  \"key\": \"url\",\n" +
                                "                  \"value\": \"https://www.nespresso.com/es/en/order/capsules/original/stockholm-lungo-coffee-capsules\"\n" +
                                "                },\n" +
                                "                {\n" +
                                "                  \"key\": \" description \",\n" +
                                "                  \"value\": \" Warm heritage rich & full-bodied\"\n" +
                                "                }\n" +
                                "              ]\n" +
                                "            }"
                    )
                ),
                onItemSelected = {},
                onScanCapsule = {},
            )
        }
    }
}


