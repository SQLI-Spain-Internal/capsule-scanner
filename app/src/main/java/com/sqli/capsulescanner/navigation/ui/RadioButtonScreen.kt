package com.sqli.capsulescanner.navigation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sqli.capsulescanner.R
import com.sqli.capsulescanner.entity.ProcessorOption

@Composable
fun RadioButtonScreen(
    optionsList: List<ProcessorOption>,
    onOptionSelected: (ProcessorOption) -> Unit
) {
    var selectedOption by remember { mutableStateOf(optionsList[0]) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(stringResource(id = R.string.option_select))

        optionsList.forEach { option ->
            SingleSelectionRadioButton(
                option = option,
                selected = option == selectedOption,
                onOptionSelected = {
                    selectedOption = it
                    onOptionSelected(selectedOption)
                }
            )
        }
    }
}

@Composable
fun SingleSelectionRadioButton(
    option: ProcessorOption,
    selected: Boolean,
    onOptionSelected: (ProcessorOption) -> Unit
) {
    Row(
        modifier = Modifier.padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = { onOptionSelected(option) }
        )
        Text(
            text = option.title,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    RadioButtonScreen(
        optionsList = listOf(
            ProcessorOption(1, "option 1"),
            ProcessorOption(2, "option 2"),
            ProcessorOption(3, "option 3")
        ),
        {}
    )
}