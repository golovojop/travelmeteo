package k.s.yarlykov.travelmeteo.extensions

import com.google.android.gms.maps.model.Marker
import k.s.yarlykov.travelmeteo.data.domain.CustomForecast

/**
 * MutableList <CustomForecast>
 */
fun MutableList<CustomForecast>.initFromModel(li: List<CustomForecast>) {
    // Из исходного массива возьмём не более QTY элементов
    val QTY = 12
    this.clear()

    for ((index, value) in li.withIndex()) {
        if (index < QTY) {
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