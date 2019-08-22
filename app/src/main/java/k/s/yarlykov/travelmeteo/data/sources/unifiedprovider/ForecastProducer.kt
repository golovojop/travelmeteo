package k.s.yarlykov.travelmeteo.data.sources.unifiedprovider

interface ForecastProducer {
    fun requestForecastCurrent(consumer: ForecastConsumer?, lat: String, lon: String)
    fun requestForecastHourly(consumer: ForecastConsumer?, lat: String, lon: String)
}