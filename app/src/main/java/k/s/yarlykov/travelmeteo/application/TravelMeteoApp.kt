package k.s.yarlykov.travelmeteo.application

import android.app.Application
import k.s.yarlykov.travelmeteo.di.AppExtensions
import k.s.yarlykov.travelmeteo.di.AppExtensionProvider

class TravelMeteoApp: Application() , AppExtensionProvider {

    lateinit var applicationExtension : AppExtensions

    override fun onCreate() {
        super.onCreate()
        applicationExtension = AppExtensions(this)
    }

    override fun provideAppExtension() : AppExtensions {
        return applicationExtension
    }
}