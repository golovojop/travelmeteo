package k.s.yarlykov.travelmeteo.data.sources.unifiedprovider

import com.google.android.gms.maps.model.LatLng
import k.s.yarlykov.travelmeteo.extensions.lat
import k.s.yarlykov.travelmeteo.extensions.lon

interface WeatherProvider {
    fun requestForecastHourly(consumer: ForecastConsumer?, coord: LatLng)
    fun requestForecastCurrent(consumer: ForecastConsumer?, coord: LatLng)
}

data class AppWeatherProvider(val forecastProducer: ForecastProducer) : WeatherProvider {
    override fun requestForecastHourly(consumer: ForecastConsumer?, coord: LatLng) {
        forecastProducer.requestForecastHourly(consumer, coord.lat(), coord.lon())
    }
    override fun requestForecastCurrent(consumer: ForecastConsumer?, coord: LatLng) {
        forecastProducer.requestForecastCurrent(consumer, coord.lat(), coord.lon())
    }
}

//val weatherProvider = object : WeatherProvider {
//
//    // Текущий источник данных: openweathermap.org
//    val modelSource: ForecastProducer = OpenWeatherMapProvider
//
//    override fun requestForecastHourly(consumer: ForecastConsumer?, coord: LatLng) {
//        modelSource.requestForecastHourly(consumer, coord.lat(), coord.lon())
//    }
//
//    override fun requestForecastCurrent(consumer: ForecastConsumer?, coord: LatLng) {
//        modelSource.requestForecastCurrent(consumer, coord.lat(), coord.lon())
//    }
//}