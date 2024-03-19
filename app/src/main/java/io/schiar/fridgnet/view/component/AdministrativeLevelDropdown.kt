package io.schiar.fridgnet.view.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdministrativeLevelDropdown(
    administrativeLevels: List<String>,
    currentAdministrativeLevel: String,
    onDropdown: (index: Int) -> Unit
) {
    fun textOf(administrativeLevel: String): String {
        return when (administrativeLevel) {
            "CITY" -> "Cities"
            "COUNTY" -> "Counties"
            "STATE" -> "States"
            "COUNTRY" -> "Countries"
            else -> "Cities"
        }
    }

    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            modifier = Modifier.menuAnchor(),
            value = textOf(currentAdministrativeLevel),
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            administrativeLevels.mapIndexed { index, administrativeLevel ->
                DropdownMenuItem(
                    modifier = Modifier.fillMaxWidth(),
                    text = { Text(textOf(administrativeLevel)) },
                    onClick = {
                        expanded = false
                        onDropdown(index)
                    }
                )
            }
        }
    }
}