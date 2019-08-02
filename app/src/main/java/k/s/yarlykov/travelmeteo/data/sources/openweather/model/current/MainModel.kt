package k.s.yarlykov.travelmeteo.data.sources.openweather.model.current

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MainModel {
    @SerializedName("temp")
    @Expose
    var temp: Float = 0f

    @SerializedName("pressure")
    @Expose
    var pressure: Int = 0

    @SerializedName("humidity")
    @Expose
    var humidity: Int = 0

    @SerializedName("temp_min")
    var tempMin: Float = 0f

    @SerializedName("temp_max")
    var tempMax: Float = 0f
}