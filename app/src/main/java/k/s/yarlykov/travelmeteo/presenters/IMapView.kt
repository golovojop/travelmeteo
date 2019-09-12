package k.s.yarlykov.travelmeteo.presenters

import k.s.yarlykov.travelmeteo.data.domain.CustomForecastModel

interface IMapView {
    fun initViews()
    fun initMap()
    fun loadMap()
    fun setBottomSheetState(state: Int)
    fun setBottomSheetSizing()
    fun setBottomSheetVisibility(isVisible: Boolean)
    fun updateForecastData(model: CustomForecastModel?)
}