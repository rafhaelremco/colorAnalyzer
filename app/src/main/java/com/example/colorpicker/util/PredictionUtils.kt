package com.example.colorpicker.util

import com.example.colorpicker.data.CalibrationEntry

object PredictionUtils {

    data class LinearModel(
        val a: Double,
        val b: Double,
        val rControl: Double
    )

    /**
     * Bangun model regresi linear berbasis ΔR
     * y = a * ΔR + b
     * ΔR = R_sampel - R_kontrol
     */
    fun buildDeltaRModel(
        data: List<CalibrationEntry>
    ): LinearModel {

        require(data.size >= 2) {
            "Minimal 2 data kalibrasi diperlukan"
        }

        // R_kontrol diasumsikan sebagai R terendah (kontrol negatif)
        val rControl = data.minOf { it.r }.toDouble()

        val n = data.size
        var sumX = 0.0
        var sumY = 0.0
        var sumXY = 0.0
        var sumX2 = 0.0

        for (d in data) {
            val deltaR = d.r - rControl
            val y = d.concentration

            sumX += deltaR
            sumY += y
            sumXY += deltaR * y
            sumX2 += deltaR * deltaR
        }

        val a =
            (n * sumXY - sumX * sumY) /
                    (n * sumX2 - sumX * sumX)

        val b =
            (sumY - a * sumX) / n

        return LinearModel(
            a = a,
            b = b,
            rControl = rControl
        )
    }

    /**
     * Prediksi konsentrasi berbasis ΔR
     */
    fun predict(
        model: LinearModel,
        rSample: Int
    ): Double {
        val deltaR = rSample - model.rControl
        return model.a * deltaR + model.b
    }
}
