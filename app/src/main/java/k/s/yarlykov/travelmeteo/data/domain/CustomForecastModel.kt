/**
 * Materials:
 * https://proandroiddev.com/parcelable-in-kotlin-here-comes-parcelize-b998d5a5fcac
 */
package k.s.yarlykov.travelmeteo.data.domain

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CustomForecastModel(
    val city: String,
    val country: String,
    val coord: LatLng,
    val season: Season,
    val dayPart: DayPart,
    val list: List<CustomForecast>
) : Parcelable