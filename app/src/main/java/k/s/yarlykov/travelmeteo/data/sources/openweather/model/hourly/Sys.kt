package k.s.yarlykov.travelmeteo.data.sources.openweather.model.hourly

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Sys {

    @SerializedName("pod")
    @Expose
    var pod: String? = null

}