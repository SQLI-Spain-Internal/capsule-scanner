package com.sqli.capsulescanner.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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

    Column(
        modifier = Modifier.padding(16.dp)
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

}

@ExperimentalMaterial3Api
@Composable
fun ResultsScreen(
    onBackPressClick: () -> Unit,
) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {

    }
}