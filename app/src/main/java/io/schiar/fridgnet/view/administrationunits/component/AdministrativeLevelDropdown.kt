package io.schiar.fridgnet.view.administrationunits.component

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
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
import io.schiar.fridgnet.view.administrationunits.uistate.AdministrativeLevelsUiState
import io.schiar.fridgnet.view.administrationunits.uistate.CurrentAdministrativeLevelUiState
import io.schiar.fridgnet.view.shared.component.Loading
import io.schiar.fridgnet.view.shared.util.getResourceString

/**
 * The administrative levels dropdown used in the toolbar on Administrative Unit Screen.
 *
 * @param administrativeLevelsUiState the ui state [List] of administrative levels used in the
 * dropdown.
 * @param currentAdministrativeLevelUiState the ui state of current administrative level displayed.
 * @param onDropdown the event fired when another option is selected in the dropdown. The index of
 * the selected option is sent via parameter.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdministrativeLevelDropdown(
    administrativeLevelsUiState: AdministrativeLevelsUiState,
    currentAdministrativeLevelUiState: CurrentAdministrativeLevelUiState,
    onDropdown: (index: Int) -> Unit
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }

    val currentAdministrativeLevel = when (currentAdministrativeLevelUiState) {
        is CurrentAdministrativeLevelUiState.Loading -> ""

        is CurrentAdministrativeLevelUiState.CurrentAdministrativeLevelLoaded -> {
            currentAdministrativeLevelUiState.currentAdministrativeLevel
                .getResourceString(context)
        }
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {

        TextField(
            modifier = Modifier.menuAnchor().width(IntrinsicSize.Min),
            value = currentAdministrativeLevel,
            placeholder = {
                when (currentAdministrativeLevelUiState) {
                    is CurrentAdministrativeLevelUiState.Loading -> Loading()
                    else -> {}
                }
            },
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
        )

        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            when (administrativeLevelsUiState) {
                is AdministrativeLevelsUiState.Loading -> Loading()

                is AdministrativeLevelsUiState.AdministrativeLevelsLoaded -> {
                    administrativeLevelsUiState.administrativeLevels
                        .mapIndexed { index, administrativeLevel ->
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
    }
}