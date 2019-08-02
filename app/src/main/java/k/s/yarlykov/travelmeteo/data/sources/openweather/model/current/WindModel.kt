package k.s.yarlykov.travelmeteo.data.sources.openweather.model.current


import com.google.gson.annotations.SerializedName

class WindModel {
    @SerializedName("speed")
    var speed: Float = 0f
    @SerializedName("deg")
    var deg: Float = 0f
}