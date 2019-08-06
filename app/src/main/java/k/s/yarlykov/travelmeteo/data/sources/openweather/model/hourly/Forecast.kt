package k.s.yarlykov.travelmeteo.data.sources.openweather.model.hourly

import android.content.Context
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import k.s.yarlykov.travelmeteo.data.domain.CustomForecast
import k.s.yarlykov.travelmeteo.extensions.format
import java.lang.StringBuilder
import java.util.*

class Forecast {

    @SerializedName("dt")
    @Expose
    var dt: Long = 0
    @SerializedName("main")
    @Expose
    lateinit var main: Main
    @SerializedName("weather")
    @Expose
    lateinit var weather: List<Weather>
    @SerializedName("clouds")
    @Expose
    lateinit var clouds: Clouds
    @SerializedName("wind")
    @Expose
    lateinit var wind: Wind
    @SerializedName("sys")
    @Expose
    lateinit var sys: Sys
    @SerializedName("dt_txt")
    @Expose
    lateinit var dtTxt: String
    @SerializedName("rain")
    @Expose
    lateinit var rain: Rain
}

// Конвертировать имя иконки в ID ресурса картинки
fun Forecast.iconId(context: Context): Int {
    val icon = "ow${weather[0].icon}"
    val resources = context.resources
    return resources.getIdentifier(icon, "drawable",
        context.packageName)
}

fun Forecast.celsius(): String {
    val sb = StringBuilder()
    val t = Math.round(this.main.temp)

    sb.append(if(t < 0) "-" else "+")
    sb.append("$t")
    sb.append("\u00B0")

    return sb.toString()
}

// Расширение для конвертации прогноза в базовый формат приложения
fun Forecast.mapModel(context: Context) : CustomForecast {
    return CustomForecast(
        Date(this.dt * 1000).format("HH:mm"),
        Math.round(this.main.temp),
        Math.round(this.main.tempMin),
        Math.round(this.main.tempMax),
        this.wind.speed,
        this.wind.direction(),
        this.main.humidity.toInt(),
        this.main.pressure.toInt(),
        this.weather.get(0).main,
        this.weather.get(0).description,
        this.iconId(context)
    )
}