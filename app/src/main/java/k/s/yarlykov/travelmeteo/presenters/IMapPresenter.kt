package k.s.yarlykov.travelmeteo.presenters

import com.google.android.gms.maps.model.LatLng
import k.s.yarlykov.travelmeteo.data.domain.CustomForecastModel

interface IMapPresenter {
    fun onCreate()
    fun onResume()
    fun onDestroy()
    fun onPermissionsGranted()
    fun onMapScreenReady()
    fun onMapClick(latLng: LatLng)
    fun onMapLongClick(latLng: LatLng)
    fun onSavedDataPresent(model: CustomForecastModel?)
}