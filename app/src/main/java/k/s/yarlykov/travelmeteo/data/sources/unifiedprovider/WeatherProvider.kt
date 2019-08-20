package k.s.yarlykov.travelmeteo.data.sources.unifiedprovider

import com.google.android.gms.maps.model.LatLng
import k.s.yarlykov.travelmeteo.data.sources.openweather.api.OpenWeatherProvider
import k.s.yarlykov.travelmeteo.extensions.lat
import k.s.yarlykov.travelmeteo.extensions.lon

interface WeatherProvider {
    fun requestForecastHourly(consumer: ForecastConsumer?, coord: LatLng)
    fun requestForecastCurrent(consumer: ForecastConsumer?, coord: LatLng)
}

val weatherProvider = object : WeatherProvider {

    // Текущий источник данных: openweathermap.org
    val modelSource: ForecastProducer = OpenWeatherProvider

    override fun requestForecastHourly(consumer: ForecastConsumer?, coord: LatLng) {
        modelSource.requestForecastHourly(consumer, coord.lat(), coord.lon())
    }

    override fun requestForecastCurrent(consumer: ForecastConsumer?, coord: LatLng) {
        modelSource.requestForecastCurrent(consumer, coord.lat(), coord.lon())
    }
}