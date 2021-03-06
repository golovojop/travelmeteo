package k.s.yarlykov.travelmeteo.data.sources.openweather.model.hourly

import android.content.Context
import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import k.s.yarlykov.travelmeteo.R
import k.s.yarlykov.travelmeteo.data.domain.CustomForecast
import k.s.yarlykov.travelmeteo.data.domain.CustomForecastModel
import k.s.yarlykov.travelmeteo.data.domain.Season
import k.s.yarlykov.travelmeteo.extensions.formatHHmm
import k.s.yarlykov.travelmeteo.extensions.partOfDay
import k.s.yarlykov.travelmeteo.extensions.season
import java.util.*

class OpenWeatherHourlyResponseModel {

    @SerializedName("cod")
    @Expose
    lateinit var cod: String
    @SerializedName("message")
    @Expose
    var message: Float = 0.toFloat()
    @SerializedName("cnt")
    @Expose
    var cnt: Long = 0
    @SerializedName("list")
    @Expose
    lateinit var list: List<Forecast>
    @SerializedName("city")
    @Expose
    lateinit var city: City
}

fun OpenWeatherHourlyResponseModel.mapModel(): CustomForecastModel {

    val currentMillis = System.currentTimeMillis()

    return CustomForecastModel(
        this.city.name,
        this.city.country,
        LatLng(this.city.coord.lat.toDouble(), this.city.coord.lon.toDouble()),
        Date(currentMillis).season(),
        Date(currentMillis).partOfDay(this.city.timezone),
        this.list.map {
            CustomForecast(
                Date(it.dt * 1000).formatHHmm(this.city.timezone),
                Math.round(it.main.temp),
                Math.round(it.main.tempMin),
                Math.round(it.main.tempMax),
                it.wind.speed,
                it.wind.deg,
                it.main.humidity.toInt(),
                it.main.pressure,
                it.weather.get(0).main,
                it.weather.get(0).description,
                "ow${it.weather.get(0).icon}"
            )
        }
    )
}