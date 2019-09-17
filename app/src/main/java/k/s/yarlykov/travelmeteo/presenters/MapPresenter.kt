package k.s.yarlykov.travelmeteo.presenters

import android.graphics.Bitmap
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetBehavior
import io.reactivex.disposables.CompositeDisposable
import k.s.yarlykov.travelmeteo.data.domain.CustomForecastModel
import k.s.yarlykov.travelmeteo.data.sources.openweather.model.current.WeatherResponseModel
import k.s.yarlykov.travelmeteo.data.sources.unifiedprovider.ForecastConsumer
import k.s.yarlykov.travelmeteo.data.sources.unifiedprovider.WeatherProviderRx
import k.s.yarlykov.travelmeteo.ui.MapActivity

class MapPresenter(var mapView: IMapView, private val wp: WeatherProviderRx) : IMapPresenter, ForecastConsumer {

    private var isMapReady = false
    private val compositeDisposable = CompositeDisposable()
    private val forecastStream = wp.hourlyForecastStream()

    //region Activity Life Cycle

    // Вызывается, если приложению предоставлены разрешения ACCESS_X_LOCATION
    override fun onCreate() {
        MapActivity.logIt("MapPresenter: onCreate()")

        // Загрузить макет
        mapView.initViews()
        // Загрузить карту
        mapView.loadMap()
    }

    override fun onResume() {
        MapActivity.logIt("MapPresenter: onResume(). isMapScreenReady = ${isMapReady}")

        // Подписаться на получение данных из модели.
        // Это нужно делать либо после mapView.initViews() либо здесь, потому что данные о прогнозе
        // могут прилететь раньше, чем загрузится макет. Например, при повороте экрана.
        compositeDisposable.add(
            forecastStream.subscribe { model ->
                mapView.updateForecastData(model)
            }
        )
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
        mapView.initMap()
    }

    override fun onMapClick(latLng: LatLng) {
        mapView.setBottomSheetState(BottomSheetBehavior.STATE_COLLAPSED)
    }

    override fun onMapLongClick(latLng: LatLng) {
        MapActivity.logIt("MapPresenter: onMapLongClick()")
        wp.requestForecastHourly(latLng)
    }

    override fun onSavedDataPresent(model: CustomForecastModel?) {
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

