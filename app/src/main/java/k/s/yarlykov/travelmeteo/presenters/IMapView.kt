package k.s.yarlykov.travelmeteo.presenters

import android.os.Bundle

interface IMapView {
    fun initViews(savedInstanceState: Bundle?)
    fun setBottomSheetState(state: Int)
    fun setBottomSheetSizing()
    fun setBottomSheetVisibility(hideContent: Boolean)
}