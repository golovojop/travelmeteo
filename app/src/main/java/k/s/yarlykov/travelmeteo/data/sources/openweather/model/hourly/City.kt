package k.s.yarlykov.travelmeteo.data.sources.openweather.model.hourly

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class City {
    @SerializedName("id")
    @Expose
    var id: Long = 0
    @SerializedName("name")
    @Expose
    lateinit var name: String
    @SerializedName("coord")
    @Expose
    lateinit var coord: Coord
    @SerializedName("country")
    @Expose
    lateinit var country: String
}