package k.s.yarlykov.travelmeteo.data.sources.openweather.model.hourly

import android.content.Context
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

fun Wind.direction(directions: List<String>): String {
    val step = 22.5

    var n = Math.floor(this.deg/step).toInt()
    if(n >= 15) n = 0
    if(n % 2 > 0) n++
    return directions.get(n/2)
}