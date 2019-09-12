package k.s.yarlykov.travelmeteo.data.sources.openweather.api

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import k.s.yarlykov.travelmeteo.data.domain.CustomForecastModel
import k.s.yarlykov.travelmeteo.data.sources.openweather.model.hourly.OpenWeatherHourlyResponseModel
import k.s.yarlykov.travelmeteo.data.sources.openweather.model.hourly.mapModel
import k.s.yarlykov.travelmeteo.data.sources.unifiedprovider.ForecastProducerRx
import k.s.yarlykov.travelmeteo.ui.MapActivity
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object OpenWeatherMapProviderRx : ForecastProducerRx {

    private const val apiKey = "b7838252ab3cde579f376d9417c878d1"
    private const val apiUnits = "metric"
    private const val baseUrl = "https://api.openweathermap.org/"

    private val api: OpenWeatherRx = createAdapter()
    private val requestHolder: BehaviorSubject<Pair<String, String>> = BehaviorSubject.create()
    private val responsePublisher: PublishSubject<CustomForecastModel> = PublishSubject.create()

    init {
        val disposable = requestHolder
            .subscribe(::requestHandler)
    }

    // Принять запрос и смапить его в Rx-формат
    override fun requestForecastHourlyRx(lat: String, lon: String) {
        MapActivity.logIt("OpenWeatherMapProviderRx: requestForecastHourlyRx()")

        requestHolder.onNext(Pair(lat, lon))
    }

    // Вернуть Subject как Observable
    override fun getForecastPublisher() = responsePublisher.hide()

    // Отправить запрос на сервер и подписаться на ответ
    private fun requestHandler(coords: Pair<String, String>) {
        val d = api.loadGeoWeatherHourly(coords.first, coords.second, apiUnits, apiKey)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(::responseHandler)
    }

    // Принять ответ от сервера и сделать эмиссию в Subject
    private fun responseHandler(model: OpenWeatherHourlyResponseModel) {
        responsePublisher.onNext(model.mapModel())
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
