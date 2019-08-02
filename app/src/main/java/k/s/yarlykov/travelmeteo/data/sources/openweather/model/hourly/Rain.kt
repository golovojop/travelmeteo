package k.s.yarlykov.travelmeteo.data.sources.openweather.model.hourly

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Rain {

    @SerializedName("1h")
    @Expose
    var _1h: Float = 0.toFloat()

}