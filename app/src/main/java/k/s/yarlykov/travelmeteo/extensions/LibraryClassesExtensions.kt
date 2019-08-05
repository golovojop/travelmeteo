package k.s.yarlykov.travelmeteo.extensions

import k.s.yarlykov.travelmeteo.data.sources.openweather.model.hourly.Forecast
import java.text.SimpleDateFormat
import java.util.*

fun MutableList<Forecast>.initFromModel(li: List<Forecast>) {
    clear()
    this.addAll(li)

//    for((index, f) in li.withIndex()) {
//        this.add(f)
//    }
}


fun Date.format(format: String) = SimpleDateFormat(format, Locale.getDefault()).format(this)




