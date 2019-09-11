package k.s.yarlykov.travelmeteo.extensions

import k.s.yarlykov.travelmeteo.data.domain.DayPart
import k.s.yarlykov.travelmeteo.data.domain.Season
import java.text.SimpleDateFormat
import java.util.*

/**
 * Date
 *
 * Materials:
 * https://kodejava.org/how-do-i-get-timezone-ids-by-their-milliseconds-offset/
 * http://tutorials.jenkov.com/java-date-time/java-util-timezone.html
 * https://stackoverflow.com/questions/7670355/convert-date-time-for-given-timezone-java
 */
fun Date.format(format: String, offset: Int): String {
    // Получить ID Time-зоны для данного offset'а
    val zoneId = TimeZone.getAvailableIDs(offset * 1000).asList()[0]
    val timeZone = TimeZone.getTimeZone(zoneId)

    val sdf = SimpleDateFormat(format, Locale.getDefault())
    sdf.timeZone = timeZone

    return sdf.format(this)
}

// Тот же код, но в более извращенной форме )
fun Date.formatHHmm(offset: Int) = SimpleDateFormat("HH:mm", Locale.getDefault()).apply {
    val zoneId = TimeZone.getAvailableIDs(offset * 1000).asList()[0]
    this.timeZone = TimeZone.getTimeZone(zoneId)
}.format(this)

// Часть дня
fun Date.partOfDay(offset: Int) : DayPart {
    val hour = SimpleDateFormat("HH", Locale.getDefault()).apply {
        val zoneId = TimeZone.getAvailableIDs(offset * 1000).asList()[0]
        this.timeZone = TimeZone.getTimeZone(zoneId)
    }.format(this).toInt()

    return when(hour) {
        in 6..7 -> DayPart.SUNRISE
        in 7..22 -> DayPart.DAY
        else -> DayPart.NIGHT
    }
}

// Определить текущее время года
fun Date.season(): Season {

    val month = Calendar.getInstance().also {
        it.setTime(this)
    }.get(Calendar.MONTH)

    return when(month) {
        in 3..5 ->  Season.SPRING
        in 6..8 -> Season.SUMMER
        in 9..11 -> Season.AUTUMN
        else -> Season.WINTER
    }
}
