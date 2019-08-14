package k.s.yarlykov.travelmeteo.data.domain

import com.google.android.gms.maps.model.LatLng
import java.io.Serializable

data class CustomForecastModel(
    val city: String,
    val country: String,
    val coord: LatLng,
    val list: List<CustomForecast>
) : Serializable