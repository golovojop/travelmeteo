package k.s.yarlykov.travelmeteo.data.sources.openweather.api

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import k.s.yarlykov.travelmeteo.data.domain.CityForecast
import k.s.yarlykov.travelmeteo.data.sources.openweather.model.current.WeatherResponseModel
import k.s.yarlykov.travelmeteo.data.sources.openweather.model.hourly.HourlyWeatherResponseModel
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
        fun onForecastCurrent(model: WeatherResponseModel, icon: Bitmap)
        fun onForecastHourly(model: HourlyWeatherResponseModel)
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

    fun requestForecastCurrent(receiver: ForecastReceiver?, lat: Int, lon: Int) {
        api.loadGeoWeatherCurrent(lat, lon, apiUnits, apiKey).enqueue(object : Callback<WeatherResponseModel> {

            override fun onResponse(call: Call<WeatherResponseModel>, response: Response<WeatherResponseModel>) {

                // Code 404: body() == null; isSuccessful() == false
                if (response.isSuccessful()) {
                    response.body()?.let {
                        requestIcon(receiver, it)
                    }
                }
            }

            override fun onFailure(call: Call<WeatherResponseModel>, t: Throwable) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }

    fun requestForecastHourly(receiver: ForecastReceiver?, lat: Int, lon: Int) {
        api.loadGeoWeatherHourly(lat, lon, apiUnits, apiKey).enqueue(object : Callback<HourlyWeatherResponseModel> {

            override fun onResponse(
                call: Call<HourlyWeatherResponseModel>,
                response: Response<HourlyWeatherResponseModel>
            ) {

                // Code 404: body() == null; isSuccessful() == false
                if (response.isSuccessful) {
                    response.body()?.let {
                        receiver?.onForecastHourly(it)
                    }
                }
            }

            override fun onFailure(call: Call<HourlyWeatherResponseModel>, t: Throwable) {
                Log.d("QWM_ERROR", t.message)
            }
        })
    }


    // Получить иконку погоды
    private fun requestIcon(receiver: ForecastReceiver?, model: WeatherResponseModel) {

        val call = api.fetchIcon("https://openweathermap.org/img/w/${model.weather!![0].icon!!}.png")

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                if (response.isSuccessful) {
                    response.body()?.let {
                        val bmp = BitmapFactory.decodeStream(response.body()!!.byteStream())
                        receiver?.onForecastCurrent(model, bmp)
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.            }
            }
        })
    }
}