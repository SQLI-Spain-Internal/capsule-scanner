package com.sqli.capsulescanner.navigation

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sqli.capsulescanner.R
import com.sqli.capsulescanner.entity.ProcessorOption
import com.sqli.capsulescanner.navigation.ui.DialogFormProcessorSelector
import com.sqli.capsulescanner.navigation.ui.replaceImageInUri
import com.sqli.capsulescanner.screens.HomeScreen
import com.sqli.capsulescanner.screens.ResultsScreen
import com.sqli.capsulescanner.ui.theme.Dimens
import com.sqli.capsulescanner.ui.theme.MainGreen
import com.sqli.capsulescanner.utilities.ResourceState
import com.sqli.capsulescanner.viewmodel.MainViewModel
import io.moyuru.cropify.Cropify
import io.moyuru.cropify.rememberCropifyState

@ExperimentalMaterial3Api
@Composable
fun AppNavigationGraph(
    mainViewModel: MainViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    var showProcessDialog by remember { mutableStateOf(false) }
    var processorSelected by remember { mutableStateOf<ProcessorOption?>(null) }
    var imageCaptureResult by remember { mutableStateOf("") }
    val optionsList by mainViewModel.processorsState.collectAsStateWithLifecycle()
    val state = rememberCropifyState()
    fun navigateWithPopUp(from: String, to: String) {
        navController.navigate(to) {
            launchSingleTop = true
            popUpTo(from) {
                inclusive = true
            }
        }
    }
    if (showProcessDialog) {
        mainViewModel.getProcessorsAvailable()
        AlertDialog(onDismissRequest = {},
            confirmButton = {
                Column(modifier = Modifier.fillMaxWidth()) {

                    Cropify(
                        uri = Uri.parse(imageCaptureResult),
                        state = state,
                        onImageCropped = {
                            replaceImageInUri(
                                context = context,
                                imageBitmap =  it,
                                targetUri = Uri.parse(imageCaptureResult))
                        },
                        onFailedToLoadImage = {

                        },
                        modifier = Modifier.size(300.dp)
                            .padding(all = Dimens.dp_8)
                            .weight(1f)
                    )

                    DialogFormProcessorSelector(
                        optionsList = (optionsList as ResourceState.Success).data.lists,
                        onOptionSelected = { processor ->
                            state.crop()
                            processorSelected = processor
                            mainViewModel.setSelectedProcessor(processorSelected)
                        }
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = {
                                showProcessDialog = false
                                /**
                                 * process image
                                 */
                                mainViewModel.setSelectedProcessor(processorSelected)
                                mainViewModel.setData(ResourceState.Loading)
                                mainViewModel.processImage()
                                navController.navigate(Routes.RESULTS_SCREEN)
                            },
                            modifier = Modifier
                                .padding(all = Dimens.medium_padding)
                                .background(color = MainGreen),
                        ) {
                            Text(
                                text = stringResource(id = R.string.process),
                                color = colorResource(id = R.color.white),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                }

            })
    }

    NavHost(navController = navController, startDestination = Routes.HOME_SCREEN) {

        composable(Routes.HOME_SCREEN) {
            HomeScreen(
                mainViewModel = mainViewModel,
                onScanCapsule = {
                    showProcessDialog = true
                    imageCaptureResult = it
                })

        }

        composable(Routes.RESULTS_SCREEN) {
            ResultsScreen(
                mainViewModel = mainViewModel,
                onBackPressClick = {
                    navController.navigateUp()
                },
                onRetry = {
                    navController.navigateUp()
                })

        }
    }

}
