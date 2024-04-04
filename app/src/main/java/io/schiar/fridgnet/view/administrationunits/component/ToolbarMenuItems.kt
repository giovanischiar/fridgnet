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
//                        viewModel.removeAllImages()
//
                    }
                )
            }
        }
    }
}