package k.s.yarlykov.travelmeteo.presenters

import android.content.Context
import android.graphics.Bitmap
import com.google.android.gms.maps.model.LatLng
import k.s.yarlykov.travelmeteo.data.domain.CustomForecastModel
import k.s.yarlykov.travelmeteo.data.sources.openweather.model.current.WeatherResponseModel
import k.s.yarlykov.travelmeteo.data.sources.unifiedprovider.ForecastConsumer
import k.s.yarlykov.travelmeteo.data.sources.unifiedprovider.WeatherProvider

class MapPresenter(mapView: IMapView, val wp: WeatherProvider) : IMapPresenter, ForecastConsumer {

    private var view: IMapView? = mapView

    //region Activity Life Cycle
    override fun onCreate() {
    }

    override fun onResume() {
    }

    override fun onDestroy() {
        view = null
    }
    //endregion

    override fun onMapClick(latLng: LatLng) {
    }

    override fun onMapLongClick(latLng: LatLng) {
        wp.requestForecastHourly(this, latLng)
    }

    //region ForecastConsumer
    override fun onForecastCurrent(model: WeatherResponseModel, icon: Bitmap) {
    }

    override fun onForecastHourly(model: CustomForecastModel) {
        if (!model.list.isEmpty()) {
            view?.updateForecastData(model)
        }
    }
}

