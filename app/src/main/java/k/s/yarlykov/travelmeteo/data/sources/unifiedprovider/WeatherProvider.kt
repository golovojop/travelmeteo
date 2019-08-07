package k.s.yarlykov.travelmeteo.data.sources.unifiedprovider

import com.google.android.gms.maps.model.LatLng
import k.s.yarlykov.travelmeteo.data.sources.openweather.api.OpenWeatherProvider
import k.s.yarlykov.travelmeteo.extensions.lat
import k.s.yarlykov.travelmeteo.extensions.lon

object WeatherProvider {

    // Текущий источник данных: openweathermap.org
    val provider: ForecastProvider = OpenWeatherProvider

    fun requestForecastHourly(consumer: ForecastConsumer?, coord: LatLng) {
        provider.requestForecastHourly(consumer, coord.lat(), coord.lon())
    }

    fun requestForecastCurrent(consumer: ForecastConsumer?, coord: LatLng) {
        provider.requestForecastCurrent(consumer, coord.lat(), coord.lon())
    }
}