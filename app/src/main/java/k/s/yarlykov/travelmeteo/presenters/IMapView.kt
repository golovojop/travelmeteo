package k.s.yarlykov.travelmeteo.presenters

import k.s.yarlykov.travelmeteo.data.domain.CustomForecastModel

interface IMapView {
    fun initViews()
    fun setBottomSheetState(state: Int)
    fun setBottomSheetSizing()
    fun setBottomSheetVisibility(hideContent: Boolean)
    fun updateForecastData(model: CustomForecastModel?)
}