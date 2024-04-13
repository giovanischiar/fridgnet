package io.schiar.fridgnet.view.administrationunits.component

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import io.schiar.fridgnet.R

/**
 * A composable representing a menu with options displayed as a vertical ellipsis (...) button in
 * the toolbar. When clicked, it expands to reveal a dropdown menu with options based on the
 * provided string IDs.
 *
 * @param itemTitleIDs a list of string resource IDs for the titles of the options in the menu.
 * @param onItemPressedAt an event fired when an option is selected. It sends the index of the
 * selected option as a parameter.
 */
@Composable
fun ToolbarMenuItems(itemTitleIDs: List<Int>, onItemPressedAt: (index: Int) -> Unit) {
    var expanded by remember { mutableStateOf(value = false) }
    Box {
        IconButton(onClick = { expanded = !expanded }) {
            Icon(
                imageVector = Icons.Outlined.MoreVert,
                contentDescription = "More",
                tint = colorResource(id = R.color.white)
            )
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            itemTitleIDs.mapIndexed { index, titleID ->
                DropdownMenuItem(
                    text = { Text(stringResource(id = titleID)) },
                    onClick = {
                        expanded = false
                        onItemPressedAt(index)
                    }
                )
            }
        }
    }
}