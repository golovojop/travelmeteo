package k.s.yarlykov.travelmeteo.extensions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.util.DisplayMetrics
import android.widget.ImageView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import k.s.yarlykov.travelmeteo.data.domain.CustomForecast
import k.s.yarlykov.travelmeteo.data.sources.openweather.model.hourly.Forecast
import java.text.SimpleDateFormat
import java.util.*

/**
 * MutableList <CustomForecast>
 */
fun MutableList<CustomForecast>.initFromModel(li: List<CustomForecast>) {
    // Из исходного массива возьмём не более QTY элементов
    val QTY = 12
    this.clear()

    for ((index, value) in li.withIndex()) {
        if (index < QTY) {
            this.add(index, value)
        } else {
            break
        }
    }
}

/**
 * MutableList <Marker>
 */
fun MutableList<Marker>.deleteAll() {
    this.forEach(Marker::remove)
    this.clear()
}

/**
 * Date
 *
 * Materials:
 * https://kodejava.org/how-do-i-get-timezone-ids-by-their-milliseconds-offset/
 * http://tutorials.jenkov.com/java-date-time/java-util-timezone.html
 * https://stackoverflow.com/questions/7670355/convert-date-time-for-given-timezone-java
 */
fun Date.format(format: String, offset: Int): String {
    // Получить ID Time-зоны для данного offset'а
    val zoneId = TimeZone.getAvailableIDs(offset * 1000).asList()[0]
    val timeZone = TimeZone.getTimeZone(zoneId)

    val sdf = SimpleDateFormat(format, Locale.getDefault())
    sdf.timeZone = timeZone

    return sdf.format(this)
}

// Тот же код, но в более извращенной форме )
fun Date.formatHHmm(offset: Int) = SimpleDateFormat("HH:mm", Locale.getDefault()).apply {
    val zoneId = TimeZone.getAvailableIDs(offset * 1000).asList()[0]
    this.timeZone = TimeZone.getTimeZone(zoneId)
}.format(this)

/**
 * LatLng
 */
fun LatLng.lat(): String {
    // Для того, чтобы в результирующей строке в качестве разделителя
    // целой и дробной части была точка, а не запятая, используется
    // аргумент Locale.ROOT.
    // На телефоне с русской локалью как раз используется запятая
    // и из-за этого неправильно формировались координаты для GET
    // запроса и прога глючила.
    return String.format(Locale.ROOT, "%.6f", this.latitude)
}

fun LatLng.lon(): String {
    // Тоже самое ->> Locale.ROOT
    return String.format(Locale.ROOT, "%.6f", this.longitude)
}

/**
 * ImageView
 *
 * Materials:
 * https://stackoverflow.com/questions/8981845/android-rotate-image-in-imageview-by-an-angle
 *
 * Есть проблемы при первом показе картинки. Лучше поворачивать bitmap, а потом устанавливать её
 * в ImageView (см метод Bitmap.rotate ниже)
 *
 */
fun ImageView.rotate(angle: Float) {
    val matrix = Matrix()
    matrix.postRotate(angle,
        this.drawable.bounds.width()/2.toFloat(),
        this.drawable.bounds.height()/2.toFloat())

    this.scaleType = ImageView.ScaleType.MATRIX
    this.imageMatrix = matrix
}

/**
 * Context
 *
 * Materials:
 * https://stackoverflow.com/questions/33696488/getting-bitmap-from-vector-drawable
 */

fun Context.bitmapFromVectorDrawable(drawableId: Int, dpW: Int, dpH: Int): Bitmap? {
    var drawable = ContextCompat.getDrawable(this, drawableId) ?: return null

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
        drawable = DrawableCompat.wrap(drawable).mutate()
    }

    val bitmap = Bitmap.createBitmap(
        dpToPix(dpW.toFloat()), dpToPix(dpH.toFloat()),
        Bitmap.Config.ARGB_8888) ?: return null

    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)

    return bitmap
}

// Конвертируем dip to pixels
fun Context.dpToPix(dp: Float): Int {
    val displayMetrics = this.resources.displayMetrics
    return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
}

/**
 * Bitmap
 *
 * Materials:
 * https://stackoverflow.com/questions/9015372/how-to-rotate-a-bitmap-90-degrees
 *
 * Поворачиваем bitmap на заданный угол
 */
fun Bitmap.rotate(degrees: Float): Bitmap {
    val matrix = Matrix().apply { postRotate(degrees) }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}



