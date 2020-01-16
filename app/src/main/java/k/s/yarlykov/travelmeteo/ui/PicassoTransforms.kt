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

import android.graphics.*
import com.squareup.picasso.Transformation
import k.s.yarlykov.travelmeteo.data.domain.DayPart

/**
 * Из исходной картинки берется нижняя часть высотой (cropRatio * source.height).
 * Создается новая bitmap высотой enlargeRatio * source.height) и вырезанная нижняя часть копируется
 * в нижнюю часть новой bitmap. Ширина не изменятеся.
 */
class CropHorizontalAndEnlargeVertical(val cropRatio: Float, val enlargeRatio: Float) : Transformation {

    override fun transform(source: Bitmap): Bitmap {
        // Ширина и высота вырезаемой области
        val cropW = source.width
        var cropH = (source.height.toFloat() * cropRatio).toInt()

        // Вырезаемый прямоугольник.
        val xSrc = 0
        val ySrc = source.height - cropH
        val rectSrc = Rect(xSrc, ySrc, cropW, source.height)

        // Целевой прямоугольник в будущей bitmap
        val heightDst = (cropH.toFloat() * enlargeRatio).toInt()
        val xDst = 0
        val yDst = heightDst - cropH
        val rectDest = Rect(xDst, yDst, cropW, heightDst)

        // Новая bitmap, в которую скопируем прямоугольних из исходной bitmap
        val bitmapDst: Bitmap = Bitmap.createBitmap(cropW, heightDst, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmapDst)
        canvas.drawBitmap(source, rectSrc, rectDest, null)

        source.recycle()
        return bitmapDst
    }

    override fun key(): String {
        return "crop_horizontal_${cropRatio}"
    }
}

/**
 * Разместить поверх контента градиентную заливку с градиентной прозрачностью
 */
class ForegroundGradientOverlay(val mode: DayPart) : Transformation {

    class Palette(vararg val colors: Long) {
        fun getPallete(): IntArray = colors.map { c -> c.toInt() }.toIntArray()
    }

    private val palettes: HashMap<DayPart, Palette> = hashMapOf(
        DayPart.SUNRISE to Palette(0xffbd5270, 0x40d7807c),
        DayPart.DAY to Palette(0xFF097BE6, 0xE01D99F1, 0x402FB4FA),
        DayPart.NIGHT to Palette(0xff0e2d5c, 0x402FB4FA),
        // Цвета пока не выбраны
        DayPart.SUNSET to Palette(0xffbd5270, 0x40d7807c))

    override fun transform(source: Bitmap?): Bitmap {

        // Проверка аргумента на случай если добавится новый элемент в enum
        val colors = if(palettes.containsKey(mode)) {
            palettes[mode]!!.getPallete()
        } else {
            Palette(0xFF097BE6, 0xE01D99F1, 0x402FB4FA).getPallete()
        }

        val canvas = Canvas(source!!)

        with(Paint()) {
            shader = LinearGradient(
                0f,
                0f,
                0f,
                source.height.toFloat(),
                colors,
                null,
                Shader.TileMode.MIRROR)
            canvas.drawPaint(this)
        }

        return source
    }

    override fun key(): String {
        return "ForegroundGradientOverlay_${hashCode()}"
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