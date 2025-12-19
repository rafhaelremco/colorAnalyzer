package com.example.colorpicker.ui

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.colorpicker.R
import com.example.colorpicker.data.AppDatabase
import com.example.colorpicker.util.ImageUtils
import com.example.colorpicker.util.PredictionUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PredictionActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var tvResult: TextView

    private val imagePicker =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                processImage(uri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prediction)

        imageView = findViewById(R.id.imagePreview)
        tvResult = findViewById(R.id.tvResult)

        findViewById<Button>(R.id.btnPickImage).setOnClickListener {
            imagePicker.launch("image/*")
        }
    }

    private fun processImage(uri: Uri) {
        lifecycleScope.launch(Dispatchers.IO) {

            // 1) Ambil data kalibrasi
            val calibrationData = AppDatabase
                .getInstance(applicationContext)
                .calibrationDao()
                .getAll()

            if (calibrationData.size < 2) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@PredictionActivity,
                        "Minimal 2 data kalibrasi diperlukan",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return@launch
            }

            // 2) Bangun model ΔR
            val model = PredictionUtils.buildDeltaRModel(calibrationData)

            // 3) Decode bitmap sampel
            val inputStream = contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            // 4) Ambil RGB, gunakan R saja
            val (r, _, _) = ImageUtils.calculateMeanRGB(bitmap)

            // 5) Hitung ΔR & prediksi
            val deltaR = r - model.rControl
            val prediction = PredictionUtils.predict(model, r)

            // 6) Tampilkan hasil
            withContext(Dispatchers.Main) {
                imageView.setImageBitmap(bitmap)
                tvResult.text =
                    "R sampel      : $r\n" +
                            "R kontrol     : ${model.rControl.toInt()}\n" +
                            "ΔR            : ${deltaR.toInt()}\n\n" +
                            "Prediksi Konsentrasi:\n" +
                            String.format("%.3f", prediction)
            }
        }
    }
}
