package k.s.yarlykov.travelmeteo.application

import android.app.Application
import k.s.yarlykov.travelmeteo.di.AppExtensions
import k.s.yarlykov.travelmeteo.di.AppExtensionProvider

class TravelMeteoApp: Application() , AppExtensionProvider {

    val applicationExtension = AppExtensions()

    override fun onCreate() {
        super.onCreate()
    }

    override fun provideAppExtension() : AppExtensions {
        return applicationExtension
    }
}