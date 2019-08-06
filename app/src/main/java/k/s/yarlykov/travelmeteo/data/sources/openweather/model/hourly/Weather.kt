package k.s.yarlykov.travelmeteo.data.sources.openweather.model.hourly

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Weather {

    @SerializedName("id")
    @Expose
    var id: Long = 0
    @SerializedName("main")
    @Expose
    lateinit var main: String
    @SerializedName("description")
    @Expose
    lateinit var description: String
    @SerializedName("icon")
    @Expose
    lateinit var icon: String
}