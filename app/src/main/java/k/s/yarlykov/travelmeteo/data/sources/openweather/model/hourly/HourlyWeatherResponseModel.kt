package k.s.yarlykov.travelmeteo.data.sources.openweather.model.hourly

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class HourlyWeatherResponseModel {

    @SerializedName("cod")
    @Expose
    var cod: String? = null
    @SerializedName("message")
    @Expose
    var message: Float = 0.toFloat()
    @SerializedName("cnt")
    @Expose
    var cnt: Long = 0
    @SerializedName("list")
    @Expose
    var list: List<Forecast>? = null
    @SerializedName("city")
    @Expose
    var city: City? = null

}