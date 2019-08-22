package k.s.yarlykov.travelmeteo.data.sources.openweather.model.hourly

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Coord {

    @SerializedName("lat")
    @Expose
    var lat: Float = 0.toFloat()
    @SerializedName("lon")
    @Expose
    var lon: Float = 0.toFloat()

}