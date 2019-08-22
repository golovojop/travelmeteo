package k.s.yarlykov.travelmeteo.data.sources.openweather.model.current

import com.google.gson.annotations.SerializedName
import k.s.yarlykov.travelmeteo.data.domain.CityForecast

class WeatherResponseModel {
    @SerializedName("coord")
    var coordinates: CoordModel? = null
    @SerializedName("weather")
    var weather: Array<WeatherModel>? = null
    @SerializedName("base")
    var base: String? = null
    @SerializedName("main")
    var main: MainModel? = null
    @SerializedName("visibility")
    var visibility: Int = 0
    @SerializedName("wind")
    var wind: WindModel? = null
    @SerializedName("clouds")
    var clouds: CloudsModel? = null
    @SerializedName("dt")
    var dt: Long = 0
    @SerializedName("sys")
    var sys: SysModel? = null
    @SerializedName("id")
    var id: Long = 0
    @SerializedName("name")
    var name: String? = null
    @SerializedName("cod")
    var cod: Int = 0
}

fun WeatherResponseModel.mapToCityForecast(): CityForecast {
    return CityForecast(
        this.name!!,
        this.sys!!.country!!,
        this.main!!.temp.toInt(),
        this.weather!![0].icon!!,
        this.wind!!.speed,
        this.main!!.humidity,
        this.main!!.pressure
    )
}