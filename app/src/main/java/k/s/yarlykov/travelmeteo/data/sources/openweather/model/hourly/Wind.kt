package k.s.yarlykov.travelmeteo.data.sources.openweather.model.hourly

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Wind {

    @SerializedName("speed")
    @Expose
    var speed: Float = 0.toFloat()
    @SerializedName("deg")
    @Expose
    var deg: Float = 0.toFloat()

}