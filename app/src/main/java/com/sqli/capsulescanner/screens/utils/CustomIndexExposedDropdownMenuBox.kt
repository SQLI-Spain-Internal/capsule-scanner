package com.sqli.capsulescanner.screens.utils

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sqli.capsulescanner.ui.theme.Dimens

@ExperimentalMaterial3Api
@Composable
fun CustomIndexExposedDropdownMenuBox(
    valueToCompare: String?,
    indexSelected: Int?,
    header: String,
    elements: List<String>,
    size: Dp?,
    enabled: Boolean,
    onValueChange: (String, Boolean, Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var indexS by remember {
        mutableStateOf(indexSelected)
    }
    var text by remember {
        mutableStateOf("Selecciona...")
    }

    text = indexS?.let {
        elements.getOrNull(it + 1)
    } ?: "Selecciona..."

    Column(
        modifier = Modifier.padding(horizontal = Dimens.dp_8)
    ) {
        Text(text = header)
        Box(
            modifier = Modifier
                .width(size ?: 250.dp)
                .padding(vertical = Dimens.dp_8)
        ) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {
                    expanded = !expanded
                }
            ) {
                OutlinedTextField(
                    value = text,
                    onValueChange = {
                        //text = it
                    },
                    readOnly = true,
                    enabled = enabled,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor(),
                    maxLines = 1,
                    singleLine = true
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    elements.forEachIndexed { index, item ->
                        DropdownMenuItem(
                            text = { Text(text = item) },
                            onClick = {
                                if (enabled) {
                                    val result = if (index == 0) "" else (index - 1).toString()
                                    val match = item == valueToCompare
                                    onValueChange(result, match, index)
                                    expanded = false
                                    indexS = index - 1
                                }
                            },
                            enabled = enabled
                        )
                    }
                }
            }
        }
    }

}