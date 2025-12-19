package com.example.colorpicker.util

import android.graphics.Bitmap

object ImageUtils {

    /**
     * Menghitung nilai mean RGB dari seluruh bitmap
     * TANPA crop, TANPA resize, TANPA ubah orientasi
     */
    fun calculateMeanRGB(bitmap: Bitmap): Triple<Int, Int, Int> {

        val width = bitmap.width
        val height = bitmap.height

        var sumR = 0L
        var sumG = 0L
        var sumB = 0L
        val totalPixels = width * height

        val pixels = IntArray(totalPixels)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        for (pixel in pixels) {
            sumR += (pixel shr 16) and 0xFF
            sumG += (pixel shr 8) and 0xFF
            sumB += pixel and 0xFF
        }

        val meanR = (sumR / totalPixels).toInt()
        val meanG = (sumG / totalPixels).toInt()
        val meanB = (sumB / totalPixels).toInt()

        return Triple(meanR, meanG, meanB)
    }
}
