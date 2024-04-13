package io.schiar.fridgnet.view.shared.viewdata

/**
 * The data necessary for displaying the AdministrativeLevel on the View.
 *
 * @property title the title of the AdministrativeLevel (e.g., "City", "County", "State").
 * @property columnCount the number of columns used in the grid layout for this level.
 * @property zIndex the layer order on the map where the boundary should be drawn. Higher zIndex
 * values are drawn on top of lower values (typically ranges from 0 for the bottom layer to higher
 * values for layers displayed on top).
 */
data class AdministrativeLevelViewData(
    val title: String,
    val columnCount: Int,
    val zIndex: Float
)