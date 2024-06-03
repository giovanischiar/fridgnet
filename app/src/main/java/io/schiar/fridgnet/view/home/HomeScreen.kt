package io.schiar.fridgnet.view.home

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import io.schiar.fridgnet.R
import io.schiar.fridgnet.view.home.component.FloatingActionButton
import io.schiar.fridgnet.view.home.component.PhotoPicker
import io.schiar.fridgnet.view.home.component.ScreenBarNavigator
import io.schiar.fridgnet.view.home.component.TopBar
import io.schiar.fridgnet.view.home.util.Screen
import io.schiar.fridgnet.view.home.util.ScreenInfo
import io.schiar.fridgnet.view.navigation.Navigation

/**
 * The Home Screen is the application's entry point and acts as a container for navigation to
 * other screens. It displays a toolbar with dynamic content based on the current screen,
 * a bottom bar for quick access to main sections, and a floating action button for adding photos.
 *
 * @param addURIs a method that calls when all images uris are selected.
 * @param navController a reference to the navigation controller used for navigating between
 * screens. If not provided, a new one will be created.
 */
@Composable
fun HomeScreen(
    addURIs: (uris: List<String>) -> Unit,
    navController: NavHostController = rememberNavController()
) {
    val screens = remember {
        listOf(Screen.AdministrativeUnits, Screen.RegionsAndImages)
    }
    var currentScreenInfo by remember { mutableStateOf(
        ScreenInfo(Screen.AdministrativeUnits.route.id))
    }
    var isPhotoPickerShowing by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopBar(navController = navController, screenInfo = currentScreenInfo) },
        floatingActionButton = {
            FloatingActionButton(
                iconDrawableID = R.drawable.ic_add_photo,
                contentDescriptionStringID = R.string.add_image
            ) {
                isPhotoPickerShowing = true
            }
        },
        bottomBar = { ScreenBarNavigator(screens = screens, navController = navController) }
    ) { innerPadding ->
        Navigation(
            navController = navController,
            innerPadding = innerPadding,
            onChangeToolbarInfo = { screenInfo -> currentScreenInfo = screenInfo }
        )
    }

    if (isPhotoPickerShowing) {
        PhotoPicker(onURIsSelected = { uris ->
            addURIs(uris)
            isPhotoPickerShowing = false

        })
    }
}