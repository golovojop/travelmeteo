package k.s.yarlykov.travelmeteo.data.sources.openweather.model.hourly

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Forecast {

    @SerializedName("dt")
    @Expose
    var dt: Long = 0
    @SerializedName("main")
    @Expose
    var main: Main? = null
    @SerializedName("weather")
    @Expose
    var weather: List<Weather>? = null
    @SerializedName("clouds")
    @Expose
    var clouds: Clouds? = null
    @SerializedName("wind")
    @Expose
    var wind: Wind? = null
    @SerializedName("sys")
    @Expose
    var sys: Sys? = null
    @SerializedName("dt_txt")
    @Expose
    var dtTxt: String? = null
    @SerializedName("rain")
    @Expose
    var rain: Rain? = null
}

fun Forecast.getDescription(): String {
    return """
        date: ${this.dtTxt}
        state: '${this.weather!!.get(0).main}: ${this.weather!!.get(0).description}'
        temp: ${Math.round(this.main!!.temp)}
        temp_min: ${Math.round(this.main!!.tempMin)}
        temp_max: ${Math.round(this.main!!.tempMax)}
        wind: ${this.wind!!.speed}
        pressure: ${this.main!!.pressure}
        humidity: ${this.main!!.humidity}
    """.trimIndent()
}