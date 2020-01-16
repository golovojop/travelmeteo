package k.s.yarlykov.travelmeteo.extensions

import com.google.android.gms.maps.model.LatLng
import java.util.*

/**
 * LatLng
 */
fun LatLng.lat(): String {
    // Для того, чтобы в результирующей строке в качестве разделителя
    // целой и дробной части была точка, а не запятая, используется
    // аргумент Locale.ROOT.
    // На телефоне с русской локалью как раз используется запятая
    // и из-за этого неправильно формировались координаты для GET
    // запроса и прога глючила.
    return String.format(Locale.ROOT, "%.6f", this.latitude)
}

fun LatLng.lon(): String {
    // Тоже самое ->> Locale.ROOT
    return String.format(Locale.ROOT, "%.6f", this.longitude)
}