package k.s.yarlykov.travelmeteo.data.sources.openweather.model


import com.google.gson.annotations.SerializedName

class SysModel {
    @SerializedName("type")
    var type: Int = 0
    @SerializedName("id")
    var id: Int = 0
    @SerializedName("message")
    var message: Float = 0f
    @SerializedName("country")
    var country: String? = null
    @SerializedName("sunrise")
    var sunrise: Long = 0
    @SerializedName("sunset")
    var sunset: Long = 0
}