package com.sqli.capsulescanner.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sqli.capsulescanner.data.api.AppConstants
import com.sqli.capsulescanner.entity.DataResponse
import com.sqli.capsulescanner.entity.ImageData
import com.sqli.capsulescanner.entity.ProcessorOption
import com.sqli.capsulescanner.entity.ProcessorsResponse
import com.sqli.capsulescanner.repository.MainRepository
import com.sqli.capsulescanner.utilities.ResourceState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {

    private val _dataState: MutableStateFlow<ResourceState<DataResponse>> =
        MutableStateFlow(ResourceState.Loading)
    val dataState: StateFlow<ResourceState<DataResponse>> = _dataState

    private val _processorsState: MutableStateFlow<ResourceState<ProcessorsResponse>> =
        MutableStateFlow(
            ResourceState.Success(
                data = ProcessorsResponse(
                    listOf(
                        ProcessorOption(
                            processorId = 1,
                            title = "Default"
                        )
                    )
                )
            )
        )
    val processorsState: StateFlow<ResourceState<ProcessorsResponse>> = _processorsState

    private val _processorSelectedState: MutableStateFlow<ProcessorOption?> = MutableStateFlow(null)
    val processorSelectedState: StateFlow<ProcessorOption?> = _processorSelectedState

    private val _imageCapturedState: MutableStateFlow<Uri?> = MutableStateFlow(null)
    val imageCapturedState: StateFlow<Uri?> = _imageCapturedState

    fun setData(data: ResourceState<DataResponse>) {
        _dataState.value = data
        when (data) {
            is ResourceState.Success -> {
                (_dataState.value as ResourceState.Success).data.setImageUri(_imageCapturedState.value)
            }
        }

    }

    fun processImage() {
        viewModelScope.launch(Dispatchers.IO) {
            imageCapturedState.value?.let {
                mainRepository.process(
                    ImageData(
                        processor = when (processorSelectedState.value?.processorId) {
                            1 -> AppConstants.Processors.CUSTOM_PROCESSOR
                            2 -> AppConstants.Processors.LOCAL_PROCESSOR
                            3 -> AppConstants.Processors.OPEN_AI
                            else -> AppConstants.Processors.OPEN_AI //maybe not default :/
                        },
                        imageURI = it,
                        info = "" // maybe useful as a question

                    )
                )
                    .collectLatest { dataResponse ->
                        _dataState.value = dataResponse
                    }
            }

        }
    }

    fun getProcessorsAvailable() {
        viewModelScope.launch(Dispatchers.IO) {
            mainRepository.getProcessorsAvailable()
                .collectLatest { dataResponse ->
                    /**
                     * Use response from API
                     */
                    //_processorsState.value = dataResponse
                    _processorsState.value =
                        ResourceState.Success(
                            data = ProcessorsResponse(
                                lists = listOf(
                                    ProcessorOption(
                                        processorId = 1,
                                        title = "Option 1"
                                    ),
                                    ProcessorOption(
                                        processorId = 2,
                                        title = "Option 2"
                                    ),
                                    ProcessorOption(
                                        processorId = 3,
                                        title = "Open AI"
                                    ),
                                )
                            )
                        )
                }
        }
    }

    fun setSelectedProcessor(processor: ProcessorOption?) {
        _processorSelectedState.value = processor
    }

    fun setImageCapture(uri: Uri) {
        _imageCapturedState.value = uri
    }

}