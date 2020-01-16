package k.s.yarlykov.travelmeteo.di

import android.content.Context
import k.s.yarlykov.travelmeteo.R
import k.s.yarlykov.travelmeteo.data.domain.Season
import k.s.yarlykov.travelmeteo.data.sources.openweather.api.OpenWeatherMapProvider
import k.s.yarlykov.travelmeteo.data.sources.openweather.api.OpenWeatherMapProviderRx
import k.s.yarlykov.travelmeteo.data.sources.unifiedprovider.AppWeatherProvider
import k.s.yarlykov.travelmeteo.data.sources.unifiedprovider.AppWeatherProviderRx

// Implemented by TravelMeteoApp
interface AppExtensionProvider {
    fun provideAppExtension() : AppExtensions
}

class AppExtensions(val context: Context) {

    private val backgroundPics : HashMap<Season, List<Int>>

    /**
     * Для каждого времени года свой набор фоновых картинок загружаем в отдельный List.
     * Полученные List складываем в HashMap. Ключём будет время года как enum Season
     */
    init {
        backgroundPics = HashMap()

        // Получить массив массивов: массив, содержащий ID других массивов
        with(context.resources.obtainTypedArray(R.array.seasons)) {
            val seasons = enumValues<Season>()

            // Проход по массивам картинок. 4 массива по временам года
            for (i in 0 until this.length()) {
                resources.obtainTypedArray(this.getResourceId(i, 0)).also {
                    val picIds: MutableList<Int> = mutableListOf()

                    for (j in 0 until it.length()) {
                        picIds.add(it.getResourceId(j, 0))
                    }

                    backgroundPics[seasons[i]] = picIds
                    it.recycle()
                }
            }
            this.recycle()
        }
    }

    // Вернуть поставщика метео данных
    fun getWeatherProvider() = AppWeatherProvider(OpenWeatherMapProvider)

    // Вернуть поставщика метео данных
    fun getWeatherProviderRx() = AppWeatherProviderRx(OpenWeatherMapProviderRx)

    // Вернуть картинки фона для сезона года season
    fun getSeasonBackground(season: Season) : List<Int> = backgroundPics[season]!!
}