package k.s.yarlykov.travelmeteo.data.sources.unifiedprovider

interface ForecastProvider {
    fun requestForecastCurrent(consumer: ForecastConsumer?, lat: String, lon: String)
    fun requestForecastHourly(consumer: ForecastConsumer?, lat: String, lon: String)
}