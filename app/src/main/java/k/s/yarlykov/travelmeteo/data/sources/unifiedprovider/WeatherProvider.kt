package k.s.yarlykov.travelmeteo.data.sources.unifiedprovider

import com.google.android.gms.maps.model.LatLng
import io.reactivex.Observable
import k.s.yarlykov.travelmeteo.data.domain.CustomForecastModel
import k.s.yarlykov.travelmeteo.extensions.lat
import k.s.yarlykov.travelmeteo.extensions.lon
import k.s.yarlykov.travelmeteo.ui.MapActivity

interface WeatherProvider {
    fun requestForecastHourly(consumer: ForecastConsumer?, coord: LatLng)
    fun requestForecastCurrent(consumer: ForecastConsumer?, coord: LatLng)
}

interface WeatherProviderRx {
    fun requestForecastHourly(coord: LatLng)
    fun hourlyForecastStream() : Observable<CustomForecastModel>
}

data class AppWeatherProvider(val forecastProducer: ForecastProducer) : WeatherProvider {

    override fun requestForecastHourly(consumer: ForecastConsumer?, coord: LatLng) {
        forecastProducer.requestForecastHourly(consumer, coord.lat(), coord.lon())
    }
    override fun requestForecastCurrent(consumer: ForecastConsumer?, coord: LatLng) {
        forecastProducer.requestForecastCurrent(consumer, coord.lat(), coord.lon())
    }
}

data class AppWeatherProviderRx(val forecastProducer: ForecastProducerRx) : WeatherProviderRx {

    override fun requestForecastHourly(coord: LatLng) {
        MapActivity.logIt("AppWeatherProviderRx: requestForecastHourly()")
        forecastProducer.onRequestForecastHourlyRx(coord.lat(), coord.lon())
    }

    override fun hourlyForecastStream(): Observable<CustomForecastModel> {
        return forecastProducer.hourlyForecastStream()
    }
}