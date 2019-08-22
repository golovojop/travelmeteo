package k.s.yarlykov.travelmeteo.data.sources.openweather.api

import k.s.yarlykov.travelmeteo.data.sources.openweather.model.current.WeatherResponseModel
import k.s.yarlykov.travelmeteo.data.sources.openweather.model.hourly.OpenWeatherHourlyResponseModel
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface OpenWeather {
    @GET("data/2.5/weather")
    fun loadWeather(@Query("q") city: String,
                    @Query("appid") keyApi: String,
                    @Query("units") units: String): Call<WeatherResponseModel>

    @GET("data/2.5/weather")
    fun loadGeoWeatherCurrent(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("units") units: String,
        @Query("appid") keyApi: String): Call<WeatherResponseModel>

    /**
     * Free subscription
     * https://openweathermap.org/forecast5
     *
     * Correct request:
     * https://api.openweathermap.org/data/2.5/forecast?lat=35&lon=139&units=metric&appid=b7838252ab3cde579f376d9417c878d1
     */
    @GET("data/2.5/forecast")
    fun loadGeoWeatherHourly(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("units") units: String,
        @Query("appid") keyApi: String): Call<OpenWeatherHourlyResponseModel>

    @GET
    fun fetchIcon(@Url url: String): Call<ResponseBody>
}