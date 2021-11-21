package com.example.gagyeboost.common

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable

object BitmapUtils {
    fun createBitmapFromDrawable(drawable: Drawable): Bitmap {
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }
}
