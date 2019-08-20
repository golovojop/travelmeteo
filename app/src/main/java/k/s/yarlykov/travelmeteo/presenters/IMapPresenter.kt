package k.s.yarlykov.travelmeteo.presenters

import com.google.android.gms.maps.model.LatLng

interface IMapPresenter {
    fun onCreate()
    fun onResume()
    fun onDestroy()
    fun onMapClick(latLng: LatLng)
    fun onMapLongClick(latLng: LatLng)
}