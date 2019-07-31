package k.s.yarlykov.travelmeteo.data.sources.openweather.model

import com.google.gson.annotations.SerializedName

class CoordModel {
    @SerializedName("lon")
    var lon: Float = 0f
    @SerializedName("lat")
    var lat: Float = 0f
}