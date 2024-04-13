package io.schiar.fridgnet.view.home

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import io.schiar.fridgnet.view.home.component.BottomBar
import io.schiar.fridgnet.view.home.component.FloatingActionButton
import io.schiar.fridgnet.view.home.component.Navigation
import io.schiar.fridgnet.view.home.component.PhotoPicker
import io.schiar.fridgnet.view.home.component.TopBar
import io.schiar.fridgnet.view.home.util.BottomNavScreen
import io.schiar.fridgnet.view.home.util.ScreenInfo
import io.schiar.fridgnet.viewmodel.HomeViewModel

/**
 * The Home Screen is the application's entry point and acts as a container for navigation to
 * other screens. It displays a toolbar with dynamic content based on the current screen,
 * a bottom bar for quick access to main sections, and a floating action button for adding photos.
 *
 * @param viewModel the corresponding viewModel that provides access to data and methods for
 * manipulating the Home Screen.
 * @param navController a reference to the navigation controller used for navigating between
 * screens. If not provided, a new one will be created.
 */
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController()
) {
    var currentScreenInfo by remember { mutableStateOf(
        ScreenInfo(BottomNavScreen.AdministrativeUnits.route.id))
    }
    var isPhotoPickerShowing by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopBar(navController = navController, screenInfo = currentScreenInfo) },
        floatingActionButton = { FloatingActionButton { isPhotoPickerShowing = true } },
        bottomBar = { BottomBar(navController = navController) }
    ) { innerPadding ->
        Navigation(
            navController = navController,
            innerPadding = innerPadding,
            onChangeScreenInfo = { screenInfo -> currentScreenInfo = screenInfo }
        )
    }

    if (isPhotoPickerShowing) {
        PhotoPicker { uris ->
            viewModel.addURIs(uris = uris)
            isPhotoPickerShowing = false
        }
    }
}