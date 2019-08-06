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
    this.clear()
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
    // Для того, чтобы в результирующей строке в качестве разделителя
    // целой и дробной части была точка, а не запятая, используется
    // аргумент Locale.ROOT.
    // На телефоне с русской локалью как раз используется запятая
    // и из-за этого неправильно формировались координаты для GET
    // запроса и прога глючила.
    return String.format(Locale.ROOT,"%.6f", this.latitude)
}

fun LatLng.lon() : String {
    // Тоже самое ->> Locale.ROOT
    return String.format(Locale.ROOT, "%.6f", this.longitude)
}



