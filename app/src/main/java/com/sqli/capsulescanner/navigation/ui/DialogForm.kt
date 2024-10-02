package com.sqli.capsulescanner.navigation.ui

import androidx.compose.runtime.Composable
import com.sqli.capsulescanner.entity.ProcessorOption

@Composable
fun DialogFormProcessorSelector(
    optionsList: List<ProcessorOption>,
    onOptionSelected: (ProcessorOption) -> Unit
) {
    RadioButtonScreen(
        optionsList = optionsList,
        onOptionSelected = onOptionSelected
    )
}