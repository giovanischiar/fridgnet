package io.schiar.fridgnet.view.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdministrativeUnitDropdown(
    administrativeUnits: List<String>,
    currentAdministrativeUnit: String,
    onDropdown: (administrativeUnit: String) -> Unit
) {
    fun textOf(administrativeUnit: String): String {
        return when (administrativeUnit) {
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
            value = textOf(currentAdministrativeUnit),
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            administrativeUnits.map { administrativeUnit ->
                DropdownMenuItem(
                    modifier = Modifier.fillMaxWidth(),
                    text = { Text(textOf(administrativeUnit)) },
                    onClick = {
                        expanded = false
                        onDropdown(administrativeUnit)
                    }
                )
            }
        }
    }
}