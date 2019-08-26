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
import com.squareup.picasso.Transformation

// Изменение размера
class ResizeTransformation(val width: Int, val height : Int) : Transformation {
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