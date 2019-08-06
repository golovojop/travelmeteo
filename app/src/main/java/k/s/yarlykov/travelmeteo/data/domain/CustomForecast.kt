package k.s.yarlykov.travelmeteo.data.domain

import com.google.android.gms.maps.model.LatLng
import k.s.yarlykov.travelmeteo.data.sources.openweather.model.hourly.Forecast
import java.lang.StringBuilder

/**
 * Единый формат для прогноза из разных источников
 */
data class CustomForecast(
    val time: String,
    val temp: Int,
    val temp_min: Int,
    val temp_max: Int,
    val wind_speed: Float,
    val wind_direction: String,
    val humidity: Int,
    val pressure: Int,
    val weather_main: String,
    val weather_descr: String,
    val icon: Int
)

fun CustomForecast.celsius(t: Int): String {
    val sb = StringBuilder()
    sb.append(if(t < 0) "-" else "+")
    sb.append("$t")
    sb.append("\u00B0")

    return sb.toString()
}