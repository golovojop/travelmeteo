/**
 * Materials:
 * RecycleView with header^
 * https://stackoverflow.com/questions/26530685/is-there-an-addheaderview-equivalent-for-recyclerview
 */

package k.s.yarlykov.travelmeteo.ui

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Transformation
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import k.s.yarlykov.travelmeteo.R
import k.s.yarlykov.travelmeteo.data.domain.*
import k.s.yarlykov.travelmeteo.extensions.*

class HourlyRVAdapter(private val source: MutableList<CustomForecast>, val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // Загрузить массивы с именами сторон света и значениями углов направления ветра
    val directionNames: List<String> = context.resources.getStringArray(R.array.compass_directions).asList()
    val directionAngles: IntArray = context.resources.getIntArray(R.array.compass_directions_angles)

    // Размеры указателя направления ветра в dip
    val dipWindW = 20
    val dipWindH = 20

    // Исходный bitmap для иконки направления ветра (стрелка вверх).
    val windBitmap = context.bitmapFromVectorDrawable(R.drawable.ic_wind_direction_white, dipWindW, dipWindH)

    // Позволяет адаптеру самостоятельно устанавливать типы для отдельных элементов
    // списка RV. Нужно в том случае, если они, например, имеют разные макеты.
    // Как в данном случае. Потом этот тип используется в onCreateViewHolder
    override fun getItemViewType(position: Int): Int {
        return if (position == 0) TYPE_HEADER else TYPE_ITEM
    }

    // Вызывается когда нужно создать новый элемент списка
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            TYPE_HEADER -> {
                ViewHolderHeader(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.layout_hourly_header,
                        parent,
                        false
                    )
                )
            }
            else -> {
                ViewHolderItem(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.layout_hourly_item,
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {

        if (source.size == 0) return

        when (viewHolder) {
            // Если прилетел холдер для заголовка, то в него передать данные
            // из первого элемента массива - текущий прогноз
            is ViewHolderHeader -> viewHolder.bind(source[0])
            // Холдеры дугих типов получают данные из источника
            // согласно позиции
            is ViewHolderItem -> viewHolder.bind(source[position - 1])
        }
    }

    override fun getItemCount(): Int {
        return source.size
    }

    inner class ViewHolderItem(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDate = itemView.findViewById<TextView>(R.id.tvHourlyDate)
        val tvTemp = itemView.findViewById<TextView>(R.id.tvHourlyTemp)
        val ivIcon = itemView.findViewById<ImageView>(R.id.ivHourlyIcon)

        fun bind(f: CustomForecast) = with(f) {
            tvDate.text = this.time
            tvTemp.text = this.celsius(this.temp)
            ivIcon.loadWithPicasso(context.iconId(this.icon), EmptyTransformation,0f)
        }
    }

    inner class ViewHolderHeader(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvWind = itemView.findViewById<TextView>(R.id.tvWindValue)
        val tvWindDir = itemView.findViewById<TextView>(R.id.tvWindDirection)
        val tvPressure = itemView.findViewById<TextView>(R.id.tvPressureValue)
        val tvHumidity = itemView.findViewById<TextView>(R.id.tvHumidityValue)
        val ivWindDir = itemView.findViewById<ImageView>(R.id.ivWindDirection)

        fun bind(f: CustomForecast) = with(f) {

            // Преобразовать курс ветра в его ID (также будет индексом в массивах значений)
            val windId = this.windDegToId()

            // Concatenate before usage
            val windDirection = ", ${directionNames[windId]}"

            tvWind.text = "${this.wind_speed}"
            tvWindDir.text = windDirection
            tvPressure.text = "${this.toMmHg()}"
            tvHumidity.text = "${this.humidity}"
            // Исходный bitmap поворачиваем и устанавливаем
            // в качестве bitmap'а элемента ImageView
            ivWindDir.setImageBitmap(windBitmap?.rotate(directionAngles[windId].toFloat()))
        }
    }

    companion object {
        const val TYPE_HEADER = 0
        const val TYPE_ITEM = 1
    }
}