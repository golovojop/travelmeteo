/**
 * Materials:
 * https://square.github.io/picasso/
 * https://developer.android.com/reference/android/graphics/Bitmap.html#createBitmap(android.graphics.Bitmap,%20int,%20int,%20int,%20int)
 *
 * С векторными картинками Picasso не работает
 * https://stackoverflow.com/questions/51798937/picasso-how-to-load-vector-drawable
 *
 */

package k.s.yarlykov.travelmeteo.ui

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import com.squareup.picasso.Transformation

/**
 * В метод прилетают реальные размеры, выделенные для ImageView.
 * Нам нужно чтобы картинка полностью вошла в них по ширине, а
 * всю лишнюю часть сверху обрезаем.
 */
class CropTransformationOld(val viewWidth: Int, val viewHeight: Int) : Transformation {

    override fun transform(source: Bitmap): Bitmap {
        val actualW = Math.min(viewWidth, source.width)
        val actualH = Math.min(viewHeight, source.height)

        val x = 0
        val y = if (viewHeight > source.height) 0 else source.height - viewHeight

        return Bitmap.createBitmap(source, x, y, actualW, actualH).apply {
            if (this !== source) {
                source.recycle()
            }
        }
    }

    override fun key(): String {
        return "resize_${viewWidth}_${viewHeight}"
    }
}

/**
 * https://github.com/wasabeef/picasso-transformations/blob/master/transformations/src/main/java/jp/wasabeef/picasso/transformations/CropTransformation.java
 */
class CropTransformation(val viewWidth: Int, val viewHeight: Int) : Transformation {

    // Сейчас прихо
    override fun transform(source: Bitmap): Bitmap {
        val actualW = Math.min(viewWidth, source.width)
        val actualH = Math.min(viewHeight, source.height)

        val x = 0
        val y = if (source.height > viewHeight) source.height - viewHeight else 0

        val rectSrc = Rect(x, y, x + actualW, y + actualH)
        val rectDest = Rect(0, 0, actualW, actualH)

        val bitmap = Bitmap.createBitmap(actualW, actualH, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawBitmap(source, rectSrc, rectDest, null)

        source.recycle()
        return bitmap
    }

    override fun key(): String {
        return "resize_${viewWidth}_${viewHeight}"
    }
}


// Изменение размера
class ResizeTransformation(val width: Int, val height: Int) : Transformation {
    override fun transform(source: Bitmap?): Bitmap {

        return Bitmap.createBitmap(source!!, 0, 0, width, height).apply {
            if (this !== source) {
                source.recycle()
            }
        }
    }

    override fun key(): String {
        return "resize(${width}, ${height})"
    }
}

// Заглушка
object EmptyTransformation : Transformation {

    override fun transform(source: Bitmap?): Bitmap {
        return source!!
    }

    override fun key(): String {
        return "empty_transform"
    }
}