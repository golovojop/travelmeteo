package k.s.yarlykov.travelmeteo.presenters

import android.graphics.Bitmap
import com.google.android.gms.maps.model.LatLng
import io.reactivex.disposables.CompositeDisposable
import k.s.yarlykov.travelmeteo.data.domain.CustomForecastModel
import k.s.yarlykov.travelmeteo.data.sources.openweather.model.current.WeatherResponseModel
import k.s.yarlykov.travelmeteo.data.sources.unifiedprovider.ForecastConsumer
import k.s.yarlykov.travelmeteo.data.sources.unifiedprovider.WeatherProvider
import k.s.yarlykov.travelmeteo.data.sources.unifiedprovider.WeatherProviderRx
import k.s.yarlykov.travelmeteo.ui.MapActivity

class MapPresenter(var mapView: IMapView, private val wp: WeatherProviderRx) : IMapPresenter, ForecastConsumer {

    private var isMapReady = false
    private val meteoDataSource = wp.getForecastPublisher()
    private val compositeDisposable = CompositeDisposable()

    //region Activity Life Cycle

    // Вызывается, если приложению предоставлены разрешения ACCESS_X_LOCATION
    override fun onCreate() {
        MapActivity.logIt("MapPresenter: onCreate()")

        // Подписаться на получение данных из модели
        compositeDisposable.add(
            meteoDataSource.subscribe { model ->
                mapView.updateForecastData(model)
            }
        )

        // Загрузить макет
        mapView.initViews()
        // Загрузить карту
        mapView.loadMap()
    }

    override fun onResume() {
        MapActivity.logIt("MapPresenter: onResume(). isMapScreenReady = ${isMapReady}")
        // Если карта загружена, то...
        if (isMapReady) {
            mapView.setBottomSheetSizing()
        }
    }

    override fun onDestroy() {
        compositeDisposable.clear()
    }

    override fun onActivityLayoutLoaded() {
        MapActivity.logIt("MapPresenter: onActivityLayoutLoaded()")
    }
    //endregion

    //region Map Events Handlers
    override fun onMapLoaded() {
        MapActivity.logIt("MapPresenter: onMapLoaded()")
        isMapReady = true
    }

    override fun onMapClick(latLng: LatLng) {}

    override fun onMapLongClick(latLng: LatLng) {
        MapActivity.logIt("MapPresenter: onMapLongClick()")
        wp.requestForecastHourly(latLng)
    }

    override fun onSavedDataPresent(model: CustomForecastModel?) {
        model?.let {
            mapView.updateForecastData(model)
        }
    }
    //endregion

    //region ForecastConsumer
    override fun onForecastCurrent(model: WeatherResponseModel, icon: Bitmap) {
    }

    override fun onForecastHourly(model: CustomForecastModel) {
        if (model.list.isNotEmpty()) {
            mapView.updateForecastData(model)
        }
    }
    //endregion
}

