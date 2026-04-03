package com.example.testfilterimage

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import androidx.core.graphics.createBitmap
import coil3.size.Size
import coil3.transform.Transformation
import kotlinx.coroutines.delay

class ImageTransformation(
    private val filter: Filter
) : Transformation() {

    override val cacheKey: String =
        "${this::class.qualifiedName}-${filter.name}-${filter.colorInt}"

    override suspend fun transform(input: Bitmap, size: Size): Bitmap {
        val output = createBitmap(
            width = input.width,
            height = input.height,
            config = input.config ?: Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(output)

        canvas.drawBitmap(input, 0f, 0f, null)

        val overlayPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            colorFilter = PorterDuffColorFilter(filter.colorInt, PorterDuff.Mode.SRC_ATOP)
            alpha = 160
        }
        canvas.drawBitmap(input, 0f, 0f, overlayPaint)

        delay(3000)
        return output
    }
}
