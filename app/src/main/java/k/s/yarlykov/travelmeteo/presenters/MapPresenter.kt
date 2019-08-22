package k.s.yarlykov.travelmeteo.presenters

import android.content.Context
import android.graphics.Bitmap
import com.google.android.gms.maps.model.LatLng
import k.s.yarlykov.travelmeteo.data.domain.CustomForecastModel
import k.s.yarlykov.travelmeteo.data.sources.openweather.model.current.WeatherResponseModel
import k.s.yarlykov.travelmeteo.data.sources.unifiedprovider.ForecastConsumer
import k.s.yarlykov.travelmeteo.data.sources.unifiedprovider.WeatherProvider

class MapPresenter(var mapView: IMapView?, private val wp: WeatherProvider) : IMapPresenter, ForecastConsumer {

    private var isPermissionsGranted = false
    private var isMapScreenReady = false

    //region Activity Life Cycle
    override fun onCreate() {
        if(isPermissionsGranted) {
            mapView?.initViews()

        }
    }

    override fun onResume() {
        if(isPermissionsGranted && isMapScreenReady) {
            mapView?.setBottomSheetSizing()
        }
    }

    override fun onDestroy() {
        mapView = null
        isMapScreenReady = false
    }

    override fun onPermissions(isGranted: Boolean) {
        isPermissionsGranted = isGranted
    }

    override fun onMapScreenReady() {
        isMapScreenReady = true
    }
    //endregion

    //region Map Events Handlers
    override fun onMapClick(latLng: LatLng) {
    }

    override fun onMapLongClick(latLng: LatLng) {
        wp.requestForecastHourly(this, latLng)
    }
    //endregion

    //region ForecastConsumer
    override fun onForecastCurrent(model: WeatherResponseModel, icon: Bitmap) {
    }

    override fun onForecastHourly(model: CustomForecastModel) {
        if (model.list.isNotEmpty()) {
            mapView?.updateForecastData(model)
        }
    }
    //endregion
}

