package k.s.yarlykov.travelmeteo.data.sources.openweather.model.hourly

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Main {

    @SerializedName("temp")
    @Expose
    var temp: Float = 0.toFloat()
    @SerializedName("temp_min")
    @Expose
    var tempMin: Float = 0.toFloat()
    @SerializedName("temp_max")
    @Expose
    var tempMax: Float = 0.toFloat()
    @SerializedName("pressure")
    @Expose
    var pressure: Float = 0.toFloat()
    @SerializedName("sea_level")
    @Expose
    var seaLevel: Float = 0.toFloat()
    @SerializedName("grnd_level")
    @Expose
    var grndLevel: Float = 0.toFloat()
    @SerializedName("humidity")
    @Expose
    var humidity: Long = 0
    @SerializedName("temp_kf")
    @Expose
    var tempKf: Float = 0.toFloat()
}