package k.s.yarlykov.travelmeteo.data.domain

import k.s.yarlykov.travelmeteo.R
import java.io.Serializable

/**
 * Единый формат для прогноза из разных источников
 */
data class CustomForecast(
    val time: String = "",
    val temp: Int = 0,
    val temp_min: Int = 0,
    val temp_max: Int = 0,
    val wind_speed: Float = 0F,
    val wind_direction_sz: String = "",
    val wind_direction_degree: Float = 0F,
    val humidity: Int = 0,
    val pressure: Float = 0F,
    val weather_main: String = "",
    val weather_descr: String = "",
    val icon: Int = R.drawable.ovc_flat
) : Serializable

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
