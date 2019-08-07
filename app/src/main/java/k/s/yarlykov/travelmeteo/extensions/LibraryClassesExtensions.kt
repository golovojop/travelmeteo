package k.s.yarlykov.travelmeteo.extensions

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import k.s.yarlykov.travelmeteo.data.domain.CustomForecast
import k.s.yarlykov.travelmeteo.data.sources.openweather.model.hourly.Forecast
import java.text.SimpleDateFormat
import java.util.*

/**
 * MutableList <CustomForecast>
 */
fun MutableList<CustomForecast>.initFromModel(li: List<CustomForecast>) {
    // Из исходного массива возьмём не более QTY элементов
    val QTY = 12
    this.clear()

    for((index, value) in li.withIndex()) {
        if(index < QTY) {
            this.add(index, value)
        } else {
            break
        }
    }
}

/**
 * MutableList <Marker>
 */
fun MutableList<Marker>.deleteAll() {
    this.forEach(Marker::remove)
    this.clear()
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



