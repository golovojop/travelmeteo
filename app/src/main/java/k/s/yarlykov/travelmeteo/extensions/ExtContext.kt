package k.s.yarlykov.travelmeteo.extensions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.util.DisplayMetrics
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat

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
        Bitmap.Config.ARGB_8888
    ) ?: return null

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

fun Context.screenRatioHeight(ratio: Float): Int =
    (this.resources.displayMetrics.heightPixels.toFloat() * Math.abs(ratio)).toInt()

fun Context.screenRatioWidth(ratio: Float): Int =
    (this.resources.displayMetrics.widthPixels.toFloat() * Math.abs(ratio)).toInt()

// Получить Resource Id ресурса картинки по имени файла (без расшипения)
fun Context.iconId(picName: String): Int =
    resources.getIdentifier(picName, "drawable", this.packageName)
