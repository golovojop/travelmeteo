package k.s.yarlykov.travelmeteo.data.sources.unifiedprovider

import android.graphics.Bitmap
import k.s.yarlykov.travelmeteo.data.domain.CustomForecastModel
import k.s.yarlykov.travelmeteo.data.sources.openweather.model.current.WeatherResponseModel

interface ForecastConsumer {
    fun onForecastCurrent(model: WeatherResponseModel, icon: Bitmap)
    fun onForecastHourly(model: CustomForecastModel)
}