/**
 * Materials:
 * Используем BehaviorSubject как источник данных. Он хранит последний эмиттированный элемент,
 * что позволит нам получать последний прогноз погоды после пересоздания MapActivity.
 */

package k.s.yarlykov.travelmeteo.data.sources.openweather.api

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import k.s.yarlykov.travelmeteo.data.domain.CustomForecastModel
import k.s.yarlykov.travelmeteo.data.sources.openweather.model.hourly.OpenWeatherHourlyResponseModel
import k.s.yarlykov.travelmeteo.data.sources.openweather.model.hourly.mapModel
import k.s.yarlykov.travelmeteo.data.sources.unifiedprovider.ForecastProducerRx
import k.s.yarlykov.travelmeteo.ui.MapActivity
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Принцип работы:
 * Принимаем запросы через Observable "val requestHolder: BehaviorSubject<Pair<String, String>>"
 * Отправляем ответы через Observable "val forecastPublisher: BehaviorSubject<CustomForecastModel>"
 */

object OpenWeatherMapProviderRx : ForecastProducerRx {

    private const val apiKey = "b7838252ab3cde579f376d9417c878d1"
    private const val apiUnits = "metric"
    private const val baseUrl = "https://api.openweathermap.org/"

    private val api: OpenWeatherRx = createAdapter()
    // Observable для приема запросов от пользователя и их ретрансляции в обработчик.
    private val requestHolder: BehaviorSubject<Pair<String, String>> = BehaviorSubject.create()
    // Observable для рассылки прогнозов клиентам
    private val forecastPublisher: BehaviorSubject<CustomForecastModel> = BehaviorSubject.create()
    // Сюда складываем подписки на запросы к Retrofit. После очередного ответа отписываемся от всего.
    // Хотя и предполагается, что после запроса следует ответ именно на него, но
    private val retrofitSubscriptions = CompositeDisposable()

    init {
        // Подписываемся на пустой BehaviorSubject.
        // Запросы в него будет складывать метод requestForecastHourlyRx.
        requestHolder.subscribe(::rawRequestHandler)
    }

    // Принять запрос и смапить его в Rx-формат
    override fun onRequestForecastHourlyRx(lat: String, lon: String) {
        MapActivity.logIt("OpenWeatherMapProviderRx: requestForecastHourlyRx()")

        // Сконвертировать клиентский запрос в Rx-event
        requestHolder.onNext(Pair(lat, lon))
    }

    // Вернуть Subject как Observable
    override fun hourlyForecastStream() = forecastPublisher.hide()

    // Отправить запрос на сервер и подписаться на ответ
    private fun rawRequestHandler(coords: Pair<String, String>) {
        retrofitSubscriptions.add(
            api.loadGeoWeatherHourly(coords.first, coords.second, apiUnits, apiKey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(::restResponseHandler)
        )
    }

    // Прогноз прилетает из Observable с сервера и эмиттируется дальше подписчикам
    private fun restResponseHandler(model: OpenWeatherHourlyResponseModel) {
        retrofitSubscriptions.clear()
        forecastPublisher.onNext(model.mapModel())
    }

    private fun createAdapter(): OpenWeatherRx {
        // Установить таймауты
        val okHttpClient = OkHttpClient().newBuilder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()

        val adapter = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

        return adapter.create(OpenWeatherRx::class.java)
    }
}
