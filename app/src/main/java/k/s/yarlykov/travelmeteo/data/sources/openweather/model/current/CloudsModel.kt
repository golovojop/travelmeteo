package k.s.yarlykov.travelmeteo.data.sources.openweather.model.current

import com.google.gson.annotations.SerializedName

class CloudsModel {
    @SerializedName("all")
    var all: Int = 0
}