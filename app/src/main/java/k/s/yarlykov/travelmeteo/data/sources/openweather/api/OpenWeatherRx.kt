/**
 * Materials:
 * https://medium.com/3xplore/handling-api-calls-using-retrofit-2-and-rxjava-2-1871c891b6ae
 */
package k.s.yarlykov.travelmeteo.data.sources.openweather.api


import io.reactivex.Observable
import k.s.yarlykov.travelmeteo.data.sources.openweather.model.current.WeatherResponseModel
import k.s.yarlykov.travelmeteo.data.sources.openweather.model.hourly.OpenWeatherHourlyResponseModel
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url


interface OpenWeatherRx {
    @GET("data/2.5/weather")
    fun loadWeather(@Query("q") city: String,
                    @Query("appid") keyApi: String,
                    @Query("units") units: String): Observable<WeatherResponseModel>

    @GET("data/2.5/weather")
    fun loadGeoWeatherCurrent(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("units") units: String,
        @Query("appid") keyApi: String): Observable<WeatherResponseModel>

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
        @Query("appid") keyApi: String): Observable<OpenWeatherHourlyResponseModel>

    @GET
    fun fetchIcon(@Url url: String): Observable<ResponseBody>
}