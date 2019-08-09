package k.s.yarlykov.travelmeteo.data.domain

import k.s.yarlykov.travelmeteo.R

/**
 * Единый формат для прогноза из разных источников
 */
data class CustomForecast(
    val time: String,
    val temp: Int,
    val temp_min: Int,
    val temp_max: Int,
    val wind_speed: Float,
    val wind_direction_sz: String,
    val wind_direction_degree: Float,
    val humidity: Int,
    val pressure: Float,
    val weather_main: String,
    val weather_descr: String,
    val icon: Int
) {
    companion object {
        fun empty() = CustomForecast(
            "",
            1,
            1,
            1,
            0F,
            "",
            0F,
            1,
            0F,
            "",
            "",
            R.drawable.ovc_flat
        )
    }
}

// Представить температуру в виде строки с префиксом "+" или "-" и знаком градуса после значения
fun CustomForecast.celsius(t: Int): String {
    val sb = StringBuilder()
    sb.append(if (t < 0) "-" else "+")
    sb.append("$t")
    sb.append("\u00B0")

    return sb.toString()
}

fun CustomForecast.toMmHg(): Int = (this.pressure * 0.75006f).toInt()
fun CustomForecast.toMb(): Int = this.pressure.toInt()
