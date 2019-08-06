package k.s.yarlykov.travelmeteo.extensions

import com.google.android.gms.maps.model.LatLng
import k.s.yarlykov.travelmeteo.data.domain.CustomForecast
import k.s.yarlykov.travelmeteo.data.sources.openweather.model.hourly.Forecast
import java.text.SimpleDateFormat
import java.util.*

/**
 * MutableList
 */

fun MutableList<CustomForecast>.initFromModel(li: List<CustomForecast>) {
    clear()
    this.addAll(li)
}

/**
 * Date
 */

fun Date.format(format: String) = SimpleDateFormat(format, Locale.getDefault()).format(this)

/**
 * LatLng
 */
fun LatLng.lat() : String {
    return String.format("%.4f", this.latitude)
}

fun LatLng.lon() : String {
    return String.format("%.4f", this.longitude)
}



