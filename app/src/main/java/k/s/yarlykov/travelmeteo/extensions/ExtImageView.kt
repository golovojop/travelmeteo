package k.s.yarlykov.travelmeteo.extensions

import android.graphics.Matrix
import android.widget.ImageView
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import k.s.yarlykov.travelmeteo.data.domain.DayPart
import k.s.yarlykov.travelmeteo.ui.CropHorizontalAndEnlargeVertical
import k.s.yarlykov.travelmeteo.ui.ForegroundGradientOverlay

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
 * Загрузка растровой картинки из локальных ресурсов.
 * Picasso не поддерживает работу с векторной графикой, поэтому иконку направления
 * ветра поворачиваем самостоятельно, он векторная.
 */
fun ImageView.loadWithPicasso(resourceId: Int, transformation : Transformation, angle: Float) {
    Picasso
        .get()
        .load(resourceId)
        .transform(transformation)
        .rotate(angle)
        .into(this)
}

fun ImageView.loadAsForecastBackground(resourceId: Int, mode: DayPart, verticalScalling : Float) {
    Picasso
        .get()
        .load(resourceId)
        .transform(CropHorizontalAndEnlargeVertical(0.25f, verticalScalling))
        .transform(ForegroundGradientOverlay(mode))
        .memoryPolicy(MemoryPolicy.NO_CACHE)
        .into(this)
}