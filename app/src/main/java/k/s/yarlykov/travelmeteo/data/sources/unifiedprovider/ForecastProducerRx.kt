package k.s.yarlykov.travelmeteo.data.sources.unifiedprovider

import io.reactivex.Observable
import k.s.yarlykov.travelmeteo.data.domain.CustomForecastModel

interface ForecastProducerRx {
    fun onRequestForecastHourlyRx(lat: String, lon: String)
    fun hourlyForecastStream() : Observable<CustomForecastModel>
}