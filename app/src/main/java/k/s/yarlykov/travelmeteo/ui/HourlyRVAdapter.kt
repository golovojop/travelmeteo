package k.s.yarlykov.travelmeteo.ui

import android.media.Image
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import k.s.yarlykov.travelmeteo.R
import k.s.yarlykov.travelmeteo.data.sources.openweather.model.hourly.Forecast

class HourlyRVAdapter(val source: List<Forecast>): RecyclerView.Adapter<HourlyRVAdapter.ViewHolder>(){

    // Вызывается когда нужно создать новый элемент списка
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_hourly_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return source.size
    }


    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(source[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDate = itemView.findViewById<TextView>(R.id.tvHourlyDate)
        val tvTemp = itemView.findViewById<TextView>(R.id.tvHourlyTemp)
        val ivIcon = itemView.findViewById<ImageView>(R.id.ivHourlyIcon)

        fun bind(f: Forecast) = with(f) {
            tvDate.text = f.dtTxt
        }
    }
}