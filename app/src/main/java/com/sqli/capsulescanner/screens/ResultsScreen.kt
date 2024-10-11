package com.sqli.capsulescanner.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sqli.capsulescanner.R
import com.sqli.capsulescanner.components.Loader
import com.sqli.capsulescanner.entity.DataResponse
import com.sqli.capsulescanner.utilities.ResourceState
import com.sqli.capsulescanner.viewmodel.MainViewModel

@ExperimentalMaterial3Api
@Composable
fun ResultsScreen(
    mainViewModel: MainViewModel,
    onBackPressClick: () -> Unit,
    onRetry: () -> Unit,
) {
    val context = LocalContext.current
    val selectedResult by mainViewModel.dataState.collectAsStateWithLifecycle()
    val processorSelected by mainViewModel.processorSelectedState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {

            when (selectedResult) {

                is ResourceState.Success -> {
                    /**
                     * Successful
                     */
                    SuccessScreen(
                        dataResponse = (selectedResult as ResourceState.Success<DataResponse>).data
                    )
                }

                is ResourceState.Error -> {
                    /**
                     * Data Error
                     */
                    if ((selectedResult as ResourceState.Error).error == "Data error")
                        DataErrorScreen()
                    else
                        NetworkErrorScreen()
                }

                is ResourceState.Loading -> {
                    /**
                     * Network Error
                     */
                    Loader()
                }
            }
        }
        if (selectedResult is ResourceState.Success || selectedResult is ResourceState.Error) {
            ExpandableFAB(
                onBackPressClick = onBackPressClick,
                onRetry = onRetry
            )
        }

    }

}

@Composable
fun ExpandableFAB(
    onBackPressClick: () -> Unit,
    onRetry: () -> Unit,
) {
    var isExpanded by remember { mutableStateOf(false) }

    val scale = animateFloatAsState(if (isExpanded) 1f else 0f, label = "scale")
    val offsetY1 = animateDpAsState(if (isExpanded) (-70).dp else 0.dp, label = "offsetY1")
    val offsetY2 = animateDpAsState(if (isExpanded) (-140).dp else 0.dp, label = "offsetY2")

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        FloatingActionButton(
            onClick = {
                onRetry()
            },
            modifier = Modifier
                .offset(y = offsetY1.value)
                .padding(25.dp)
                .scale(scale.value),
            shape = CircleShape,
            containerColor = colorResource(id = R.color.orange)
        ) {
            Icon(Icons.Default.CameraAlt, contentDescription = "Camera")
        }

        FloatingActionButton(
            onClick = {
                onBackPressClick()
            },
            modifier = Modifier
                .offset(y = offsetY2.value)
                .padding(30.dp)
                .scale(scale.value),
            shape = CircleShape,
            containerColor = colorResource(id = R.color.red_intense)
        ) {
            Icon(Icons.Default.Home, contentDescription = "Home")
        }

        FloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp),
            onClick = { isExpanded = !isExpanded },
            shape = CircleShape,
            containerColor = colorResource(id = R.color.teal_200)
        ) {
            Icon(
                imageVector = if (!isExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                contentDescription = "Actions"
            )
        }
    }
}