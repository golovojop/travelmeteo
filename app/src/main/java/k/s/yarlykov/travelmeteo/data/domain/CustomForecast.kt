package k.s.yarlykov.travelmeteo.data.domain

import com.google.android.gms.maps.model.LatLng

/**
 * Единый формат для прогноза из разных источников
 */
data class CustomForecast(
    val time: String,
    val city: String,
    val country: String,
    val coord: LatLng,
    val temp: Int,
    val temp_min: Int,
    val temp_max: Int,
    val wind_speed: Float,
    val wind_degree: String,
    val humidity: Int,
    val pressure: Int,
    val weather_main: String,
    val weather_descr: String,
    val icon: Int
)