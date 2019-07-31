package k.s.yarlykov.travelmeteo.data.sources.openweather.api

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import k.s.yarlykov.travelmeteo.data.domain.CityForecast
import k.s.yarlykov.travelmeteo.data.sources.openweather.model.WeatherResponseModel
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object OpenWeatherProvider {

    interface ForecastReceiver {
        fun onForecastOnline(forecastOnline: CityForecast, icon: Bitmap)
    }

    private const val apiKey = "b7838252ab3cde579f376d9417c878d1"
    private const val apiUnits = "metric"
    private const val baseUrl = "https://api.openweathermap.org/"

    private val api: OpenWeather = createAdapter()

    private fun createAdapter(): OpenWeather {
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
            .build()

        return adapter.create(OpenWeather::class.java)
    }

    fun requestForecast(receiver: ForecastReceiver?, lat: Int, lon: Int) {
        api.loadGeoWeather(lat, lon, apiUnits, apiKey).enqueue(object : Callback<WeatherResponseModel> {

            override fun onResponse(call: Call<WeatherResponseModel>, response: Response<WeatherResponseModel>) {

                // Code 404: body() == null; isSuccessful() == false
                if (response.isSuccessful()) {
                    response.body()?.let {
                        val forecast = CityForecast(
                            it.name!!,
                            it.sys!!.country!!,
                            it.main!!.temp.toInt(),
                            it.weather!![0].icon!!,
                            it.wind!!.speed,
                            it.main!!.humidity,
                            it.main!!.pressure
                        )

                        requestIcon(receiver, forecast)
                    }
                }
            }

            override fun onFailure(call: Call<WeatherResponseModel>, t: Throwable) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }

    // Получить иконку погоды
    private fun requestIcon(receiver: ForecastReceiver?, forecast: CityForecast) {

        val call = api.fetchIcon("https://openweathermap.org/img/w/${forecast.icon}.png")

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                if(response.isSuccessful) {
                    response.body()?.let {
                        val bmp = BitmapFactory.decodeStream(response.body()!!.byteStream())
                        receiver?.onForecastOnline(forecast, bmp)
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.            }
            }

        })
    }
}