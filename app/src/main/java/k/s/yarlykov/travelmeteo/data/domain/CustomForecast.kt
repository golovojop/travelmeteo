package k.s.yarlykov.travelmeteo.data.domain

/**
 * Единый формат для прогноза из разных источников
 */
data class CustomForecast(
    val time: String,
    val temp: Int,
    val temp_min: Int,
    val temp_max: Int,
    val wind_speed: Float,
    val wind_direction: String,
    val humidity: Int,
    val pressure: Float,
    val weather_main: String,
    val weather_descr: String,
    val icon: Int
)

// Представить температуру в виде строки с префиксом "+" или "-" и знаком градуса после значения
fun CustomForecast.celsius(t: Int): String {
    val sb = StringBuilder()
    sb.append(if(t < 0) "-" else "+")
    sb.append("$t")
    sb.append("\u00B0")

    return sb.toString()
}

fun CustomForecast.toMmHg(): Int = (this.pressure * 0.75006f).toInt()
fun CustomForecast.toMb(): Int = this.pressure.toInt()