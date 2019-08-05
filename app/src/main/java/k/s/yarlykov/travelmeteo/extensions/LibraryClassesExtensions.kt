package k.s.yarlykov.travelmeteo.extensions

import k.s.yarlykov.travelmeteo.data.sources.openweather.model.hourly.Forecast

fun MutableList<Forecast>.fromList(li: List<Forecast>) {
    clear()
    this.addAll(li)
}