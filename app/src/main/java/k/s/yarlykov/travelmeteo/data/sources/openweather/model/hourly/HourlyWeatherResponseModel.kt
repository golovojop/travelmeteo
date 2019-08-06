package k.s.yarlykov.travelmeteo.data.sources.openweather.model.hourly

import android.content.Context
import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import k.s.yarlykov.travelmeteo.data.domain.CustomForecast
import k.s.yarlykov.travelmeteo.data.domain.CustomForecastModel
import k.s.yarlykov.travelmeteo.extensions.format
import java.util.*

class HourlyWeatherResponseModel {

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

fun HourlyWeatherResponseModel.mapModel(context: Context): CustomForecastModel {

    return CustomForecastModel(
        this.city.name,
        this.city.country,
        LatLng(this.city.coord.lat.toDouble(), this.city.coord.lon.toDouble()),
        this.list.map {
            CustomForecast(
                Date(it.dt * 1000).format("HH:mm"),
                Math.round(it.main.temp),
                Math.round(it.main.tempMin),
                Math.round(it.main.tempMax),
                it.wind.speed,
                it.wind.direction(),
                it.main.humidity.toInt(),
                it.main.pressure.toInt(),
                it.weather.get(0).main,
                it.weather.get(0).description,
                it.iconId(context)
            )
        }
    )
}