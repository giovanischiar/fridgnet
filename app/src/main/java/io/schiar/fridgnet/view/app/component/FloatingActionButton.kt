package io.schiar.fridgnet.view.app.component

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import io.schiar.fridgnet.R

@Composable
fun FloatingActionButton(onPress: () -> Unit) {
    androidx.compose.material3.FloatingActionButton(
        onClick = onPress,
        containerColor = colorResource(id = R.color.imperial_red_500)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_add_photo),
            contentDescription = stringResource(id = R.string.add_image),
            tint = colorResource(id = R.color.white)
        )
    }
}