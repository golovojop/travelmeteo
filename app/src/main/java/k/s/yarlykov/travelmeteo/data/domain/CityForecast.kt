package k.s.yarlykov.travelmeteo.data.domain

import java.io.Serializable

data class CityForecast(val city: String,
                        val country: String,
                        val temperature: Int,
                        val icon: String,
                        val wind: Float,
                        val humidity: Int,
                        val pressure: Int,
                        val id: Int = 0) : Serializable {

    fun mmToMb(mm: Int) = (mm * 1.333f).toInt()
    fun mbToMm(mb: Int) = (mb * 0.75006f).toInt()
    fun getPressure(isMm: Boolean) = if (isMm) pressure else mmToMb(pressure)
}