package io.schiar.fridgnet.view.screen.administrationunits.component

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
import androidx.compose.ui.platform.LocalContext
import io.schiar.fridgnet.view.util.getResourceString
import io.schiar.fridgnet.view.viewdata.AdministrativeLevelViewData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdministrativeLevelDropdown(
    administrativeLevels: List<AdministrativeLevelViewData>,
    currentAdministrativeLevel: AdministrativeLevelViewData,
    onDropdown: (index: Int) -> Unit
) {

    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            modifier = Modifier.menuAnchor(),
            value = currentAdministrativeLevel.getResourceString(context),
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            administrativeLevels.mapIndexed { index, administrativeLevel ->
                val titleResource = administrativeLevel.getResourceString(context)
                DropdownMenuItem(
                    modifier = Modifier.fillMaxWidth(),
                    text = { Text(titleResource) },
                    onClick = {
                        expanded = false
                        onDropdown(index)
                    }
                )
            }
        }
    }
}