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