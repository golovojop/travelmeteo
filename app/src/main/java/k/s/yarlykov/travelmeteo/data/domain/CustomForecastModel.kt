package k.s.yarlykov.travelmeteo.data.domain

import com.google.android.gms.maps.model.LatLng

data class CustomForecastModel(
    val city: String,
    val country: String,
    val coord: LatLng,
    val list: List<CustomForecast>
)