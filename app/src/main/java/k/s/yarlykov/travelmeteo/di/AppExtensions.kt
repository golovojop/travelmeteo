package k.s.yarlykov.travelmeteo.di

import k.s.yarlykov.travelmeteo.data.sources.openweather.api.OpenWeatherMapProvider
import k.s.yarlykov.travelmeteo.data.sources.unifiedprovider.AppWeatherProvider

// Implemented by TravelMeteoApp
interface AppExtensionProvider {
    fun provideAppExtension() : AppExtensions
}

class AppExtensions {
    fun getWeatherProvider() = AppWeatherProvider(OpenWeatherMapProvider)
}