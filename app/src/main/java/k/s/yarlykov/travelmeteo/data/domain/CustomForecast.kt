package k.s.yarlykov.travelmeteo.data.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Единый формат для прогноза из разных источников
 */
@Parcelize
data class CustomForecast(
    val time: String = "",
    val temp: Int = 0,
    val temp_min: Int = 0,
    val temp_max: Int = 0,
    val wind_speed: Float = 0F,
    val wind_deg: Float = 0F,
    val humidity: Int = 0,
    val pressure: Float = 0F,
    val weather_main: String = "",
    val weather_descr: String = "",
    val icon: String = ""
) : Parcelable

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

/**
 * Курс направления ветра (в градусах) сконвертировать в индекс направления ветра.
 * Программа работает с набором из 8 направлений (N,S,W,E,NW,NE,SW,SE).
 */
fun CustomForecast.windDegToId(): Int {
    val step = 22.5

    var n = Math.floor(this.wind_deg / step).toInt()
    if (n >= 15) n = 0
    if (n % 2 > 0) n++
    return n / 2
}
