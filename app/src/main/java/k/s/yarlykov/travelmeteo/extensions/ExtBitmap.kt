package k.s.yarlykov.travelmeteo.extensions

import android.graphics.Bitmap
import android.graphics.Matrix

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